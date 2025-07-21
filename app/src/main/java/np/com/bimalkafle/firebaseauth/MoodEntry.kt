package np.com.bimalkafle.firebaseauth

data class MoodEntry(
    val uid: String = "",
    val moodValue: Int = 0,
    val dateString: String = "",
    val timestamp: Long? = null // Ini adalah versi yang benar dan aman
)