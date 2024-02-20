package com.example.helloworld.ipc;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BookManagerImpl extends Binder implements IBookManager2 {

    public BookManagerImpl() {
        this.attachInterface(this, DESCRIPTOR);
    }

    public static IBookManager2 asInstance(android.os.IBinder obj) {
        if ((obj == null)) {
            return null;
        }
        IInterface iInterface = obj.queryLocalInterface(DESCRIPTOR);
        if (iInterface instanceof IBookManager2) {
            return (IBookManager2) iInterface;
        }

        return new Proxy(obj);
    }

    @Override
    protected boolean onTransact(int code, @NonNull Parcel data, @Nullable Parcel reply, int flags) throws RemoteException {
        String descriptor = DESCRIPTOR;
        switch (code) {
            case INTERFACE_TRANSACTION: {
                reply.writeString(descriptor);
                return true;
            }
            case TRANSACTION_getBookList: {


                return true;
            }
            case TRANSACTION_addBook: {

                return true;
            }
        }
        return super.onTransact(code, data, reply, flags);
    }

    private static class Proxy implements IBookManager2 {

        private IBinder mRemote;

        public Proxy(IBinder obj) {
            this.mRemote = obj;
        }

        @Override
        public List<Book> getBookList() throws RemoteException {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            List<Book> result = new ArrayList<>();
            try {
                data.writeInterfaceToken(DESCRIPTOR);
                boolean status = mRemote.transact(TRANSACTION_getBookList, data, reply, 0);
                reply.readException();
                result = reply.createTypedArrayList(Book.CREATOR);
            } catch (Exception e) {
                reply.recycle();
                data.recycle();
            }

            return result;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            try {
                data.writeInterfaceToken(DESCRIPTOR);
                if (book != null) {
                    data.writeInt(1);
                    book.writeToParcel(data, 0);
                } else {
                    data.writeInt(0);
                }
                boolean status = mRemote.transact(TRANSACTION_addBook, data, reply, 0);
                reply.readException();
            } catch (Exception e) {
                reply.recycle();
                data.recycle();
            }
        }

        public String getInterfaceDescriptor() {
            return DESCRIPTOR;
        }

        @Override
        public IBinder asBinder() {
            return mRemote;
        }
    }


    @Override
    public List<Book> getBookList() throws RemoteException {
        // todo
        return null;
    }

    @Override
    public void addBook(Book book) throws RemoteException {
        // todo
    }

    @Override
    public IBinder asBinder() {
        return this;
    }
}
