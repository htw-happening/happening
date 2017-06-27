package blue.happening.mesh;

class Router {

    private RoutingTable routingTable;
    private String uuid;

    Router(RoutingTable routingTable, String uuid) {
        this.routingTable = routingTable;
        this.uuid = uuid;
    }

    /**
     * @param message Message to be routed
     * @return Only return message if it is for this device and
     * it should be forwarded to client apps
     * @throws RoutingException Caused by invalid message header
     */
    Message routeMessage(Message message) throws RoutingException {
        if (!isEchoOGM(message)) {
            routingTable.ensureConnection(message.getSource(), message.getPreviousHop());
        }
        if (message.getType() == MeshHandler.MESSAGE_TYPE_OGM) {
            slideWindows(message);
            routeOgm(message);
            return null;
        } else if (message.getType() == MeshHandler.MESSAGE_TYPE_UCM) {
            return routeUcm(message);
        } else {
            throw new RoutingException("Unknown message type");
        }
    }

    private void slideWindows(Message message) throws RoutingException {
        SlidingWindow window;
        if (isEchoOGM(message)) {
            RemoteDevice previous = routingTable.get(message.getPreviousHop());
            if (previous == null)
                throw new RoutingException("slideWindows: Previous hop has left " + message.getPreviousHop());
            window = previous.getEchoSlidingWindow();
        } else {
            RemoteDevice source = routingTable.get(message.getSource());
            if (source == null)
                throw new RoutingException("slideWindows: Message source has left " + message.getSource());
            window = source.getReceiveSlidingWindow();
        }
        if (window != null) {
            window.slideSequence(message.getSequence());
            window.addIfIsSequenceInWindow(message);
        }
    }

    private void routeOgm(Message message) throws RoutingException {
        RemoteDevice existingDevice = routingTable.get(message.getSource());
        if (existingDevice != null) {
            if (message.getDestination().equals(MeshHandler.BROADCAST_ADDRESS)) {
                if (shouldOGMBeForwarded(message)) {
                    broadcastMessage(message);
                }
            } else {
                throw new RoutingException("OGM needs broadcast destination");
            }
        }
    }

    private Message routeUcm(Message message) throws RoutingException {
        if (message.getDestination().equals(MeshHandler.BROADCAST_ADDRESS)) {
            throw new RoutingException("Cannot broadcast UPC");
        } else if (message.getDestination().equals(uuid)) {
            System.out.println("MESSAGE RECEIVED: " + message);
            return message;
        } else {
            System.out.println("MESSAGE FORWARDED: " + message);
            forwardMessage(message);
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
        return window.getSequence() == null || window.getSequence() == message.getSequence();
    }

    private boolean shouldOGMBeForwarded(Message message) {
        if (isEchoOGM(message)) {
            System.out.println("DROP ECHO OGM: " + message);
            return false;
        } else if (isNeighbourOGM(message)) {
            System.out.println("BROADCAST NEIGHBOUR OGM: " + message);
            return true;
        } else if (!isMessageVital(message)) {
            System.out.println("DROP NOT VITAL OGM: " + message);
            return false;
        } else if (!slidingWindowSaysYes(message)) {
            System.out.println("DROP IN WINDOW OGM: " + message);
            return false;
        } else {
            System.out.println("BROADCAST VITAL OGM: " + message);
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

    private int calculateTq(Message message) throws RoutingException {
        RemoteDevice previousHop = routingTable.get(message.getPreviousHop());
        float previousTq = 0;
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
        preparedMessage.setTq(calculateTq(message));
        preparedMessage.setPreviousHop(uuid);
        preparedMessage.setTtl(message.getTtl() - 1);
        return preparedMessage;
    }

    private void forwardMessage(Message message) throws RoutingException {
        Message preparedMessage = prepareMessage(message);
        RemoteDevice destination = routingTable.get(message.getDestination());
        RemoteDevice bestNeighbour = routingTable.getBestNeighbourForRemoteDevice(destination);
        if (bestNeighbour != null) {
            bestNeighbour.sendMessage(preparedMessage);
        }
    }

    private void broadcastMessage(Message message) throws RoutingException {
        Message preparedMessage = prepareMessage(message);
        for (RemoteDevice remoteDevice : routingTable.getNeighbours()) {
            if (shouldOGMBeEchoedTo(message, remoteDevice.getUuid()) ||
                    shouldOGMBeBroadcastTo(message, remoteDevice.getUuid())) {
                remoteDevice.sendMessage(preparedMessage);
            }
        }
    }

    class RoutingException extends Exception {
        RoutingException(String message) {
            super(message);
        }
    }
}