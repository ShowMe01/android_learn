package com.example.helloworld.share;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;


import com.example.helloworld.application.AppContext;

import java.io.File;

/**
 * move from AlbumNotifyHelper
 * Created by wangduanqing on 2020-05-06
 */
public class AlbumNotifyUtils {
    public static final int FLAG_IMAGE = 1;
    public static final int FLAG_VIDEO = 2;
    private static final String DCIM = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();
    private static String rootDir = DCIM + File.separator + "Camera";
    private static MediaScannerConnection sMediaScannerConnection;

    public static boolean isRootDirExists() {
        return new File(rootDir).exists();
    }

    public static String getRootDirPath() {
        return rootDir;
    }

    /**
     * @param targetFile 要保存的照片文件
     */
    public static void insertImageToMedia(long createTime, File targetFile) {
        Context context = AppContext.INSTANCE.getAppContext();
        ContentResolver resolver = context.getContentResolver();
        ContentValues newValues = new ContentValues();
        newValues.put(MediaStore.Images.Media.TITLE, targetFile.getName());
        newValues.put(MediaStore.Images.Media.DISPLAY_NAME, targetFile.getName());
        newValues.put(MediaStore.Images.Media.MIME_TYPE, getPhotoMimeType(targetFile.getAbsolutePath()));
        newValues.put(MediaStore.Images.Media.DATE_TAKEN, createTime);
        newValues.put(MediaStore.Images.Media.DATE_ADDED, createTime);
        newValues.put(MediaStore.Images.Media.DATE_MODIFIED, createTime);
        newValues.put(MediaStore.Images.Media.ORIENTATION, 0);
        newValues.put(MediaStore.Images.Media.DATA, targetFile.getAbsolutePath());
        newValues.put(MediaStore.Images.Media.SIZE, targetFile.length());
        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, newValues);
        scanFile(FLAG_IMAGE, targetFile.getAbsolutePath());
    }

    /**
     * @param saveFile 要保存的视频文件
     */
    public static Uri insertVideoToMedia(long createTime, File saveFile) {
        Context context = AppContext.INSTANCE.getAppContext();
        ContentResolver mContentResolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Video.Media.TITLE, saveFile.getName());
        values.put(MediaStore.Video.Media.DISPLAY_NAME, saveFile.getName());
        values.put(MediaStore.Video.Media.MIME_TYPE, getVideoMimeType(saveFile.getAbsolutePath()));
        values.put(MediaStore.Video.Media.DATE_TAKEN, createTime);
        values.put(MediaStore.Video.Media.DATE_MODIFIED, createTime);
        values.put(MediaStore.Video.Media.DATE_ADDED, createTime);
        values.put(MediaStore.Video.Media.DURATION, getDurationOfVideo(saveFile.getAbsolutePath()));
        values.put(MediaStore.MediaColumns.DATA, saveFile.getAbsolutePath());
        values.put(MediaStore.MediaColumns.SIZE, saveFile.length());
        Uri uri = mContentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
        scanFile(FLAG_VIDEO, saveFile.getAbsolutePath());
        return uri;
    }

    private static void scanFile(final int flag, final String filePath) {
        sMediaScannerConnection = new MediaScannerConnection(AppContext.INSTANCE.getAppContext(), new MediaScannerConnection.MediaScannerConnectionClient() {
            @Override
            public void onMediaScannerConnected() {
                String mimeType = "";
                if (FLAG_IMAGE == flag) {
                    mimeType = getPhotoMimeType(filePath);
                } else if (FLAG_VIDEO == flag) {
                    mimeType = getVideoMimeType(filePath);
                }
                try {
                    sMediaScannerConnection.scanFile(filePath, mimeType);
                } catch (Exception ignored) {

                }
            }

            @Override
            public void onScanCompleted(String path, Uri uri) {
                sMediaScannerConnection.disconnect();
            }
        });
        sMediaScannerConnection.connect();
    }

    // 获取音视频播放时长
    private static long getDurationOfVideo(String filePath) {
        MediaMetadataRetriever mmr = null;
        try {
            mmr = new MediaMetadataRetriever();
            mmr.setDataSource(filePath);
            String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            return Long.valueOf(duration);
        } catch (Exception e) {
            return 0;
        } finally {
            if (mmr != null) {
                mmr.release();
            }
        }
    }

    // 获取照片的mine_type
    private static String getPhotoMimeType(String path) {
        String lowerPath = path.toLowerCase();
        if (lowerPath.endsWith("jpg") || lowerPath.endsWith("jpeg")) {
            return "image/jpeg";
        } else if (lowerPath.endsWith("png")) {
            return "image/png";
        } else if (lowerPath.endsWith("gif")) {
            return "image/gif";
        }
        return "image/jpeg";
    }

    // 获取video的mine_type,暂时只支持mp4,3gp
    private static String getVideoMimeType(String path) {
        String lowerPath = path.toLowerCase();
        if (lowerPath.endsWith("mp4") || lowerPath.endsWith("mpeg4") || lowerPath.endsWith("mp4_")) {
            return "video/mp4";
        } else if (lowerPath.endsWith("3gp")) {
            return "video/3gp";
        }
        return "video/mp4";
    }
}
