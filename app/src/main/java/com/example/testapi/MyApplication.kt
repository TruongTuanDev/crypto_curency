package com.example.testapi

import SessionManager
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.testapi.screens.MainAdminActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MyApplication : Application() {

    private var activityReferences = 0
    private var isActivityChangingConfigurations = false
    private lateinit var sessionManager: SessionManager
    private lateinit var database : DatabaseReference

    override fun onCreate() {
        super.onCreate()
        sessionManager = SessionManager(this)
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                Log.d("MyApplication", "onActivityCreated: ${activity.localClassName}")
            }

            override fun onActivityStarted(activity: Activity) {
                if (++activityReferences == 1 && !isActivityChangingConfigurations) {
                    // App enters foreground
                    Log.d("MyApplication", "App entered foreground")
                }
            }

            override fun onActivityResumed(activity: Activity) {
                Log.d("MyApplication", "onActivityResumed: ${activity.localClassName}")
            }

            override fun onActivityPaused(activity: Activity) {
                Log.d("MyApplication", "onActivityPaused: ${activity.localClassName}")
            }

            override fun onActivityStopped(activity: Activity) {
                isActivityChangingConfigurations = activity.isChangingConfigurations
                if (--activityReferences == 0 && !isActivityChangingConfigurations) {
                   val phone = sessionManager.getPhoneInstall()
                    database = FirebaseDatabase.getInstance().getReference("Accounts")
                    database.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (phone != null) {
                                database.child(phone).child("isOnline").setValue(false)
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {

                        }

                    })

                }
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                Log.d("MyApplication", "onActivitySaveInstanceState: ${activity.localClassName}")
            }

            override fun onActivityDestroyed(activity: Activity) {
                Log.d("MyApplication", "Tắt rồi: ${activity.localClassName}")
            }
        })
    }
}