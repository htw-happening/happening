package blue.happening.chat;

import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import blue.happening.chat.fragment.ChatFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // register
        // set up fragment layout
        setContentView(R.layout.activity_main);
        FragmentManager fragmentManager = getFragmentManager();
        Fragment chatFragment = ChatFragment.getInstance();
        fragmentManager.beginTransaction()
                .replace(R.id.main_fragment_holder, chatFragment, "chat")
                .commit();
    }

}
