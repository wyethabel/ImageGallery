package com.example.imagegallery

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter

// Composable that utilizes an onClick() lambda trigger to shift to CameraView
@Composable
fun GalleryView(onOpenCamera: () -> Unit) {
    val context = LocalContext.current
    // Maintains image uniform resource identifiers
    var images by remember { mutableStateOf(listOf<String>()) }

    // Initial image load.
    LaunchedEffect(Unit) { images = loadDeviceImages(context) }

    /* Material 3 container utilized for defining the action button to
       swap to camera view and the grid utilized for the images. */
    Scaffold(
        floatingActionButton = { FloatingActionButton(onClick = onOpenCamera) { Text("+") } }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(100.dp),
            contentPadding = padding
        ) {
            /* Iterate of the image uniform resource identifiers which
               coil asynchronously loads and caches. */
            items(images) { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )
            }
        }
    }
}