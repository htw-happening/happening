// IAsyncInterface.aidl
package com.happening;

import com.happening.IAsyncCallback;

interface IAsyncInterface {
    void methodOne(IAsyncCallback callback);
    void methodTwo(IAsyncCallback callback);
}