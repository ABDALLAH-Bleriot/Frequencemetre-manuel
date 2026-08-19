package com.example.comptegouttes

import android.app.Activity
import android.app.Application
import android.os.Bundle

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                val screen = when (activity) {
                    is MainActivity -> "counter"
                    is GeneratorActivity -> "generator"
                    is CalcActivity -> "calc"
                    is InfoActivity -> "info"
                    else -> null
                }
                if (screen != null) TutoManager.attach(activity, screen)
            }
            override fun onActivityStarted(a: Activity) {}
            override fun onActivityResumed(a: Activity) {}
            override fun onActivityPaused(a: Activity) {}
            override fun onActivityStopped(a: Activity) {}
            override fun onActivitySaveInstanceState(a: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(a: Activity) {}
        })
    }
}
