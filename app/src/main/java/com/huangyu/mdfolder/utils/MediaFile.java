package com.huangyu.mdfolder.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by huangyu on 2017/6/27.
 */

public class MediaFile {

    private final File file;
    private final ContentResolver contentResolver;
    private final Uri filesUri;
    private final Uri imagesUri;

    public MediaFile(ContentResolver contentResolver, File file) {
        this.file = file;
        this.contentResolver = contentResolver;
        filesUri = MediaStore.Files.getContentUri("external");
        imagesUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    }

    /**
     * Deletes the file. Returns true if the file has been successfully deleted or otherwise does not exist. This operation is not
     * recursive.
     */
    public boolean delete()
            throws IOException {
        if (!file.exists()) {
            return true;
        }

        boolean directory = file.isDirectory();
        if (directory) {
            // Verify directory does not contain any files/directories within it.
            String[] files = file.list();
            if (files != null && files.length > 0) {
                return false;
            }
        }

        String where = MediaStore.MediaColumns.DATA + "=?";
        String[] selectionArgs = new String[]{file.getAbsolutePath()};

        // Delete the entry from the media database. This will actually delete media files (images, audio, and video).
        contentResolver.delete(filesUri, where, selectionArgs);

        if (file.exists()) {
            // If the file is not a media file, create a new entry suggesting that this location is an image, even
            // though it is not.
            ContentValues values = new ContentValues();
            values.put(MediaStore.Files.FileColumns.DATA, file.getAbsolutePath());
            contentResolver.insert(imagesUri, values);

            // Delete the created entry, such that content provider will delete the file.
            contentResolver.delete(filesUri, where, selectionArgs);
        }

        return !file.exists();
    }

    public File getFile() {
        return file;
    }

    /**
     * Creates a new directory. Returns true if the directory was successfully created or exists.
     */
    public boolean mkdir()
            throws IOException {
        if (file.exists()) {
            return file.isDirectory();
        }

        ContentValues values;
        Uri uri;

        // Create a media database entry for the directory. This step will not actually cause the directory to be created.
        values = new ContentValues();
        values.put(MediaStore.Files.FileColumns.DATA, file.getAbsolutePath());
        contentResolver.insert(filesUri, values);

        // Create an entry for a temporary image file within the created directory.
        // This step actually causes the creation of the directory.
        values = new ContentValues();
        values.put(MediaStore.Files.FileColumns.DATA, file.getAbsolutePath() + "/temp.jpg");
        uri = contentResolver.insert(imagesUri, values);

        // Delete the temporary entry.
        contentResolver.delete(uri, null, null);

        return file.exists();
    }

    /**
     * Returns an OutputStream to write to the file. The file will be truncated immediately.
     */
    public OutputStream write()
            throws IOException {
        if (file.exists() && file.isDirectory()) {
            throw new IOException("File exists and is a directory.");
        }

        // Delete any existing entry from the media database.
        // This may also delete the file (for media types), but that is irrelevant as it will be truncated momentarily in any case.
        String where = MediaStore.MediaColumns.DATA + "=?";
        String[] selectionArgs = new String[]{file.getAbsolutePath()};
        contentResolver.delete(filesUri, where, selectionArgs);

        ContentValues values = new ContentValues();
        values.put(MediaStore.Files.FileColumns.DATA, file.getAbsolutePath());
        Uri uri = contentResolver.insert(filesUri, values);

        if (uri == null) {
            // Should not occur.
            throw new IOException("Internal error.");
        }

        return contentResolver.openOutputStream(uri);
    }

}
