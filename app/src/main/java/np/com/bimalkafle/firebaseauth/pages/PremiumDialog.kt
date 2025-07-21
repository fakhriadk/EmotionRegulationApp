// file: pages/PremiumDialog.kt
package np.com.bimalkafle.firebaseauth.pages

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController

@Composable
fun PremiumDialog(onDismiss: () -> Unit, navController: NavController) { // Tambahkan NavController
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Star, contentDescription = "Premium Feature", tint = Color(0xFFD4AF37)) },
        title = { Text(text = "Premium Feature Locked", fontWeight = FontWeight.Bold) },
        text = { Text("Upgrade to CalmBot Premium to unlock unlimited journal entries, in-depth statistics, and all relaxation content.") },
        confirmButton = {
            Button(
                onClick = {
                    onDismiss()
                    navController.navigate("premium")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4AF37))
            ) {
                Text("Upgrade Now")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Maybe Later")
            }
        }
    )
}