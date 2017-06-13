package blue.happening.mesh;

import org.apache.log4j.Logger;

class Router {

    private static Logger logger = Logger.getLogger(Router.class);
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
        routingTable.ensureConnection(message.getSource(), message.getPreviousHop());

        if (message.getType() == Message.MESSAGE_TYPE_OGM) {
            routeOgm(message);
            return null;
        } else if (message.getType() == Message.MESSAGE_TYPE_UCM) {
            return routeUcm(message);
        } else {
            throw new RoutingException("Unknown message type");
        }
    }

    private void routeOgm(Message message) throws RoutingException {
        RemoteDevice existingDevice = routingTable.get(message.getSource());
        if (existingDevice != null) {
            SlidingWindow window = existingDevice.getSlidingWindow();

            if (message.getDestination().equals(MeshHandler.BROADCAST_ADDRESS)) {
                window.addIfIsSequenceInWindow(message.getSequence());
                if (shouldMessageBeForwarded(message)) {
                    logger.debug(uuid + " OGM BROADCAST: " + message);
                    window.slideSequence(message.getSequence());
                    broadcastMessage(message);
                } else {
                    // Message is dropped
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
            logger.debug("MESSAGE RECEIVED: " + message);
            return message;
        } else {
            logger.debug("MESSAGE FORWARDED: " + message);
            forwardMessage(message);
            return null;
        }
    }

    private boolean isMyMessage(Message message) {
        return message.getSource().equals(uuid);
    }

    private boolean sourceIsNeighbour(Message message) {
        return message.getSource().equals(message.getPreviousHop());
    }

    private boolean isMessageVital(Message message) {
        return message.getTq() > MeshHandler.HOP_PENALTY && message.getTtl() > 1;
    }

    private boolean slidingWindowSaysYes(Message message) {
        SlidingWindow window = routingTable.get(message.getSource()).getSlidingWindow();
        return window.isSequenceOutOfWindow(message.getSequence());
    }

    private boolean shouldMessageBeForwarded(Message message) {
        if (sourceIsNeighbour(message)) {
            return true;
        } else if (isMyMessage(message)) {
            logger.debug(uuid + " OGM WAS MINE: " + message);
            return false;
        } else {
            if (!isMessageVital(message)) {
                logger.debug(uuid + " OGM NOT VITAL: " + message);
                return false;
            } else if (!slidingWindowSaysYes(message)) {
                logger.debug(uuid + " OGM IN WINDOW: " + message);
                return false;
            }
            return true;
        }
    }

    private void prepareMessage(Message message) {
        message.setPreviousHop(uuid);
        message.setTtl(message.getTtl() - 1);
        message.setTq(message.getTq() - MeshHandler.HOP_PENALTY);
    }

    private void forwardMessage(Message message) {
        prepareMessage(message);
        RemoteDevice destination = routingTable.get(message.getDestination());
        RemoteDevice bestNeighbour = routingTable.getBestNeighbourForRemoteDevice(destination);
        if (bestNeighbour != null) {
            bestNeighbour.sendMessage(message);
        }
    }

    private void broadcastMessage(Message message) {
        prepareMessage(message);
        for (RemoteDevice remoteDevice : routingTable.getNeighbours()) {
            remoteDevice.sendMessage(message);
        }
    }

    class RoutingException extends Exception {
        RoutingException(String message) {
            super(message);
        }
    }
}