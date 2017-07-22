package blue.happening.mesh;

import java.util.Observable;
import java.util.concurrent.ThreadLocalRandom;

class Router extends Observable {

    static final int OGM_SENT = 1;
    static final int UCM_SENT = 2;
    static final int OGM_DROPPED = 3;
    static final int UCM_DROPPED = 4;

    private RoutingTable routingTable;
    private String uuid;
    private int sequence;

    Router(RoutingTable routingTable, String uuid) {
        this.routingTable = routingTable;
        this.uuid = uuid;
        sequence = ThreadLocalRandom.current().nextInt(MeshHandler.INITIAL_MIN_SEQUENCE, MeshHandler.INITIAL_MAX_SEQUENCE);
    }

    /**
     * @param message Message to be routed
     * @return Only return message if it is for this device and
     * it should be forwarded to client apps
     * @throws RoutingException Caused by invalid message header
     */
    Message routeMessage(Message message) throws RoutingException {
        if (message.getType() == MeshHandler.MESSAGE_TYPE_OGM) {
            if (!isEchoOGM(message)) {
                routingTable.putRoute(message.getSource(), message.getPreviousHop());
            }
            routeOgm(message);
            return null;
        } else if (message.getType() == MeshHandler.MESSAGE_TYPE_UCM) {
            return routeUcm(message);
        } else {
            throw new RoutingException("Unknown message type");
        }
    }

    private void slideWindows(Message message) throws RoutingException {
        if (isEchoOGM(message)) {
            RemoteDevice previous = routingTable.get(message.getPreviousHop());
            if (previous == null) {
                throw new RoutingException("slideWindows: Previous hop has left " + message.getPreviousHop());
            }
            previous.getEchoSlidingWindow().slideAndAddSequence(message.getSequence());
        } else {
            RemoteDevice source = routingTable.get(message.getSource());
            if (source == null) {
                throw new RoutingException("slideWindows: Message source has left " + message.getSource());
            }
            source.getReceiveSlidingWindow().slideAndAddSequence(message.getSequence());
        }
    }

    private void routeOgm(Message message) throws RoutingException {
        if (message.getDestination().equals(MeshHandler.BROADCAST_ADDRESS)) {
            if (shouldOGMBeForwarded(message)) {
                broadcastOGM(message);
            } else {
                trigger(OGM_DROPPED, message);
            }
        } else {
            throw new RoutingException("OGM needs broadcast destination");
        }
        slideWindows(message);
    }

    void dispatchOgm() throws RoutingException {
        Message message = new Message(uuid, MeshHandler.BROADCAST_ADDRESS, sequence, MeshHandler.MESSAGE_TYPE_OGM, null);
        boolean ogmSent = false;
        for (RemoteDevice remoteDevice : routingTable.getNeighbours()) {
            remoteDevice.sendMessage(message);
            remoteDevice.getEchoSlidingWindow().slideSequence(sequence);
            ogmSent = true;
            trigger(OGM_SENT, message);
        }
        if (!ogmSent) {
            trigger(OGM_DROPPED, message);
        }
        sequence++;
    }

    /**
     * @param message Unicast message to be routed
     * @return Returns UCM if current device is destination
     * @throws RoutingException If someone tried to broadcast a UCM
     */
    private Message routeUcm(Message message) throws RoutingException {
        if (message.getDestination().equals(MeshHandler.BROADCAST_ADDRESS)) {
            throw new RoutingException("Cannot broadcast UPC");
        } else if (message.getDestination().equals(uuid)) {
            return message;
        } else {
            forwardUCM(message);
            return null;
        }
    }

    private boolean isEchoOGM(Message message) {
        return message.getSource().equals(uuid);
    }

    private boolean isNeighbourOGM(Message message) {
        return message.getSource().equals(message.getPreviousHop());
    }

    private boolean isMessageVital(Message message) {
        return message.getTq() > MeshHandler.HOP_PENALTY && message.getTtl() > 1;
    }

    private boolean slidingWindowSaysYes(Message message) {
        SlidingWindow window = routingTable.get(message.getSource()).getReceiveSlidingWindow();
        return window.isSequenceOutOfWindow(message.getSequence());
    }

