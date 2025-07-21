package np.com.bimalkafle.firebaseauth.ui.theme

data class MessageModel(
    val message: String = "",
    val role: String = "", // "user" or "model"
    val timestamp: Long = System.currentTimeMillis() // For sorting
)
