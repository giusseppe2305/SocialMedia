package com.optic.socialmedia.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.optic.socialmedia.models.User;

public class AuthProviders {
    private FirebaseAuth auth;

    public AuthProviders() {
        auth = FirebaseAuth.getInstance();
    }

    public Task<AuthResult> createAuthentication(User mUser) {
        return auth.createUserWithEmailAndPassword(mUser.getEmail(), mUser.getPass());

    }

    public Task<AuthResult> logIn(String email, String pass) {
        return auth.signInWithEmailAndPassword(email, pass);
    }

    public String getIdCurrentUser() {
        return auth.getCurrentUser().getUid();
    }

    public FirebaseAuth getFirebaseAuth() {
        return auth;
    }

    public boolean existSession() {
        return auth.getCurrentUser() != null;
    }

    public void logOut() {
        auth.signOut();
    }

    public Task logInGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        return auth.signInWithCredential(credential);
    }
}
