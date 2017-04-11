// IAsyncInterface.aidl
package blue.happening;

import blue.happening.IAsyncCallback;

interface IAsyncInterface {
    void methodOne(IAsyncCallback callback);
    void methodTwo(IAsyncCallback callback);
}