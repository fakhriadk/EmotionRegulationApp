// file: pages/ChatPage.kt
package np.com.bimalkafle.firebaseauth.pages

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import np.com.bimalkafle.firebaseauth.ui.theme.ChatViewModel
import np.com.bimalkafle.firebaseauth.ui.theme.MessageModel
import np.com.bimalkafle.firebaseauthdemoapp.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun ChatPage(chatViewModel: ChatViewModel) {

    // --- LOGIKA BARU ---
    // State untuk mengontrol gelembung saran secara lokal
    var showSuggestionBubbles by remember { mutableStateOf(true) }

    // Mulai mendengarkan data saat halaman pertama kali dibuka
    LaunchedEffect(Unit) {
        chatViewModel.startListening()
    }

    // Hentikan listener saat meninggalkan halaman
    DisposableEffect(Unit) {
        onDispose {
            chatViewModel.stopListening()
        }
    }
    // -------------------

    val currentTime = remember {
        val simpleDateFormat = SimpleDateFormat("E h:mm a", Locale.getDefault())
        simpleDateFormat.format(Date())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
    ) {
        AppHeader()
        DateLabel(currentTime)
        MessageList(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            messageList = chatViewModel.messageList
        )

        // Tampilkan gelembung jika state-nya true
        if (showSuggestionBubbles) {
            SuggestionBubbles(onSuggestionClick = { suggestion ->
                chatViewModel.sendMessage(suggestion)
                showSuggestionBubbles = false // Sembunyikan setelah diklik
            })
        }

        MessageInput(onMessageSend = {
            chatViewModel.sendMessage(it)
            showSuggestionBubbles = false // Sembunyikan setelah mengirim pesan
        })
    }
}

// --- Composable Baru untuk Pesan Selamat Datang Palsu ---
@Composable
fun WelcomeMessagePlaceholder(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        // Kita menggunakan MessageRow agar tampilannya konsisten
        MessageRow(
            messageModel = MessageModel(
                message = "Halo, aku CalmBot! 👋 Aku asisten pribadimu untuk membantu mengelola emosi dan kecemasan. Apa yang bisa aku bantu hari ini?",
                role = "model",
                timestamp = 0L // Timestamp tidak penting karena ini hanya placeholder
            )
        )
    }
}


// --- Composable yang Sudah Ada (Tidak Banyak Berubah) ---

@Composable
fun SuggestionBubbles(onSuggestionClick: (String) -> Unit) {
    val suggestions = listOf(
        "Bagaimana cara mengatasi panic attack?",
        "Aku sedang merasa cemas nih, apakah boleh cerita?",
        "Beri aku tips mengelola kecemasan",
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 12.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(suggestions) { suggestion ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, Color.Gray, RoundedCornerShape(20.dp))
                    .clickable { onSuggestionClick(suggestion) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(text = suggestion, fontSize = 13.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun AppHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.bot_icon),
            contentDescription = "Bot Icon",
            modifier = Modifier
                .size(40.dp)
//                .clip(CircleShape),
//            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = "CalmBot", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color.Green)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Always active", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun DateLabel(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun MessageList(modifier: Modifier = Modifier, messageList: List<MessageModel>) {
    LazyColumn(
        modifier = modifier,
        reverseLayout = true
    ) {
        items(messageList.reversed()) {
            MessageRow(messageModel = it)
        }
    }
}

@Composable
fun MessageRow(messageModel: MessageModel) {
    val isModel = messageModel.role == "model"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = if (isModel) Arrangement.Start else Arrangement.End,
        verticalAlignment = Alignment.Bottom
    ) {
        if (isModel) {
            Image(
                painter = painterResource(id = R.drawable.bot_icon),
                contentDescription = "Bot Icon",
                modifier = Modifier
                    .size(32.dp)
//                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(if (isModel) Color(0xFFF1F1F1) else Color(0xFF0F75FF))
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .widthIn(max = 260.dp)
        ) {
            SelectionContainer {
                Text(
                    text = messageModel.message,
                    fontSize = 15.sp,
                    color = if (isModel) Color.Black else Color.White
                )
            }
        }
        if (!isModel) {
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
fun MessageInput(onMessageSend: (String) -> Unit) {
    var message by remember { mutableStateOf("") }

    Surface {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = message,
                onValueChange = { message = it },
                placeholder = { Text("Type a message...") },
                shape = RoundedCornerShape(28.dp),
                maxLines = 2,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black,
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent
                )
            )
            IconButton(
                onClick = {
                    if (message.isNotBlank()) {
                        onMessageSend(message)
                        message = ""
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = Color(0xFF0F75FF)
                )
            }
        }
    }
}