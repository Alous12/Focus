package com.hlasoftware.focus

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.LocaleList
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.hlasoftware.focus.features.profile.data.repository.LanguageRepository
import com.hlasoftware.focus.navigation.AppNavigation
import com.hlasoftware.focus.ui.theme.FocusTheme
import java.util.Locale

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        val languageRepository = LanguageRepository(newBase)
        val lang = languageRepository.getLanguage()
        if (lang != null) {
            val localeList = LocaleList.forLanguageTags(lang)
            val config = Configuration(newBase.resources.configuration)
            config.setLocales(localeList)
            super.attachBaseContext(newBase.createConfigurationContext(config))
        } else {
            super.attachBaseContext(newBase)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FocusTheme(darkTheme = true) {
                AppNavigation()
            }
        }
    }

    @Composable
    fun MaintenanceScreen() {
        Text("La aplicación está en mantenimiento. Por favor, intente más tarde.")
    }
}
