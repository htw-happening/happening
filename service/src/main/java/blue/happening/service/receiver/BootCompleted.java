package blue.happening.service.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import blue.happening.service.HappeningService;


/**
 * {@link BroadcastReceiver BroadcastReceiver} that is configured to start our
 * {@link HappeningService service} on {@link Intent#ACTION_BOOT_COMPLETED device boot up}.
 */
public class BootCompleted extends BroadcastReceiver {

    /**
     * Convert an intent with device boot up action into a service start.
     *
     * @param context The Context in which the receiver is running.
     * @param intent  The Intent being received.
     */
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.v(this.getClass().getSimpleName(), "onReceive");
            Intent happening = new Intent(context, HappeningService.class);
            context.startService(happening);
            // TODO: Ensure the service start was successful.
        }
    }
}
