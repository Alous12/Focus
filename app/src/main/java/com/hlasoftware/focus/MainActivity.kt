package com.hlasoftware.focus

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.hlasoftware.focus.navigation.AppNavigation
import com.hlasoftware.focus.ui.theme.FocusTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val remoteConfigSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(0)
            .build()
        firebaseRemoteConfig.setConfigSettingsAsync(remoteConfigSettings)
        firebaseRemoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val maintenanceMode = firebaseRemoteConfig.getBoolean("isMaintenanceMode")
                    Log.d("FirebaseConfig", "Mantenimiento: $maintenanceMode")

                    if (maintenanceMode) {
                        setContent {
                            FocusTheme {
                                MaintenanceScreen()
                            }
                        }
                    } else {
                        setContent {
                            FocusTheme(darkTheme = true) {
                                AppNavigation()
                            }
                        }
                    }
                } else {
                    Log.d("FirebaseConfig", "Error al obtener la configuración de Firebase")
                    setContent {
                        FocusTheme(darkTheme = true) {
                            AppNavigation()
                        }
                    }
                }
            }
    }

    @Composable
    fun MaintenanceScreen() {
        Text("La aplicación está en mantenimiento. Por favor, intente más tarde.")
    }
}
