package com.formup.app.ui.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.formup.app.R
import com.formup.app.data.FormUpApi
import kotlinx.coroutines.tasks.await

private suspend fun finishSignIn(): Boolean {
    val snapshot = FormUpApi().loadSeason()   // creates the coach record if it's new
    return snapshot.team.name.isNotBlank()    // false means show Create Team
}

suspend fun emailSignIn(email: String, password: String): Boolean {
    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).await()
    return finishSignIn()
}

suspend fun emailSignUp(name: String, email: String, password: String): Boolean {
    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).await()
    FormUpApi().updateCoach(displayName = name)
    return finishSignIn()
}

suspend fun googleSignIn(context: Context): Boolean {
    val webClientId = context.getString(R.string.default_web_client_id)
    val request = GetCredentialRequest.Builder()
        .addCredentialOption(GetSignInWithGoogleOption.Builder(webClientId).build())
        .build()
    val credential = CredentialManager.create(context).getCredential(context, request).credential
    require(
        credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
    ) { "Unexpected credential type" }
    val idToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
    FirebaseAuth.getInstance()
        .signInWithCredential(GoogleAuthProvider.getCredential(idToken, null)).await()
    return finishSignIn()
}