// IRemoteDownloadInterface.aidl
package com.bestxty.dl.aidl;

// Declare any non-default types here with import statements

interface IRemoteDownloadInterface {

      String start(String url);

      String pause(String key);

      String resume(String key);

      String cancel(String key);
}
