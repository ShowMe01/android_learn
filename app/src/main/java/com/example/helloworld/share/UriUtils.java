package com.example.helloworld.share;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;

public class UriUtils {

    /**
     * Gets the content:// URI from the given corresponding path to a file
     *
     * @param context
     * @param imageFile
     * @return content Uri
     */
    public static Uri getImageContentUri(Context context, File imageFile) {
//        String filePath = imageFile.getAbsolutePath();
//        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                new String[] { MediaStore.Images.Media._ID }, MediaStore.Images.Media.DATA + "=? ",
//                new String[] { filePath }, null);
//        if (cursor != null && cursor.moveToFirst()) {
////            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
//            Uri baseUri = Uri.parse("content://media/external/images/media");
//            return Uri.withAppendedPath(baseUri, "" + id);
//        } else {
//            if (imageFile.exists()) {
//                ContentValues values = new ContentValues();
//                values.put(MediaStore.Images.Media.DATA, filePath);
//                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//            } else {
//                return null;
//            }
//        }
        return null;
    }

    /**
     * Gets the content:// URI from the given corresponding path to a file
     *
     * @param context
     * @param videoFile
     * @return content Uri
     */
    public static Uri getVideoContentUri(Context context, File videoFile) {
//        String filePath = videoFile.getAbsolutePath();
//        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
//                new String[] { MediaStore.Video.Media._ID }, MediaStore.Video.Media.DATA + "=? ",
//                new String[] { filePath }, null);
//        if (cursor != null && cursor.moveToFirst()) {
//            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
//            Uri baseUri = Uri.parse("content://media/external/video/media");
//            return Uri.withAppendedPath(baseUri, "" + id);
//        } else {
//            if (videoFile.exists()) {
//                ContentValues values = new ContentValues();
//                values.put(MediaStore.Video.Media.DATA, filePath);
//                return context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
//            } else {
//                return null;
//            }
//        }
        return null;
    }

    /**
     * Gets the content:// URI from the given corresponding path to a file
     *
     * @param context
     * @param audioFile
     * @return content Uri
     */
    public static Uri getAudioContentUri(Context context, File audioFile) {
//        String filePath = audioFile.getAbsolutePath();
//        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//                new String[] { MediaStore.Audio.Media._ID }, MediaStore.Audio.Media.DATA + "=? ",
//                new String[] { filePath }, null);
//        if (cursor != null && cursor.moveToFirst()) {
//            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
//            Uri baseUri = Uri.parse("content://media/external/audio/media");
//            return Uri.withAppendedPath(baseUri, "" + id);
//        } else {
//            if (audioFile.exists()) {
//                ContentValues values = new ContentValues();
//                values.put(MediaStore.Audio.Media.DATA, filePath);
//                return context.getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
//            } else {
//                return null;
//            }
//        }
        return null;
    }

}
