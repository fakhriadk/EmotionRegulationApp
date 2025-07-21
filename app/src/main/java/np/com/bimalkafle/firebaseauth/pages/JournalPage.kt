// file: pages/JournalPage.kt
package np.com.bimalkafle.firebaseauth.pages

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import np.com.bimalkafle.firebaseauth.JournalEntry
import np.com.bimalkafle.firebaseauth.JournalViewModel
import np.com.bimalkafle.firebaseauthdemoapp.R
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalPage(navController: NavController, journalViewModel: JournalViewModel) {
    var text by remember { mutableStateOf("") }
    val entries by journalViewModel.entries
    val context = LocalContext.current
    var isSaving by remember { mutableStateOf(false) }

    // --- LOGIKA BARU UNTUK DIALOG PREMIUM ---
    var showPremiumDialog by remember { mutableStateOf(false) }
    // ------------------------------------------

    LaunchedEffect(journalViewModel) {
        journalViewModel.startListening()
    }

    DisposableEffect(journalViewModel) {
        onDispose {
            journalViewModel.stopListening()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.journal_page_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Input section
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text(stringResource(id = R.string.journal_textfield_placeholder)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (text.isNotBlank() && !isSaving) {
                        // --- PERUBAHAN LOGIKA DI SINI ---
                        // Cek apakah pengguna bisa menambah entri lagi
                        if (!journalViewModel.canAddMoreEntries) {
                            showPremiumDialog = true
                        } else {
                            // Jika bisa, jalankan logika penyimpanan seperti biasa
                            isSaving = true
                            journalViewModel.addJournalEntry(text) { success ->
                                if (success) {
                                    text = "" // Clear text field on success
                                    Toast.makeText(context, "Entry saved!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Failed to save entry.", Toast.LENGTH_SHORT).show()
                                }
                                isSaving = false
                            }
                        }
                    }
                },
                modifier = Modifier.align(Alignment.End),
                enabled = text.isNotBlank() && !isSaving,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFFA5D6A7),
                    disabledContentColor = Color.White.copy(alpha = 0.7f)
                )
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text(stringResource(id = R.string.journal_save_button))
                }
            }

            // Divider and past entries list
            Divider(modifier = Modifier.padding(vertical = 16.dp))
            Text(
                text = stringResource(id = R.string.past_entries_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(entries) { entry ->
                    JournalEntryCard(entry = entry)
                }
            }
        }
    }

    // --- PENAMBAHAN DIALOG PREMIUM ---
    if (showPremiumDialog) {
        PremiumDialog(
            onDismiss = { showPremiumDialog = false },
            navController = navController // Teruskan NavController
        )
    }
}

@Composable
fun JournalEntryCard(entry: JournalEntry) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = entry.content,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = formatTimestamp(entry.timestamp),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

private fun formatTimestamp(timestamp: com.google.firebase.Timestamp?): String {
    if (timestamp == null) return "Just now"
    val sdf = SimpleDateFormat("E, dd MMM yyyy - h:mm a", Locale.getDefault())
    return sdf.format(timestamp.toDate())
}