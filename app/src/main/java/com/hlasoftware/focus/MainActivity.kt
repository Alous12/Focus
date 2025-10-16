package com.hlasoftware.focus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.hlasoftware.focus.navigation.AppNavigation
import com.hlasoftware.focus.ui.theme.FocusTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            FocusTheme(darkTheme = true) {
                AppNavigation()
            }
        }
    }
}
