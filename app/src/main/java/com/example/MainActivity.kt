package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.ui.MainScreen
import com.example.ui.components.SplashScreen
import com.example.ui.theme.AmoledBlack
import com.example.ui.theme.RipOmrTheme
import com.example.ui.viewmodel.OmrViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: OmrViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            RipOmrTheme {
                var showSplash by remember { mutableStateOf(true) }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AmoledBlack)
                ) {
                    Crossfade(
                        targetState = showSplash,
                        animationSpec = tween(500),
                        label = "splash_crossfade"
                    ) { isSplash ->
                        if (isSplash) {
                            SplashScreen(
                                onSplashFinished = {
                                    showSplash = false
                                }
                            )
                        } else {
                            MainScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}
