// file: pages/ProfilePage.kt
package np.com.bimalkafle.firebaseauth.pages

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import np.com.bimalkafle.firebaseauth.AuthState
import np.com.bimalkafle.firebaseauth.AuthViewModel
import np.com.bimalkafle.firebaseauth.LocaleHelper
import np.com.bimalkafle.firebaseauth.UserStatus
import np.com.bimalkafle.firebaseauthdemoapp.R

@Composable
fun ProfilePage(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var showSignOutDialog by remember { mutableStateOf(false) }
    var showFeedbackDialog by remember { mutableStateOf(false) }

    // Ini adalah listener yang penting.
    val authState by authViewModel.authState.observeAsState()

    val context = LocalContext.current
    val activity = context as? Activity
    var isPremium by remember { mutableStateOf(UserStatus.isPremium) }
    val currentUser = authViewModel.currentUser

    // --- INI ADALAH PERBAIKAN UTAMA ---
    // LaunchedEffect ini akan bereaksi setiap kali authState berubah.
    LaunchedEffect(authState) {
        // Jika state berubah menjadi Unauthenticated (setelah signout), navigasi ke login.
        if (authState is AuthState.Unauthenticated) {
            navController.navigate("login") {
                // Hapus semua halaman sebelumnya dari tumpukan agar pengguna tidak bisa kembali.
                popUpTo(navController.graph.id) { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // --- PROFILE HEADER ---
        Text(
            text = stringResource(id = R.string.profile),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.placeholder_profile),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(120.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = currentUser?.displayName ?: stringResource(id = R.string.premium_user),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = currentUser?.email ?: "",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(40.dp))

        // --- SETTINGS CARD ---
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // --- FIX: Gunakan 'context' yang didapat dari LocalContext ---
                val currentLang = LocaleHelper.getLanguage(context)
                SettingRowSwitch(
                    icon = Icons.Default.Language,
                    title = stringResource(id = R.string.language),
                    subtitle = if (currentLang == "in") stringResource(id = R.string.language_indonesian) else stringResource(id = R.string.language_english),
                    checked = currentLang == "in",
                    onCheckedChange = { isChecked ->
                        val newLang = if (isChecked) "in" else "en"
                        LocaleHelper.setLocale(context, newLang)
                        activity?.recreate()
                    }
                )
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
                SettingRowClickable(
                    icon = Icons.Default.Feedback,
                    title = stringResource(id = R.string.send_feedback),
                    onClick = { showFeedbackDialog = true }
                )
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
                SettingRowClickable(
                    icon = Icons.Default.Policy,
                    title = "Privacy Policy",
                    onClick = { navController.navigate("privacy_policy") }
                )
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
                SettingRowSwitch(
                    icon = Icons.Default.Star,
                    title = "Premium User (Debug)",
                    subtitle = if (isPremium) "Status: Premium" else "Status: Free",
                    checked = isPremium,
                    onCheckedChange = { newStatus ->
                        isPremium = newStatus
                        UserStatus.isPremium = newStatus
                        Toast.makeText(context, "User status set to ${if(newStatus) "Premium" else "Free"}", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }



        // --- BOTTOM SECTION ---
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = { showSignOutDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
        ) {
            Text(
                text = stringResource(id = R.string.sign_out),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
        Text(
            text = "${stringResource(id = R.string.app_version)} 1.0.0",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "© 2025 Fakhri Andika. All Rights Reserved.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }

    // --- DIALOGS ---
    if (showSignOutDialog) {
        SignOutDialog(
            onConfirm = {
                authViewModel.signout()
                showSignOutDialog = false
            },
            onDismiss = { showSignOutDialog = false }
        )
    }

    if (showFeedbackDialog) {
        // PERBAIKAN: Memanggil dialog tanpa mengirim context
        FeedbackSelectionDialog(
            onDismiss = { showFeedbackDialog = false }
        )
    }
}

// --- Reusable Composables ---
@Composable
private fun SignOutDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(id = R.string.sign_out_confirm_title)) },
        text = { Text(stringResource(id = R.string.sign_out_confirm_message)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(id = R.string.yes), color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.no))
            }
        }
    )
}

// --- PERBAIKAN: Dialog sekarang mengambil context-nya sendiri ---
@Composable
private fun FeedbackSelectionDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current // <-- Mengambil context di sini

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.send_feedback)) },
        text = {
            Column {
                Text(
                    text = "Email",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth().clickable {
                        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:fakhriandika25@gmail.com"))
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "App Feedback")
                        context.startActivity(Intent.createChooser(emailIntent, "Send Feedback"))
                        onDismiss()
                    }.padding(vertical = 16.dp)
                )
                Divider()
                Text(
                    text = "WhatsApp",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth().clickable {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW)
                            val url = "https://api.whatsapp.com/send?phone=+6285156063121&text=" +
                                    Uri.encode("Halo, saya ingin memberikan feedback untuk aplikasi Anda.")
                            intent.data = Uri.parse(url)
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "WhatsApp is not installed.", Toast.LENGTH_SHORT).show()
                        }
                        onDismiss()
                    }.padding(vertical = 16.dp)
                )
                Divider()
                Text(
                    text = "Google Form",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth().clickable {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW)
                            val url = "https://docs.google.com/forms/d/e/1FAIpQLSdO90epvGt_KrMZDcvdVgRpn05FYrQQBZjqqhAZXtnqMB8bNw/viewform?usp=sharing"
                            intent.data = Uri.parse(url)
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "An error occurred.", Toast.LENGTH_SHORT).show()
                        }
                        onDismiss()
                    }.padding(vertical = 16.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun SettingRowSwitch(icon: ImageVector, title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = title, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
            Text(text = subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SettingRowClickable(icon: ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = title, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurface)
        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}