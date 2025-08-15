package com.example.imagegallery

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore

// Returns a list of uniform resource identifiers for each image.
fun loadDeviceImages(context: Context): List<String> {
    val images = mutableListOf<String>()
    val projection = arrayOf(MediaStore.Images.Media._ID)
    val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

    /* Primary query to MediaStore that accesses all external content,
       defines the columns based on the projection without filtering,
       and defines the sort order based on the date. */
    context.contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection, null, null, sortOrder)?.use { cursor ->
        /* Selects the image ID and builds a content uniform resource identifier
           based on that object, which is then changed to a string and added to the list.
           This helps support ease of use for Coil. */
        val idCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        while (cursor.moveToNext()) {
            val id = cursor.getLong(idCol)
            images.add(ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id).toString())
        }
    }
    return images
}