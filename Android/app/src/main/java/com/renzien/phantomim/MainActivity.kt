package com.renzien.phantomim

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import android.widget.Toast
import com.renzien.phantomim.ui.screens.onboarding.OnboardingScreen
import com.renzien.phantomim.ui.theme.PhantomIMTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PhantomIMTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    OnboardingScreen(
                        onGetStartedClick = {
                            Toast.makeText(
                                this@MainActivity,
                                R.string.onboarding_welcome,
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        onSignInClick = {
                            Toast.makeText(
                                this@MainActivity,
                                R.string.onboarding_sign_in_message,
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}