import android.content.Context
import android.util.Log
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object AuthUtils {

    suspend fun handleLogout(context: Context) {
        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser

        currentUser?.providerData?.forEach { userInfo ->
            when (userInfo.providerId) {
                "google.com" -> {
                    try {
                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken("YOUR_WEB_CLIENT_ID")
                            .requestEmail()
                            .build()
                        val googleSignInClient = GoogleSignIn.getClient(context, gso)
                        googleSignInClient.signOut().await()
                        Log.d("Logout", "Google sign out successful")
                    } catch (e: Exception) {
                        Log.e("Logout", "Google sign out failed", e)
                    }
                }
                "facebook.com" -> {
                    try {
                        LoginManager.getInstance().logOut()
                        Log.d("Logout", "Facebook logout successful")
                    } catch (e: Exception) {
                        Log.e("Logout", "Facebook logout failed", e)
                    }
                }
                "password" -> {
                    Log.d("Logout", "Email/Password logout: no external provider logout needed")
                }
            }
        }

        currentUser?.uid?.let { uid ->
            val logoutData = hashMapOf(
                "userId" to uid,
                "event" to "logout",
                "timestamp" to FieldValue.serverTimestamp()
            )
            try {
                withContext(Dispatchers.IO) {
                    FirebaseFirestore.getInstance()
                        .collection("user_activity")
                        .add(logoutData)
                        .await()
                }
                Log.d("Logout", "Logout time saved.")
            } catch (e: Exception) {
                Log.e("Logout", "Failed to save logout time.", e)
            }
        }

        firebaseAuth.signOut()

        if (firebaseAuth.currentUser == null) {
            Log.d("Logout", "User is logged out")
        } else {
            Log.d("Logout", "User is still logged in")
        }
    }
}
