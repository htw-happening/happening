package blue.happening.dashboard;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import blue.happening.dashboard.fragment.ChatFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager fragmentManager = getFragmentManager();
        Fragment chatFragment = ChatFragment.getInstance();
        fragmentManager.beginTransaction()
                .replace(R.id.main_fragment_holder, chatFragment, "chat")
                .commit();
    }

}
