// file: pages/HomePage.kt
package np.com.bimalkafle.firebaseauth.pages

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import kotlinx.coroutines.delay
import np.com.bimalkafle.firebaseauth.AuthState
import np.com.bimalkafle.firebaseauth.AuthViewModel
import np.com.bimalkafle.firebaseauth.UserStatus
import np.com.bimalkafle.firebaseauthdemoapp.R

// --- DATA CLASSES ---
data class Article(val title: String, val source: String, val readTime: String, val url: String)
private data class MindfulnessVideo(val title: String, val source: String, val url: String)
private data class SoothingAudio(val title: String, val description: String, val url: String)
data class Quote(val text: String, val author: String)

@Composable
fun HomePage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {
    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current
    var showPremiumDialog by remember { mutableStateOf(false) }

    // --- DATA INITIALIZATION MOVED HERE (THE CORRECT PLACE) ---
    val quotes = remember {
        listOf(
            Quote("You don't have to control your thoughts. You just have to stop letting them control you.", "Dan Millman"),
            Quote("Anxiety's like a rocking chair. It gives you something to do, but it doesn't get you very far.", "Jodi Picoult"),
            Quote("The greatest weapon against stress is our ability to choose one thought over another.", "William James"),
            Quote("It’s not the load that breaks you down, it’s the way you carry it.", "Lou Holtz"),
            Quote("Feel the feeling but don't become the emotion. Witness it. Allow it. Release it.", "Unknown"),
            Quote("Your anxiety is lying to you. You are loved and you are going to be okay.", "Unknown")
        )
    }

    val freeArticle = Article(
        title = stringResource(id = R.string.article_2_title),
        source = stringResource(id = R.string.article_2_source),
        readTime = stringResource(id = R.string.article_2_time),
        url = "https://rsabhk.co.id/artikel-kesehatan/bagaimana-mengelola-dan-mengatasi-kecemasan-yang-dirasakan/"
    )
    val premiumArticle = Article(
        title = stringResource(id = R.string.article_1_title),
        source = stringResource(id = R.string.article_1_source),
        readTime = stringResource(id = R.string.article_1_time),
        url = "https://www.mind.org.uk/information-support/types-of-mental-health-problems/anxiety-and-panic-attacks/self-care/"
    )
    val freeVideo = MindfulnessVideo(
        title = stringResource(id = R.string.video_2_title),
        source = stringResource(id = R.string.video_2_source),
        url = "https://youtu.be/4ffr26sUTLI?si=MsR1QjG5w1ozHSoU"
    )
    val premiumVideo = MindfulnessVideo(
        title = stringResource(id = R.string.video_1_title),
        source = stringResource(id = R.string.video_1_source),
        url = "https://www.youtube.com/watch?v=O-6f5wQXSu8"
    )
    val freeAudio = SoothingAudio(
        title = stringResource(id = R.string.audio_2_title),
        description = stringResource(id = R.string.audio_2_description),
        url = "https://www.youtube.com/watch?v=yIQd2Ya0Ziw"
    )
    val premiumAudio = SoothingAudio(
        title = stringResource(id = R.string.audio_1_title),
        description = stringResource(id = R.string.audio_1_description),
        url = "https://www.youtube.com/watch?v=zPyg4N7bcHM"
    )
    // -----------------------------------------------------------------

    var currentQuote by remember { mutableStateOf(quotes.random()) }

    LaunchedEffect(key1 = true) {
        while (true) {
            delay(20000)
            currentQuote = quotes.random()
        }
    }

    val scrollState = rememberScrollState()

    LaunchedEffect(authState.value) {
        if (authState.value is AuthState.Unauthenticated) {
            navController.navigate("login")
        }
    }

    if (showPremiumDialog) {
        PremiumDialog(
            onDismiss = { showPremiumDialog = false },
            navController = navController // Teruskan NavController
        )
    }

    Column(
        modifier = modifier.fillMaxSize().background(Color(0xFFF8F8F8)).padding(20.dp).verticalScroll(scrollState)
    ) {
        // --- HEADER ---
        Text(
            text = stringResource(id = R.string.dashboard),
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(20.dp))

        // --- QUOTE CARD SECTION ---
        QuoteCard(quote = currentQuote)
        Spacer(modifier = Modifier.height(24.dp))

        // --- ACTIVITIES SECTION ---
        Text(
            text = stringResource(id = R.string.activities),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Journaling Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
            modifier = Modifier.fillMaxWidth().clickable { navController.navigate("journal") }
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = stringResource(id = R.string.journal_title), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = stringResource(id = R.string.journal_subtitle), fontSize = 14.sp, color = Color.Gray)
                }
                Surface(color = Color(0xFFC8E6C9), shape = RoundedCornerShape(12.dp)) {
                    Text(text = stringResource(id = R.string.journal_tag), color = Color(0xFF2E7D32), fontSize = 12.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        // Breathing Exercise Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6)),
            modifier = Modifier.fillMaxWidth().clickable { navController.navigate("breathing") }
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = stringResource(id = R.string.breathing_exercise_title), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = stringResource(id = R.string.breathing_exercise_subtitle), fontSize = 14.sp, color = Color.Gray)
                }
                Surface(color = Color(0xFFD1C4E9), shape = RoundedCornerShape(12.dp)) {
                    Text(text = stringResource(id = R.string.mindfulness_tag), color = Color(0xFF512DA8), fontSize = 12.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        // Chat with Bot Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
            modifier = Modifier.fillMaxWidth().clickable {
                navController.navigate("chatbot") {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = stringResource(id = R.string.chat_bot_title), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = stringResource(id = R.string.chat_bot_subtitle), fontSize = 14.sp, color = Color.Gray)
                }
                Surface(color = Color(0xFFBBDEFB), shape = RoundedCornerShape(12.dp)) {
                    Text(text = stringResource(id = R.string.ai_support_tag), color = Color(0xFF1976D2), fontSize = 12.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                }
            }
        }

        // --- FIXED SPACING AND PREMIUM LOGIC ---
        Spacer(modifier = Modifier.height(12.dp)) // Spasi konsisten

        // Mood Tracking Card
        Card(
            shape = RoundedCornerShape(16.dp),
            // Warna latar baru: Biru keunguan yang sangat muda dan sejuk
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2F1)),
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    if (UserStatus.isPremium) {
                        navController.navigate("statistics")
                    } else {
                        showPremiumDialog = true
                    }
                }
        ) {
            Box {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Mood Tracking", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "See your emotional patterns", fontSize = 14.sp, color = Color.Gray)
                    }
                    Surface(
                        color = Color(0xFFB2DFDB),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text(
                            text = "ANALYTICS",
                            // Warna teks baru: Biru indigo yang kuat
                            color = Color(0xFF00796B),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold, // Sedikit lebih tebal agar kontras
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
                if (!UserStatus.isPremium) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Premium Feature",
                        tint = Color.Black.copy(alpha = 0.5f),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp)) // Spasi konsisten
        // --- NEW COMMUNITY CARD ADDED HERE ---
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFFCE4EC)), modifier = Modifier.fillMaxWidth().clickable { navController.navigate("community") }) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Community Support", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Connect with others", fontSize = 14.sp, color = Color.Gray)
                }
                Surface(color = Color(0xFFF8BBD0), shape = RoundedCornerShape(12.dp)) {
                    Text(text = "SUPPORT", color = Color(0xFFAD1457), fontSize = 12.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                }
            }
        }


        Spacer(modifier = Modifier.height(24.dp)) // Spasi konsisten

        // --- Short Reads Section ---
        Text(text = stringResource(id = R.string.short_reads), fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
        Text(text = stringResource(id = R.string.short_reads_subtitle), fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                ShortReadCard(article = freeArticle)
            }
            Box(modifier = Modifier.weight(1f)) {
                LockedContentCard(isPremium = UserStatus.isPremium, onLockedClick = { showPremiumDialog = true }, onContentClick = { val intent = Intent(Intent.ACTION_VIEW, Uri.parse(premiumArticle.url)); context.startActivity(intent) }) {
                    ShortReadCard(article = premiumArticle)
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        // --- Mindfulness Videos Section ---
        Text(text = stringResource(id = R.string.mindfulness_section_title), fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
        Text(text = stringResource(id = R.string.mindfulness_section_subtitle), fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            MindfulnessCard(video = freeVideo)
            LockedContentCard(isPremium = UserStatus.isPremium, onLockedClick = { showPremiumDialog = true }, onContentClick = { val intent = Intent(Intent.ACTION_VIEW, Uri.parse(premiumVideo.url)); context.startActivity(intent) }) {
                MindfulnessCard(video = premiumVideo)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        // --- Soothing Sounds Section ---
        Text(text = stringResource(id = R.string.soothing_audio_section_title), fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
        Text(text = stringResource(id = R.string.soothing_audio_section_subtitle), fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            AudioCard(audio = freeAudio)
            LockedContentCard(isPremium = UserStatus.isPremium, onLockedClick = { showPremiumDialog = true }, onContentClick = { val intent = Intent(Intent.ACTION_VIEW, Uri.parse(premiumAudio.url)); context.startActivity(intent) }) {
                AudioCard(audio = premiumAudio)
            }
        }
    }
}


@Composable
fun LockedContentCard(
    isPremium: Boolean,
    onLockedClick: () -> Unit,
    onContentClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier.clickable {
            if (isPremium) {
                onContentClick()
            } else {
                onLockedClick()
            }
        }
    ) {
        // Konten di dalam dibuat tidak bisa diklik secara individual
        Box(modifier = Modifier.alpha(if (isPremium) 1f else 0.5f)) {
            content()
        }

        if (!isPremium) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Premium Feature",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}

// --- NEW COMPOSABLE FOR QUOTE CARD ---
@Composable
fun QuoteCard(quote: Quote) {
    // Warna emas yang lebih lembut dan hangat
    val goldAccentColor = Color(0xFFC0A062)
    // Warna latar belakang yang sangat muda dan hangat, hampir putih
    val cardBackgroundColor = Color(0xFFFFFBEF)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Tanda kutip pembuka
            Icon(
                painter = painterResource(id = R.drawable.ic_format_quote),
                contentDescription = "Opening quote",
                tint = goldAccentColor.copy(alpha = 0.5f), // Dibuat sedikit transparan
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Teks kutipan
            Text(
                text = quote.text,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface, // Warna teks utama
                fontStyle = FontStyle.Italic,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center, // Dibuat di tengah agar lebih seimbang
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Penulis kutipan
            Text(
                text = "— ${quote.author}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                color = goldAccentColor,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// --- EXISTING COMPOSABLES ---
@Composable
private fun AudioCard(audio: SoothingAudio) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF0EBF8)), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.GraphicEq, contentDescription = "Listen to Audio", tint = Color(0xFF5E35B1), modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = audio.title, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                Text(text = audio.description, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
private fun MindfulnessCard(video: MindfulnessVideo) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.PlayCircleOutline, contentDescription = "Play Video", tint = Color(0xFF2E7D32), modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = video.title, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Language, contentDescription = "Language", tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = video.source, fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun ShortReadCard(article: Article) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.height(130.dp), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Column(modifier = Modifier.padding(12.dp).fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween) {
            Text(text = article.title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Black, maxLines = 2)
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccessTime, contentDescription = "Read Time", tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = article.readTime, fontSize = 12.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Language, contentDescription = "Source", tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = article.source, fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}