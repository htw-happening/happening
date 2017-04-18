package com.happening;

import com.happening.ServiceCallbackInterface;

interface HappeningInterface {

      void startClientScan(ServiceCallbackInterface callback);
      void stopClientScan();

}
