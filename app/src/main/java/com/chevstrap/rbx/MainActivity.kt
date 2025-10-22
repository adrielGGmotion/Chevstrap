package com.chevstrap.rbx

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.chevstrap.rbx.Utility.FileTool
import com.chevstrap.rbx.ui.theme.AppTheme
import java.io.FileInputStream

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        setContent {
            AppTheme {
                MainScreen()
            }
        }
    }

    @Composable
    private fun MainScreen() {
        var visible by remember { mutableStateOf(false) }
        val context = LocalContext.current
        LaunchedEffect(Unit) {
            visible = true
        }

        Scaffold { innerPadding ->
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(durationMillis = 300, delayMillis = 150)),
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(onClick = {
                        val launcher = Launcher()
                        try {
                            launcher.Run()
                        } catch (e: Exception) {
                            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                        }
                    }) {
                        Text(text = context.getString(R.string.launchmenu_launchroblox))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        try {
                            val intent = Intent(context, SettingsActivity::class.java)
                            startActivity(intent)
                            finish()
                        } catch (e: Exception) {
                            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                        }
                    }) {
                        Text(text = context.getString(R.string.launchmenu_configuresettings))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { /* TODO: Handle Wiki button click */ }) {
                        Text(text = context.getString(R.string.launchmenu_wiki))
                    }
                }
            }
        }
    }
}
