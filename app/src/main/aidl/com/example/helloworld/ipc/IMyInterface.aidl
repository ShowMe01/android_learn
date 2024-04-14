// IMyInterface.aidl
package com.example.helloworld.ipc;

// Declare any non-default types here with import statements

interface IMyInterface {
    void sendMessage(String message);
    String receiveMessage();
}