package com.example.imagegallery

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.imagegallery.ui.theme.ImageGalleryTheme

class MainActivity : ComponentActivity() {

    // Manages primary activity creation and instance
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* Utilizes a lambda to register permissions implemented by the application.
           Additional verification could be utilized to perform custom logic
           if permissions are denied, but for testing purposes it can be assumed
           that they are accepted. */
        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { _ -> }

        // Camera is required, with a selection for storage based on version.
        val permissions = mutableListOf(Manifest.permission.CAMERA)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        // Activates the runtime permission request
        permissionLauncher.launch(permissions.toTypedArray())

        // UI content utilizes a navigation controller with a Material 3 theme.
        setContent {
            ImageGalleryTheme {
                Surface {
                    val navController = rememberNavController()
                    // Lands initially on the gallery view.
                    NavHost(navController, startDestination = "gallery") {
                        // Callback utilized to navigate to the camera view.
                        composable("gallery") {
                            GalleryView(onOpenCamera = { navController.navigate("camera") })
                        }
                        // Returns to the gallery view after picture is taken or back is pressed.
                        composable("camera") {
                            CameraView(onPhotoTaken = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}