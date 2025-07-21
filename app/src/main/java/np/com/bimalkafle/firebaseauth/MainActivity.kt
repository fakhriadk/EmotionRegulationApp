// file: MainActivity.kt
package np.com.bimalkafle.firebaseauth

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.google.firebase.FirebaseApp
import np.com.bimalkafle.firebaseauth.ui.theme.FirebaseAuthDemoAppTheme

class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    // Kita tidak lagi butuh ChatViewModel di sini

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        authViewModel.configureGoogleSignIn(this)
        enableEdgeToEdge()

        setContent {
            FirebaseAuthDemoAppTheme {
                // Panggil MyAppNavigation tanpa parameter yang tidak perlu
                MyAppNavigation(
                    authViewModel = authViewModel
                )
            }
        }
    }
}