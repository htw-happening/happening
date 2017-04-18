package com.happening.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.happening.IAsyncCallback;
import com.happening.IAsyncInterface;

public class ASyncService extends Service {

    private final IAsyncInterface.Stub mBinder = new IAsyncInterface.Stub() {
        public void methodOne(IAsyncCallback callback) throws RemoteException {

//            for(int i = 0; i < 10; i++) {
//                try {
//                    sleep(10);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }

            callback.handleResponse("methodOne");
        }

        public void methodTwo(IAsyncCallback callback) throws RemoteException {
            callback.handleResponse("methodTwo");
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
