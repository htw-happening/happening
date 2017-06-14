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
        if (isEchoOGM(message)) {
            System.out.println(uuid + " OGM WAS MINE: " + message);
            return null;
        }

        routingTable.ensureConnection(message.getSource(), message.getPreviousHop());
        if (message.getType() == MeshHandler.MESSAGE_TYPE_OGM) {

            slideWindows(message);
            adjustTq(message);
            routeOgm(message);
            return null;
        } else if (message.getType() == MeshHandler.MESSAGE_TYPE_UCM) {
            return routeUcm(message);
        } else {
            throw new RoutingException("Unknown message type");
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

    private boolean sourceIsNeighbour(Message message) {
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
            System.out.println(uuid + " DROP ECHO OGM: " + message);
            return false;
        } else if (sourceIsNeighbour(message)) {
            System.out.println(uuid + " BROADCAST NEIGHBOUR OGM: " + message);
            return true;
        } else if (!isMessageVital(message)) {
            System.out.println(uuid + " DROP NOT VITAL OGM: " + message);
            return false;
        } else if (!slidingWindowSaysYes(message)) {
            System.out.println(uuid + " DROP IN WINDOW OGM: " + message);
            return false;
        } else {
            System.out.println(uuid + " BROADCAST VITAL OGM: " + message);
            return true;
        }
    }

    private void prepareMessage(Message message) {
        message.setPreviousHop(uuid);
        message.setTtl(message.getTtl() - 1);
    }

    private void slideWindows(Message message) {
        RemoteDevice previousDevice = routingTable.get(message.getPreviousHop());
        if (previousDevice != null) {
            SlidingWindow window = null;
            if (isEchoOGM(message)) {
                window = previousDevice.getEchoSlidingWindow();
            } else if (isNeighbourOGM(message)) {
                window = previousDevice.getReceiveSlidingWindow();
            }
            if (window != null) {
                window.slideSequence(message.getSequence());
                window.addIfIsSequenceInWindow(message);
            }
        } else {
            System.out.println("Previous hop has left");
        }
    }

    private void adjustTq(Message message) {
        RemoteDevice previousHop = routingTable.get(message.getPreviousHop());
        float previousTq = 0;
        if (previousHop != null) {
            previousTq = previousHop.getTq();
        } else {
            System.out.println("Previous hop has left");
        }
        message.setTq((int) (message.getTq() * previousTq) - MeshHandler.HOP_PENALTY);
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