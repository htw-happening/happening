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


        RemoteDevice previousDevice = routingTable.get(message.getPreviousHop());
        if (previousDevice != null) {
            SlidingWindow window;
            if (message.getSource().equals(uuid)) {
                window = previousDevice.getEchoSlidingWindow();
            } else {
                window = previousDevice.getReceiveSlidingWindow();
            }
            window.slideSequence(message.getSequence());
            window.addIfIsSequenceInWindow(message);
        }

        adjustTq(message);

        if(isMyMessage(message)){
            System.out.println(uuid + " OGM WAS MINE: " + message);
            return null;
        }

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
            if (message.getDestination().equals(MeshHandler.BROADCAST_ADDRESS)) {
                if (shouldMessageBeForwarded(message)) {
                    System.out.println(uuid + " OGM BROADCAST: " + message);
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
            System.out.println("MESSAGE RECEIVED: " + message);
            return message;
        } else {
            System.out.println("MESSAGE FORWARDED: " + message);
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
        SlidingWindow window = routingTable.get(message.getSource()).getReceiveSlidingWindow();
        return window.isSequenceOutOfWindow(message.getSequence());
    }

    private boolean shouldMessageBeForwarded(Message message) {
        if (sourceIsNeighbour(message)) {
            return true;
        } else {
            if (!isMessageVital(message)) {
                System.out.println(uuid + " OGM NOT VITAL: " + message);
                return false;
            } else if (!slidingWindowSaysYes(message)) {
                System.out.println(uuid + " OGM IN WINDOW: " + message);
                return false;
            }
            return true;
        }
    }

    private void prepareMessage(Message message) {
        message.setPreviousHop(uuid);
        message.setTtl(message.getTtl() - 1);
    }

    private void adjustTq(Message message) {
        RemoteDevice previousHop = routingTable.get(message.getPreviousHop());
        float previousTq = 0;
        if (previousHop != null) {
            previousTq = previousHop.getTq();
        }
        System.out.println("TQ MESSAGE: " + message.getTq() + ", PREVIOUS TQ: " + previousTq + ", = " + (float) (message.getTq() * previousTq));
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