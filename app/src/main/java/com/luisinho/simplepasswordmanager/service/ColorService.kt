package com.luisinho.simplepasswordmanager.service

import android.app.Application
import com.google.android.material.color.DynamicColors

class ColorService : Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}