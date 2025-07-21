// file: pages/PremiumPage.kt
package np.com.bimalkafle.firebaseauth.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumPage(navController: NavController) {
    val goldColor = Color(0xFFD4AF37)
    val darkBgColor = Color(0xFF1C1C1E)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CalmBot Premium", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = darkBgColor)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(darkBgColor)
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Premium",
                tint = goldColor,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Buka Potensi Penuh Ketenangan Anda",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Upgrade ke Premium untuk mendapatkan akses tanpa batas ke semua fitur pendukung kesehatan mental.",
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))

            // Daftar Fitur Premium
            FeatureListItem(text = "Jurnal Harian Tanpa Batas")
            FeatureListItem(text = "Analisis Statistik Mood Mendalam")
            FeatureListItem(text = "Akses ke Semua Konten Relaksasi")
            FeatureListItem(text = "Pengalaman Bebas Iklan")

            Spacer(modifier = Modifier.height(40.dp))

            // Kartu Bulanan
            SubscriptionCard(
                title = "Bulanan",
                price = "Rp 50.000 / bulan",
                goldColor = goldColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Kartu Tahunan
            SubscriptionCard(
                title = "Tahunan",
                price = "Rp 480.000 / tahun",
                goldColor = goldColor
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Pembayaran akan diproses melalui Google Play. Anda bisa berhenti kapan saja.",
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun FeatureListItem(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text, color = Color.White, fontSize = 16.sp)
    }
}

@Composable
private fun SubscriptionCard(title: String, price: String, goldColor: Color) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.DarkGray.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(title, color = Color.White, fontWeight = FontWeight.SemiBold)
                Text(price, color = goldColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = { /* Upgrade action */ },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = goldColor),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                modifier = Modifier
                    .defaultMinSize(minWidth = 1.dp)
                    .widthIn(min = 100.dp, max = 150.dp)
                    .height(40.dp)
            ) {
                Text("Pilih Paket", color = Color.Black, fontSize = 12.sp)
            }
        }
    }
}
