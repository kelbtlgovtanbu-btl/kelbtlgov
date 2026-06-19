package com.example.data

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseSyncManager {
    private val database = try {
        FirebaseDatabase.getInstance("https://databatulicin-8d2f4-default-rtdb.asia-southeast1.firebasedatabase.app").apply {
            setPersistenceEnabled(true)
        }
    } catch (e: Exception) {
        null
    }
    
    private val firestore = try {
        FirebaseFirestore.getInstance()
    } catch (e: Exception) {
        null
    }
    
    private val profilesRef = database?.getReference("rt_profiles")
    private val usersRef = database?.getReference("users")

    suspend fun syncRtProfile(profile: RtProfile) {
        try {
            // Sync to RTDB
            profilesRef?.child(profile.rtNumber)?.setValue(profile)?.await()
            
            // Sync to Firestore
            firestore?.collection("rt_profiles")?.document(profile.rtNumber)?.set(profile)?.await()
            android.util.Log.d("FirebaseSync", "Synced RT Profile: ${profile.rtNumber}")
        } catch (e: Exception) {
            android.util.Log.e("FirebaseSync", "Error syncing profile: ${e.message}", e)
        }
    }

    suspend fun getAllProfilesFromFirebase(): List<RtProfile> {
        return try {
            val snapshot = profilesRef?.get()?.await()
            val list = mutableListOf<RtProfile>()
            snapshot?.children?.forEach { child ->
                try {
                    val profile = child.getValue(RtProfile::class.java)
                    if (profile != null) {
                        // Ensure rtNumber is set from the key if it's empty in the object
                        val finalProfile = if (profile.rtNumber.isEmpty()) {
                            profile.copy(rtNumber = child.key ?: "")
                        } else {
                            profile
                        }
                        if (finalProfile.rtNumber.isNotEmpty()) {
                            list.add(finalProfile)
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("FirebaseSync", "Failed to parse profile ${child.key}: ${e.message}")
                }
            }
            list
        } catch (e: Exception) {
            android.util.Log.e("FirebaseSync", "Error fetching profiles: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun deleteRtProfile(rtNumber: String) {
        try {
            profilesRef?.child(rtNumber)?.removeValue()?.await()
            firestore?.collection("rt_profiles")?.document(rtNumber)?.delete()?.await()
        } catch (e: Exception) {
            android.util.Log.e("FirebaseSync", "Error deleting profile: ${e.message}", e)
        }
    }

    suspend fun syncUser(user: User) {
        try {
            // Sync to RTDB
            usersRef?.child(user.username)?.setValue(user)?.await()
            
            // Sync to Firestore
            firestore?.collection("users")?.document(user.username)?.set(user)?.await()
        } catch (e: Exception) {
            android.util.Log.e("FirebaseSync", "Error syncing user: ${e.message}", e)
        }
    }
}
