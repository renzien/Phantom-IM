package com.renzien.phantomim

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.renzien.phantomim.ui.navigation.PhantomAuthFlow
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
                    PhantomAuthFlow(
                        onSignInClick = {
                            showMessage(R.string.auth_sign_in_pending)
                        },
                        onCreateAccountClick = {
                            showMessage(R.string.auth_sign_up_pending)
                        },
                        modifier = Modifier
                            .padding(innerPadding)
                            .consumeWindowInsets(innerPadding)
                    )
                }
            }
        }
    }

    private fun showMessage(messageRes: Int) {
        Toast.makeText(
            this,
            messageRes,
            Toast.LENGTH_SHORT
        ).show()
    }
}