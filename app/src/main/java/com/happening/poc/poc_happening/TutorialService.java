package com.happening.poc.poc_happening;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class TutorialService extends IntentService {
//
//    private boolean isRunning;
//    private Context context;
//    private Looper mServiceLooper;
////    private ServiceHandler mServiceHandler;
//    private Thread backgroundThread;
//


    public TutorialService() {
        super("MyTestService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Do the task here
        Log.i("MyTestService", "Service running");


    }


//    public TutorialService() {
//
//    }

//    @Override
//    public void onCreate() {
//        // To avoid cpu-blocking, we create a background handler to run our service
////        HandlerThread thread = new HandlerThread("TutorialService",
////                Process.THREAD_PRIORITY_BACKGROUND);
//        // start the new handler thread
////        thread.start();
//
//        this.context = this;
//        this.isRunning = false;
//        this.backgroundThread = new Thread(myTask);
//
////        mServiceLooper = thread.getLooper();
//        // start the service using the background handler
////        mServiceHandler = new ServiceHandler(mServiceLooper);
//    }
//
//    private Runnable myTask = new Runnable() {
//        public void run() {
//            // Do something here
//            Log.d("SERVICE", "is running " + backgroundThread.getId());
//            stopSelf();
//        }
//    };
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        Toast.makeText(this, "onStartCommand", Toast.LENGTH_SHORT).show();
//
//        if(!this.isRunning) {
//            this.isRunning = true;
//            this.backgroundThread.start();
//        }
//
//        return START_STICKY;
//
////        // call a new service handler. The service ID can be used to identify the service
////        Message message = mServiceHandler.obtainMessage();
////        message.arg1 = startId;
////        mServiceHandler.sendMessage(message);
////
////        return START_STICKY;
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        this.isRunning = false;
//    }
//
////    protected void showToast(final String msg){
////        //gets the main thread
////        Handler handler = new Handler(Looper.getMainLooper());
////        handler.post(new Runnable() {
////            @Override
////            public void run() {
////                // run this code in the main thread
////                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
////            }
////        });
////    }
//
//
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        Log.d("ON BIND", intent.toString());
//        return null;
//    }
//
////    // Object responsible for
////    private final class ServiceHandler extends Handler {
////
////        public ServiceHandler(Looper looper) {
////            super(looper);
////        }
////
////        @Override
////        public void handleMessage(Message msg) {
////            // Well calling mServiceHandler.sendMessage(message);
////            // from onStartCommand this method will be called.
////
////            // Add your cpu-blocking activity here
////            while (!Thread.currentThread().isInterrupted()){
////
////                try {
////                    Thread.sleep(1000);
////                    Log.d("Service","Im working");
////                } catch (InterruptedException e) {
////                    Thread.currentThread().interrupt();
////                }
////            }
////            showToast("Finishing TutorialService, id: " + msg.arg1);
////            // the msg.arg1 is the startId used in the onStartCommand,
////            // so we can track the running sevice here.
////            stopSelf(msg.arg1);
////        }
////    }


}