// file: JournalEntry.kt
package np.com.bimalkafle.firebaseauth

import com.google.firebase.Timestamp

data class JournalEntry(
    val content: String = "",
    val uid: String = "",
    val timestamp: Timestamp? = null
)