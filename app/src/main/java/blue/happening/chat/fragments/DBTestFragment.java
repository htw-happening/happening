package blue.happening.chat.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Random;

import blue.happening.chat.R;
import blue.happening.chat.datastore.DBHelper;
import blue.happening.chat.datastore.DBTestAdapter;
import blue.happening.chat.datastore.DBTestEntryModel;

public class DBTestFragment extends Fragment {
    private static final String ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm";
    private static DBTestFragment instance = null;
    public ArrayList<DBTestEntryModel> dbTestEntryModelArrayList;
    private View rootView = null;
    private ListView listView;
    private DBTestAdapter dbTestAdapter;
    private DBHelper dbHelper;

    public static DBTestFragment getInstance() {
        instance = new DBTestFragment();
        return instance;
    }

    private static String getRandomString(final int sizeOfRandomString) {
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(sizeOfRandomString);
        for (int i = 0; i < sizeOfRandomString; ++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dbHelper = DBHelper.getInstance();

        rootView = inflater.inflate(R.layout.fragment_db_test, container, false);

        dbTestEntryModelArrayList = new ArrayList<>();
        dbTestAdapter = new DBTestAdapter(getContext(), dbTestEntryModelArrayList);

        listView = (ListView) rootView.findViewById(R.id.listView_db_entries);
        listView.setAdapter(dbTestAdapter);

        rootView.findViewById(R.id.bt_generateDevice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = getRandomString(12);
                String address = getRandomString(12);
                String lastSeen = getRandomString(12);

                addDeviceEntry(name, address, lastSeen);
                dbHelper.insertDevice(name, address, lastSeen);
                Log.d("DB Test - ", dbHelper.getAllDeviceNames().toString());

            }
        });

        rootView.findViewById(R.id.bt_delDevice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void addDeviceEntry(String name, String address, String lastSeen) {
        DBTestEntryModel chatEntryModel = new DBTestEntryModel(name, address, lastSeen);
        dbTestEntryModelArrayList.add(chatEntryModel);
        dbTestAdapter.notifyDataSetChanged();
    }

    private void addDeviceEntry(String id, String name, String address, String lastSeen) {
        DBTestEntryModel dbTestEntryModel = new DBTestEntryModel(name, address, lastSeen);
        dbTestEntryModelArrayList.add(dbTestEntryModel);
        dbTestAdapter.notifyDataSetChanged();
    }

}
