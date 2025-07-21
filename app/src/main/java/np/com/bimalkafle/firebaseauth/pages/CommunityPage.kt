// file: pages/CommunityPage.kt
package np.com.bimalkafle.firebaseauth.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import np.com.bimalkafle.firebaseauthdemoapp.R

data class CommunityMessage(
    val senderAlias: String,
    val message: String,
    val timestamp: String,
    val senderColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityPage(navController: NavController) {
    // Dummy data for the prototype
    val dummyMessages = listOf(
        CommunityMessage("Brave Rabbit", "Hari ini rasanya berat banget, ada yang pernah merasa cemas tanpa alasan jelas?", "10:30 AM", Color(0xFFE57373)),
        CommunityMessage("Wise Owl", "Sering kok. Biasanya aku coba fokus ke mengatur napasku, itu sedikit membantu.", "10:31 AM", Color(0xFF81C784)),
        CommunityMessage("Quiet Fish", "Terima kasih sudah berbagi, kamu tidak sendirian. Aku juga sering begitu.", "10:32 AM", Color(0xFF64B5F6)),
        CommunityMessage("You (Anonymous Panda)", "Sama, aku juga. Senang tahu ada yang merasakan hal yang sama.", "10:35 AM", MaterialTheme.colorScheme.primary),
        CommunityMessage("Kind Cat", "Semangat semuanya! Ingat, satu langkah kecil setiap hari itu sudah kemajuan.", "10:38 AM", Color(0xFFFFD54F))
    )

    val listState = rememberLazyListState()
    LaunchedEffect(dummyMessages.size) {
        listState.animateScrollToItem(dummyMessages.size)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Community Support (Mockup)") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            CommunityMessageInput()
        }
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            item {
                CommunityHeader()
                Spacer(modifier = Modifier.height(16.dp))
            }
            items(dummyMessages) { msg ->
                CommunityMessageRow(message = msg, isMyMessage = msg.senderAlias.startsWith("You"))
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun CommunityHeader() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.support), // 1. Menggunakan ID gambar barumu
            contentDescription = "Community Icon",
            // 2. Properti 'tint' dihapus agar warna asli PNG ditampilkan
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("Ruang Bersama", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(
            text = "Tempat aman untuk berbagi cerita secara anonim. Ingatlah untuk selalu baik dan saling mendukung.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
        Divider(modifier = Modifier.padding(top = 16.dp))
    }
}

@Composable
fun CommunityMessageRow(message: CommunityMessage, isMyMessage: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isMyMessage) Alignment.End else Alignment.Start
    ) {
        if (!isMyMessage) {
            Text(
                text = message.senderAlias,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = message.senderColor,
                modifier = Modifier.padding(start = 8.dp, bottom = 2.dp)
            )
        }
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = if (isMyMessage) 16.dp else 0.dp,
                        topEnd = if (isMyMessage) 0.dp else 16.dp,
                        bottomStart = 16.dp,
                        bottomEnd = 16.dp
                    )
                )
                .background(if (isMyMessage) MaterialTheme.colorScheme.primary else Color(0xFFF1F1F1))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = message.message,
                color = if (isMyMessage) Color.White else Color.Black
            )
        }
        Text(
            text = message.timestamp,
            fontSize = 10.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 4.dp, end = 8.dp, start = 8.dp)
        )
    }
}

@Composable
fun CommunityMessageInput() {
    Surface(shadowElevation = 8.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Send an encouraging message...") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            // The send button is disabled for this prototype
            IconButton(onClick = {}, enabled = false) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.Gray)
            }
        }
    }
}