    private boolean shouldOGMBeForwarded(Message message) {
        if (isEchoOGM(message)) {
            // Drop echo OGM
            return false;
        } else if (isNeighbourOGM(message)) {
            // Broadcast neighbour OGM
            return true;
        } else if (!isMessageVital(message)) {
            // Drop not vital OGM
            return false;
        } else if (!slidingWindowSaysYes(message)) {
            // Drop in window OGM
            return false;
        } else {
            // Broadcast vital OGM
            return true;
        }
    }

    private boolean shouldOGMBeEchoedTo(Message message, String receiverUuid) {
        if (!message.getSource().equals(receiverUuid)) {
            return false;
        } else if (message.getPreviousHop().equals(receiverUuid)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean shouldOGMBeBroadcastTo(Message message, String receiverUuid) {
        if (message.getSource().equals(receiverUuid)) {
            return false;
        } else if (message.getPreviousHop().equals(receiverUuid)) {
            return false;
        } else {
            return true;
        }
    }

    private boolean shouldUCMBeForwardedTo(Message message, String receiverUuid) {
        if (message.getSource().equals(receiverUuid)) {
            return false;
        } else if (message.getPreviousHop().equals(receiverUuid)) {
            return false;
        } else {
            return true;
        }
    }

    private int calculateTq(Message message) throws RoutingException {
        RemoteDevice previousHop = routingTable.get(message.getPreviousHop());
        float previousTq;
        if (previousHop != null) {
            previousTq = previousHop.getTq();
        } else {
            throw new RoutingException("calculateTq: Previous hop has left " + message.getPreviousHop());
        }
        return (int) (message.getTq() * previousTq) - MeshHandler.HOP_PENALTY;
    }

    private Message prepareMessage(Message message) throws RoutingException {
        Message preparedMessage = new Message(
                message.getSource(),
                message.getDestination(),
                message.getSequence(),
                message.getType(),
                message.getBody()
        );
        if (message.getType() == MeshHandler.MESSAGE_TYPE_OGM) {
            preparedMessage.setTq(calculateTq(message));
        }
        preparedMessage.setTtl(message.getTtl() - 1);
        preparedMessage.setPreviousHop(uuid);
        preparedMessage.setUuid(message.getUuid());
        return preparedMessage;
    }

    private void forwardUCM(Message message) throws RoutingException {
        Message preparedMessage = prepareMessage(message);
        RemoteDevice destination = routingTable.get(message.getDestination());
        for (Route route : routingTable.getBestRoutesTo(destination)) {
            if (shouldUCMBeForwardedTo(message, route.getViaDevice())) {
                RemoteDevice viaDevice = routingTable.get(route.getViaDevice());
                viaDevice.sendMessage(preparedMessage);
                trigger(UCM_SENT, preparedMessage);
                return;
            }
        }
        trigger(UCM_DROPPED, preparedMessage);
    }

    private void broadcastOGM(Message message) throws RoutingException {
        Message preparedMessage = prepareMessage(message);
        boolean ogmSent = false;
        for (RemoteDevice remoteDevice : routingTable.getNeighbours()) {
            if (shouldOGMBeEchoedTo(message, remoteDevice.getUuid()) ||
                    shouldOGMBeBroadcastTo(message, remoteDevice.getUuid())) {
                remoteDevice.sendMessage(preparedMessage);
                trigger(OGM_SENT, preparedMessage);
                ogmSent = true;
            }
        }
        if (!ogmSent) {
            trigger(OGM_DROPPED, preparedMessage);
        }
    }

    class RoutingException extends Exception {
        RoutingException(String message) {
            super(message);
        }
    }

    private void trigger(int type, Object options) {
        setChanged();
        notifyObservers(new Event(type, options));
    }

    class Event {
        private int type;
        private Object options;

        Event(int type, Object options) {
            this.type = type;
            this.options = options;
        }

        int getType() {
            return type;
        }

        Object getOptions() {
            return options;
        }
    }
}