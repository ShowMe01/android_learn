// IBookManager.aidl
package com.example.helloworld.ipc;

// Declare any non-default types here with import statements
import com.example.helloworld.ipc.Book;

interface IBookManager {

    List<Book> getBookList();
    void addBook(in Book book);
}