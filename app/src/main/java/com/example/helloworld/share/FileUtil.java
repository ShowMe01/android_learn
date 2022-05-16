package com.example.helloworld.share;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtil {

    public static void copyFile(File oldFile, File newFile) throws IOException {
        try {
            copyFile(new FileInputStream(oldFile), new FileOutputStream(newFile));
        } catch (IOException e) {
            if (newFile.exists()) {
                newFile.delete();
            }
            throw e;
        }
    }

    public static void copyFile(InputStream inputStream, OutputStream outputStream) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new BufferedInputStream(inputStream, 4096);
            os = new BufferedOutputStream(outputStream, 4096);
            int len = -1;
            byte[] buffer = new byte[4096];
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.flush();
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (Throwable ignored) {
            }
        }
    }

}

