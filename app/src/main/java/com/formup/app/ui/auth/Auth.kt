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


//this is a helper function that completes the authentication after a user sign in
// It loads season data from the backend to check if the coach already has a team set up.
// Returns 'true' if team setup is complete, or 'false' if the user needs to create a team.
private suspend fun finishSignIn(): Boolean {
    val snapshot = FormUpApi().loadSeason()   // creates the coach record if it's new
    return snapshot.team.name.isNotBlank()    // checks if the team name exists
}

// Signs in an existing user using email and password with Firebase Authentication
suspend fun emailSignIn(email: String, password: String): Boolean {
    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).await() //authenticates user credentials with firebase
    return finishSignIn() //loads the users data and determines the next screen to go to (home screen)
}

// Registers a brand new user using email, password, and their full name
suspend fun emailSignUp(name: String, email: String, password: String): Boolean {
    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).await() //creates the new account in firebase
    FormUpApi().updateCoach(displayName = name)
    return finishSignIn()
}

// Handles Google Sign-In using Androids Credential Manager API
suspend fun googleSignIn(context: Context): Boolean {
    //Get the Web Client ID configured in app resources
    val webClientId = context.getString(R.string.default_web_client_id)
    //building the google sign in request
    val request = GetCredentialRequest.Builder()
        .addCredentialOption(GetSignInWithGoogleOption.Builder(webClientId).build())
        .build()
    //asking the user to select their google account on the device
    val credential = CredentialManager.create(context).getCredential(context, request).credential
    //validate that the returned credential is a valid gooogle login token
    require(
        credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
    ) { "Unexpected credential type" }
    //extracting the google id token from the response
    val idToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
    FirebaseAuth.getInstance()
        .signInWithCredential(GoogleAuthProvider.getCredential(idToken, null)).await()
    return finishSignIn()
}