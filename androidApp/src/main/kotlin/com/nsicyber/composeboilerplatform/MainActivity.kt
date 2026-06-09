package com.nsicyber.composeboilerplatform

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.nsicyber.composeboilerplatform.sharedlogic.core.network.initializePlatformConnectivity
import com.nsicyber.composeboilerplatform.sharedui.AppRoot

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        initializePlatformConnectivity(applicationContext)

        setContent {
            AppRoot()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    AppRoot()
}