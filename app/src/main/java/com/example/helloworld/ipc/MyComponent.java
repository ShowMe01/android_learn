package com.example.helloworld.ipc;

import android.os.RemoteException;

public class MyComponent {

    private IMyInterface myInterface = new IMyInterface.Stub() {
        @Override
        public void sendMessage(String message) throws RemoteException {
            // 处理发送消息的逻辑
        }

        @Override
        public String receiveMessage() throws RemoteException {
            // 处理接收消息的逻辑
            return "Hello from MyComponent!";
        }
    };

}
