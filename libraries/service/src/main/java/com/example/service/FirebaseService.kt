package com.example.service

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONObject

class FirebaseService @Inject constructor() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()


    fun saveUserToBackend(
        context: Context,
        firebaseUID: String,
        name: String,
        email: String,
        role: String
    ) {

        val url = "http://10.0.2.2:5000/save-user"

        val queue = Volley.newRequestQueue(context)

        val jsonObject = JSONObject()

        jsonObject.put("firebaseUID", firebaseUID)
        jsonObject.put("name", name)
        jsonObject.put("email", email)
        jsonObject.put("role", role)

        val request = JsonObjectRequest(
            Request.Method.POST,
            url,
            jsonObject,

            { response ->

                Log.d("BACKEND", "SUCCESS : ${response}")

            },

            { error ->

                Log.e("BACKEND", "ERROR : ${error.message}")
            }

        )

        queue.add(request)

    }

    // 🔥 CREATE USER
    fun createUser(
        email: String,
        password: String,
        onResult: (Result<String>) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                val uid = auth.currentUser?.uid
                if (it.isSuccessful && uid != null) {
                    onResult(Result.success(uid))
                } else {
                    onResult(Result.failure(it.exception ?: Exception("Signup failed")))
                }
            }
    }

    // 🔥 SAVE USER DATA
    fun saveUser(
        uid: String,
        data: Map<String, Any>,
        onResult: (Result<Unit>) -> Unit
    ) {
        db.collection("users")
            .document(uid)
            .set(data)
            .addOnSuccessListener { onResult(Result.success(Unit)) }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    // 🔥 EMAIL VERIFICATION
    fun sendEmailVerification(
        onResult: (Result<Unit>) -> Unit
    ) {
        val user = auth.currentUser
        user?.sendEmailVerification()
            ?.addOnCompleteListener {
                if (it.isSuccessful) onResult(Result.success(Unit))
                else onResult(Result.failure(it.exception ?: Exception("Verification failed")))
            }
    }

    // 🔥 LOGIN
    fun login(
        email: String,
        password: String,
        onResult: (Result<Unit>) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email.trim(), password.trim())
            .addOnCompleteListener {
                if (it.isSuccessful) onResult(Result.success(Unit))
                else onResult(Result.failure(it.exception ?: Exception("Login failed")))
            }
    }

    // 🔥 GET USER
    fun getUser(
        uid: String,
        onResult: (Result<Map<String, Any>?>) -> Unit
    ) {
        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { onResult(Result.success(it.data)) }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    fun getCurrentUserUid(): String? = auth.currentUser?.uid
    fun isEmailVerified(): Boolean = auth.currentUser?.isEmailVerified == true
    fun signOut() = auth.signOut()

    // 🔥 PASSWORD RESET
    fun sendPasswordReset(
        email: String,
        onResult: (Result<String>) -> Unit
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener {
                if (it.isSuccessful) onResult(Result.success("Reset link sent"))
                else onResult(Result.failure(it.exception ?: Exception("Error")))
            }
    }

    // 🔥 RESEND EMAIL VERIFICATION
    fun resendEmailVerification(
        onResult: (Result<String>) -> Unit
    ) {
        val user = auth.currentUser

        if (user == null) {
            onResult(Result.failure(Exception("Session expired. Please signup again.")))
            return
        }

        user.reload().addOnCompleteListener {
            user.sendEmailVerification()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onResult(Result.success("Verification email sent ✔"))
                    } else {
                        onResult(Result.failure(task.exception ?: Exception("Failed ❌")))
                    }
                }
        }
    }
}
