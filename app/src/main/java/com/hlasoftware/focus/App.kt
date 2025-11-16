package com.hlasoftware.focus

import android.app.Application
import com.google.firebase.FirebaseApp
import com.hlasoftware.focus.di.appModule
import com.hlasoftware.focus.features.activity_details.di.activityDetailsModule
import com.hlasoftware.focus.features.create_workgroup.di.createWorkgroupModule
import com.hlasoftware.focus.features.workgroups.di.workgroupsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        startKoin {
            androidContext(this@App)
            modules(appModule, activityDetailsModule, workgroupsModule, createWorkgroupModule)
        }
    }
}