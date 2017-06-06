package blue.happening.simulation.entities;

import blue.happening.mesh.IMeshHandlerCallback;


public class MockMeshHandlerCallback implements IMeshHandlerCallback {

    @Override
    public void onMessageReceived(String message) {
        System.out.println("App: Message Received");
    }

    @Override
    public void onDeviceAdded(String uuid) {
        System.out.println("App: Device added");
    }

    @Override
    public void onDeviceRemoved(String uuid) {
        System.out.println("App: Device removed");
    }
}
