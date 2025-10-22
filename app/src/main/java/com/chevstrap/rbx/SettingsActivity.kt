package com.chevstrap.rbx

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.chevstrap.rbx.ui.theme.AppTheme

class SettingsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        setContent {
            AppTheme {
                SettingsScreen()
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // TODO: Handle back press
                finish()
            }
        })
    }

    @Composable
    private fun SettingsScreen() {
        var visible by remember { mutableStateOf(false) }
        val context = LocalContext.current
        var currentPage by remember { mutableStateOf("Flags Settings") }

        LaunchedEffect(Unit) {
            visible = true
        }

        Scaffold(
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        // TODO: Handle back press
                        finish()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = currentPage, style = MaterialTheme.typography.titleLarge)
                }
            }
        ) { innerPadding ->
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(durationMillis = 300, delayMillis = 150)),
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        ToggleButton(
                            text = getTextLocale(context, R.string.menu_integrations_title),
                            isSelected = currentPage == "Integrations",
                            onClick = { currentPage = "Integrations" }
                        )
                        ToggleButton(
                            text = getTextLocale(context, R.string.menu_fastflags_title),
                            isSelected = currentPage == "Flags Settings",
                            onClick = { currentPage = "Flags Settings" }
                        )
                        ToggleButton(
                            text = getTextLocale(context, R.string.menu_fastflageditor_title),
                            isSelected = currentPage == "Flags Editor",
                            onClick = { currentPage = "Flags Editor" }
                        )
                        ToggleButton(
                            text = getTextLocale(context, R.string.menu_behaviour_title),
                            isSelected = currentPage == "Launcher",
                            onClick = { currentPage = "Launcher" }
                        )
                        ToggleButton(
                            text = "Chevstrap",
                            isSelected = currentPage == "Settings",
                            onClick = { currentPage = "Settings" }
                        )
                        ToggleButton(
                            text = getTextLocale(context, R.string.about_title),
                            isSelected = currentPage == "About",
                            onClick = { currentPage = "About" }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(modifier = Modifier.weight(1f)) {
                        // TODO: Replace with fragment content
                        Text(text = "Content for $currentPage", modifier = Modifier.align(Alignment.Center))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(onClick = {
                            App.getConfig().save()
                        }) {
                            Text(text = "Save")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            val launcher = Launcher()
                            try {
                                App.getConfig().save()
                                launcher.Run()
                            } catch (e: Exception) {
                                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                            }
                        }) {
                            Text(text = "Save and Launch")
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun ToggleButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
        TextButton(onClick = onClick) {
            Text(
                text = text,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }
    }

    private fun getTextLocale(context: Context, resId: Int): String {
        return context.getString(resId)
    }
}
