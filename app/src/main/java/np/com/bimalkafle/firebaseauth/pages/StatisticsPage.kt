// file: pages/StatisticsPage.kt
package np.com.bimalkafle.firebaseauth.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.github.tehras.charts.bar.BarChart
import com.github.tehras.charts.bar.BarChartData
import com.github.tehras.charts.bar.renderer.label.SimpleValueDrawer
import com.github.tehras.charts.bar.renderer.xaxis.SimpleXAxisDrawer
import com.github.tehras.charts.bar.renderer.yaxis.SimpleYAxisDrawer
import np.com.bimalkafle.firebaseauth.MoodEntry
import np.com.bimalkafle.firebaseauth.StatisticsViewModel
import np.com.bimalkafle.firebaseauthdemoapp.R
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*
import kotlin.math.roundToInt

@Composable
fun StatisticsPage(navController: NavController, statisticsViewModel: StatisticsViewModel) {
    val uiState by statisticsViewModel.uiState.collectAsState()

    // --- LOGIKA BARU UNTUK MENGHITUNG STATISTIK ---
    val weeklyMoods = uiState.moods.filter {
        val entryDate = LocalDate.parse(it.dateString)
        val startOfWeek = LocalDate.now().with(DayOfWeek.MONDAY)
        !entryDate.isBefore(startOfWeek)
    }

    val averageMoodValue = if (weeklyMoods.isNotEmpty()) {
        weeklyMoods.map { it.moodValue }.average().roundToInt()
    } else {
        0
    }

    val mostFrequentMoodValue = if (weeklyMoods.isNotEmpty()) {
        weeklyMoods.groupingBy { it.moodValue }.eachCount().maxByOrNull { it.value }?.key
    } else {
        null
    }

    val averageMoodEmoji = moodValueToEmoji(averageMoodValue)
    val mostFrequentMoodEmoji = moodValueToEmoji(mostFrequentMoodValue)
    // ----------------------------------------------

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(id = R.string.statistics_title),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(24.dp))

        MoodCalendarCard(
            moods = uiState.moods,
            onDayClicked = { date, moodValue ->
                statisticsViewModel.logOrUpdateMood(date, moodValue)
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- BAGIAN BARU: KARTU STATISTIK MINGGUAN ---
        Text(
            text = stringResource(id = R.string.this_week),
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard(
                label = stringResource(id = R.string.average_mood), // <-- Menggunakan string resource
                value = averageMoodEmoji,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = stringResource(id = R.string.most_frequent_mood), // <-- Menggunakan string resource
                value = mostFrequentMoodEmoji,
                modifier = Modifier.weight(1f)
            )
        }
        // -------------------------------------------

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(id = R.string.mood_consistency),
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        MoodChart(moods = uiState.moods)
    }
}

// --- FUNGSI BANTU BARU ---
private fun moodValueToEmoji(moodValue: Int?): String {
    return when (moodValue) {
        1 -> "😢"
        2 -> "😕"
        3 -> "😐"
        4 -> "🙂"
        5 -> "😊"
        else -> "-" // Tanda strip jika tidak ada data
    }
}


// --- COMPOSABLE BARU (Dihidupkan kembali) ---
@Composable
fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = value, fontSize = 32.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = label, fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center)
        }
    }
}


// --- COMPOSABLE YANG SUDAH ADA (TIDAK BERUBAH) ---

@Composable
fun MoodCalendarCard(moods: List<MoodEntry>, onDayClicked: (LocalDate, Int) -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val today = LocalDate.now()
            val startOfWeek = today.with(DayOfWeek.MONDAY)
            val days = (0..6).map { startOfWeek.plusDays(it.toLong()) }
            val moodsMap = moods.associateBy({ it.dateString }, { it.moodValue })

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                days.forEach { day ->
                    DayCell(
                        day = day,
                        isToday = day == today,
                        moodValue = moodsMap[day.toString()]
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Divider()
            Spacer(modifier = Modifier.height(12.dp))

            Text(text = stringResource(id = R.string.tap_to_log_mood), fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("😢", "😕", "😐", "🙂", "😊").forEachIndexed { index, emoji ->
                    Text(
                        text = emoji,
                        fontSize = 32.sp,
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { onDayClicked(today, index + 1) }
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DayCell(day: LocalDate, isToday: Boolean, moodValue: Int?) {
    val emoji = moodValueToEmoji(moodValue)

    val backgroundColor = when (moodValue) {
        1 -> Color(0xFFFFCDD2)
        2 -> Color(0xFFFFF9C4)
        3 -> Color(0xFFF5F5F5)
        4 -> Color(0xFFC8E6C9)
        5 -> Color(0xFFA5D6A7)
        else -> if (isToday) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = day.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isToday) MaterialTheme.colorScheme.primary else Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(backgroundColor)
                .border(
                    width = if (isToday) 1.5.dp else 0.dp,
                    color = if (isToday) MaterialTheme.colorScheme.primary else Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (emoji != "-") { // Tampilkan emoji jika bukan strip
                Text(text = emoji, fontSize = 22.sp)
            } else {
                Text(
                    text = day.dayOfMonth.toString(),
                    fontSize = 14.sp,
                    color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
fun MoodChart(moods: List<MoodEntry>) {
    val today = LocalDate.now()
    val startOfWeek = today.with(DayOfWeek.MONDAY)
    val days = (0..6).map { startOfWeek.plusDays(it.toLong()) }

    val moodsMap = moods.associateBy({ it.dateString }, { it.moodValue.toFloat() })

    val barChartData = BarChartData(
        bars = days.map { day ->
            BarChartData.Bar(
                label = day.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                value = moodsMap[day.toString()] ?: 0f,
                color = when (moodsMap[day.toString()]?.toInt()) {
                    1 -> Color.Red.copy(alpha = 0.6f)
                    2 -> Color.Yellow.copy(alpha = 0.8f)
                    3 -> Color.Gray.copy(alpha = 0.6f)
                    4 -> Color(0xFF66BB6A)
                    5 -> Color(0xFF2E7D32)
                    else -> Color.LightGray.copy(alpha = 0.5f)
                }
            )
        }
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth().height(200.dp)
    ) {
        BarChart(
            barChartData = barChartData,
            modifier = Modifier.padding(16.dp),
            yAxisDrawer = SimpleYAxisDrawer(labelValueFormatter = { " " }),
            xAxisDrawer = SimpleXAxisDrawer(),
            labelDrawer = SimpleValueDrawer()
        )
    }
}