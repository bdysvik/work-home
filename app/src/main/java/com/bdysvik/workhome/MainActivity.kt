package com.bdysvik.workhome

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.bdysvik.workhome.data.AuthRepository
import com.bdysvik.workhome.data.FirebaseAuthRepository
import com.bdysvik.workhome.data.FirebaseFamilyRepository
import com.bdysvik.workhome.data.FamilyRepository
import com.bdysvik.workhome.ui.WorkHomeApp
import com.bdysvik.workhome.ui.theme.WorkHomeTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class AppContainer(
    val authRepository: AuthRepository,
    val familyRepository: FamilyRepository,
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val firebaseConfigured = FirebaseApp.initializeApp(this) != null || FirebaseApp.getApps(this).isNotEmpty()
        val appContainer = if (firebaseConfigured) {
            AppContainer(
                authRepository = FirebaseAuthRepository(FirebaseAuth.getInstance()),
                familyRepository = FirebaseFamilyRepository(FirebaseFirestore.getInstance()),
            )
        } else {
            null
        }

        setContent {
            WorkHomeTheme {
                WorkHomeApp(appContainer = appContainer, firebaseConfigured = firebaseConfigured)
            }
        }
    }
}
