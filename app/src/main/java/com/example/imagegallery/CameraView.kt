package com.example.imagegallery

import android.content.ContentValues
import android.os.Build
import android.provider.MediaStore
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*

// Also utilizes a lambda callback for after a photo is taken.
@Composable
fun CameraView(onPhotoTaken: () -> Unit) {
    val context = LocalContext.current
    // Maintains the image capture CameraX class.
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

    // Material3 scaffold for button placement in order to capture a photo.
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val capture = imageCapture ?: return@FloatingActionButton
                takePhoto(context, capture, onPhotoTaken)
            }) { Text("◯") }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->
        // Embeds a classic android view for Compose UI functionality.
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            /* Initializes the Camerax display for a camera feed and utilizes a
               provider to manage the camera lifecycle. */
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                // Runs the camera, builds the live view, and manages the lifecycle.
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()

                    val preview = Preview.Builder()
                        .build()
                        .also { it.setSurfaceProvider(previewView.surfaceProvider) }

                    imageCapture = ImageCapture.Builder().build()
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        ctx as androidx.lifecycle.LifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageCapture
                    )
                }, ContextCompat.getMainExecutor(ctx))

                previewView
            }
        )
    }
}

// signifies the image name, sets associated metadata, and defines the location.
fun takePhoto(context: android.content.Context, imageCapture: ImageCapture, onPhotoTaken: () -> Unit) {
    val name = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MyGallery")
        }
    }

    /* Utilizes the built camera to signify that on a button press in the UI
       specifications above that a photo should be taken and saved so that
       the UI can be refreshed and pushed back to a gallery view. */
    val outputOptions = ImageCapture.OutputFileOptions.Builder(
        context.contentResolver,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        contentValues
    ).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exception: ImageCaptureException) = exception.printStackTrace()
            override fun onImageSaved(output: ImageCapture.OutputFileResults) = onPhotoTaken()
        }
    )
}