package com.formup.app.ui.auth
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import com.formup.app.R
import com.formup.app.data.ApiClient
import com.formup.app.data.UpdateCoachRequest

private suspend fun finishSignIn(): Boolean {
    ApiClient.api.getCoach()                 // API creates the coach record if it's new
    return ApiClient.api.getTeam().hasTeam   // false means show Create Team
}

suspend fun emailSignIn(email: String, password: String): Boolean {
    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).await()
    return finishSignIn()
}

suspend fun emailSignUp(name: String, email: String, password: String): Boolean {
    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).await()
    ApiClient.api.getCoach()
    ApiClient.api.updateCoach(UpdateCoachRequest(displayName = name))
    return ApiClient.api.getTeam().hasTeam
}

suspend fun googleSignIn(context: Context): Boolean {
    val webClientId = context.getString(R.string.default_web_client_id)
    val request = GetCredentialRequest.Builder().addCredentialOption(GetSignInWithGoogleOption.Builder(webClientId).build()).build()
    val credential = CredentialManager.create(context).getCredential(context, request).credential
    val idToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
    FirebaseAuth.getInstance()
        .signInWithCredential(GoogleAuthProvider.getCredential(idToken, null)).await()
    return finishSignIn()
}