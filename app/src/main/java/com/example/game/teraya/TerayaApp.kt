package com.example.game.teraya

import android.app.Application
import com.google.android.material.color.DynamicColors

class TerayaApp : Application() {

    override fun onCreate() {
        super.onCreate()

        DynamicColors.applyToActivitiesIfAvailable(this)

    }

}