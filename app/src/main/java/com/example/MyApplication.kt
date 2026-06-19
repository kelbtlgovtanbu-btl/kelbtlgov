package com.example

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.analytics.FirebaseAnalytics

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Firebase initialization disabled to prevent crashes in restricted runtime environments
        try {
            val options = com.google.firebase.FirebaseOptions.Builder()
                .setApiKey("AIzaSyBkbzjkfwZJeNX2_zJDuI2cRMJsEwgPL8k")
                .setApplicationId("1:222075204166:web:b2b6ebbf730f35d8dbd047")
                .setDatabaseUrl("https://databatulicin-8d2f4-default-rtdb.asia-southeast1.firebasedatabase.app")
                .setProjectId("databatulicin-8d2f4")
                .setStorageBucket("databatulicin-8d2f4.firebasestorage.app")
                .setGcmSenderId("222075204166")
                .build()

            com.google.firebase.FirebaseApp.initializeApp(this, options)
            
            // Log app open if initialized
            com.google.firebase.analytics.FirebaseAnalytics.getInstance(this).logEvent(com.google.firebase.analytics.FirebaseAnalytics.Event.APP_OPEN, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
