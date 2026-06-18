package com.nsicyber.composeboilerplatform

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.ui.tooling.preview.Preview
import com.nsicyber.composeboilerplatform.sharedlogic.core.audioplayer.initializePlatformAudio
import com.nsicyber.composeboilerplatform.sharedlogic.core.network.initializePlatformConnectivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        initializePlatformConnectivity(applicationContext)
        initializePlatformAudio(applicationContext)

        setContent {
            CoreHostContent()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    CoreHostContent()
}

@Composable
private fun CoreHostContent() {
    Text("Core module host app")
}