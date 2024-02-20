package com.example.helloworld.ipc;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

import java.util.List;

public interface IBookManager2 extends IInterface {
    static final String DESCRIPTOR = "com.example.helloworld.ipc.IBookManager";

    static final int TRANSACTION_getBookList = IBinder.FIRST_CALL_TRANSACTION + 0;

    static final int TRANSACTION_addBook = IBinder.FIRST_CALL_TRANSACTION + 1;


    public List<Book> getBookList() throws RemoteException;

    public void addBook(Book book) throws RemoteException;
}
