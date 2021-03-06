package blue.happening.service.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import blue.happening.service.R;

public class Bt4Controls extends Fragment {

    private static Bt4Controls instance = null;
    private TextView textView;
    //    private Layer bluetoothLayer = null;
//    private MeshHandler meshHandler = null;
    private View rootView = null;
//    private DeviceListAdapter deviceListAdapter = null;
//
//    private MeshDeviceListAdapter meshDeviceListAdapter = null;
//    private ArrayList<MeshDevice> meshDevices;

    public static Bt4Controls getInstance() {
        if (instance == null)
            instance = new Bt4Controls();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_bt4controls, container, false);

//        if (!getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//            Snackbar.make(rootView, "BLE features are not supported!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
//        }
//
//        textView = (TextView) rootView.findViewById(R.id.textView_info_bt);
//        bluetoothLayer = Layer.getInstance();
//
//        ListView deviceListView = (ListView) rootView.findViewById(R.id.discovered_devices_list);
//        ArrayList<Device> scanResults = bluetoothLayer.getDevices();
//        deviceListAdapter = new DeviceListAdapter(rootView.getContext(), scanResults);
//        deviceListView.setAdapter(deviceListAdapter);
//        registerForContextMenu(deviceListView);
//
//        bluetoothLayer.addHandler(guiHandler);
//
//        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Device device = (Device) parent.getItemAtPosition(position);
//                Log.i("CLICK", "Clicked on device " + device.toString());
//            }
//        });

//        rootView.findViewById(R.id.get_devices).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.i("CLICK", "Clicked on get_devices");
//                List<String> ids = meshHandler.getDevices();
//
//                String message = "";
//                for (String id : ids) {
//                    message += id + " \n";
//                }
//                Toast.makeText(MyApplication.getAppContext(), message, Toast.LENGTH_LONG).show();
//
//            }
//        });
//
//        ListView meshMembersListView = (ListView) rootView.findViewById(R.id.mesh_members_list);
//        meshDevices = new ArrayList<>();
//        meshDeviceListAdapter = new MeshDeviceListAdapter(rootView.getContext(), meshDevices);
//        meshMembersListView.setAdapter(meshDeviceListAdapter);
//
//        TextView userInfo = (TextView) rootView.findViewById(R.id.textView_info_user_id);
//        userInfo.setText("    "+String.valueOf(bluetoothLayer.getMacAddress()));
//
//        Intent makeMeVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//        makeMeVisible.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0); //infinity
//        startActivity(makeMeVisible);
//
//        meshHandler = new MeshHandler(bluetoothLayer.getMacAddress());
//        meshHandler.registerLayer(bluetoothLayer);
//        meshHandler.registerCallback(new IMeshHandlerCallback() {
//            @Override
//            public void onMessageReceived(byte[] message) {
//
//            }
//
//            @Override
//            public void onDeviceAdded(String uuid) {
//                mergeIntoList(uuid);
//                guiHandler.obtainMessage(1).sendToTarget();
//            }
//
//            @Override
//            public void onDeviceRemoved(String uuid) {
//                meshMembers.remove(uuid);
//                removeMeshDevice(uuid);
//                guiHandler.obtainMessage(1).sendToTarget();
//
//            }
//        });
//
//        bluetoothLayer.start();
//
//        rootView.findViewById(R.id.button_reset).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                bluetoothLayer.reset();
//            }
//        });

        return rootView;
    }

    @Override
    public void onResume() {
        Log.i("Bt4Controls", "onResume");
        super.onResume();
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.discovered_devices_list) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.device_context, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int pos = info.position;
//        Device device = deviceListAdapter.getItem(pos);
//        switch(item.getItemId()) {
//            case R.id.connect:
//                Log.i("LONGCLICK", "Clicked on device " + device.toString() + " for Connect!");
//                device.connect();
//                return true;
//            case R.id.disconnect:
//                Log.i("LONGCLICK", "Clicked on device " + device.toString() + " for Disonnect!");
//                device.disconnect();
//                return true;
//            case R.id.write:
//                Log.i("LONGCLICK", "Clicked on device " + device.toString() + " for Write!");
//                device.connection.write(new Package(new byte[]{1,2,3,4,5,6,7,8,9,1,2,3,4,5,6,7,8,9,1,23,4,5,6,7,8,9,1,2,3,4,5,6,7,8,9,1,2,3,4,5,6,7,8,9,1,2,3,4,5,6,7,8,9,1,23,4,5,6,7,8,9,1,2,3,4,5,6,7,8,9,}));
//                return true;
//            case R.id.fetch_sdp_list:
//                Log.i("LONGCLICK", "Clicked on device " + device.toString() + " for Fetching SDP List");
//                return true;
//            default:
        return super.onContextItemSelected(item);
//        }
    }

//    void mergeIntoList(String id){
//        MeshDevice newMeshDevice  = new MeshDevice(id, "Name");
//        for (MeshDevice meshDevice : meshDevices) {
//            if (meshDevice.equals(newMeshDevice)){
//                return;
//            }
//        }
//        meshDevices.add(newMeshDevice);
//    }

//    private void removeMeshDevice(String uuid) {
//        MeshDevice newMeshDevice  = new MeshDevice(uuid, "Name");
//        for (MeshDevice meshDevice : meshDevices) {
//            if (meshDevice.equals(newMeshDevice)){
//                meshDevices.remove(newMeshDevice);
//            }
//        }
//    }


    @Override
    public void onPause() {
        Log.i("Bt4Controls", "onPause");
        super.onPause();
    }


    //    private Handler guiHandler = new Handler(Looper.getMainLooper()) {
//
//        @Override
//        public void handleMessage(Message msg) {
//            make gui
//            deviceListAdapter.notifyDataSetChanged();
//            meshDeviceListAdapter.notifyDataSetChanged();
//            meshMembersAdapter.notifyDataSetChanged();
//            textView.setText("Num Connections: "+bluetoothLayer.getNumOfConnectedDevices());
//        }
//    };
//
    @Override
    public void onDestroy() {
//        bluetoothLayer.shutdown();
        super.onDestroy();
    }
}