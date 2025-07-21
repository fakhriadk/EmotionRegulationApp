// file: JournalViewModel.kt
package np.com.bimalkafle.firebaseauth

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class JournalViewModel(private val authViewModel: AuthViewModel) : ViewModel() {

    val canAddMoreEntries: Boolean
        get() = UserStatus.isPremium || _entries.value.size < 3

    private val db = FirebaseFirestore.getInstance()
    private var journalListener: ListenerRegistration? = null

    private val _entries = mutableStateOf<List<JournalEntry>>(emptyList())
    val entries: State<List<JournalEntry>> = _entries

    fun startListening() {
        stopListening()
        val uid = authViewModel.currentUser?.uid ?: return

        journalListener = db.collection("journals")
            .whereEqualTo("uid", uid)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("JournalViewModel", "Listen failed.", error)
                    return@addSnapshotListener
                }
                snapshot?.let {
                    _entries.value = it.toObjects(JournalEntry::class.java)
                }
            }
    }

    fun stopListening() {
        journalListener?.remove()
    }

    override fun onCleared() {
        super.onCleared()
        stopListening()
    }

    fun addJournalEntry(content: String, onComplete: (Boolean) -> Unit) {
        if (!canAddMoreEntries) {
            onComplete(false)
            return
        }
        val uid = authViewModel.currentUser?.uid
        if (uid == null || content.isBlank()) {
            onComplete(false)
            return
        }

        val newEntry = JournalEntry(
            content = content,
            uid = uid,
            timestamp = Timestamp.now()
        )

        // Tambahkan entri baru ke bagian atas daftar lokal secara langsung.
        _entries.value = listOf(newEntry) + _entries.value

        // 2. KIRIM KE SERVER DI LATAR BELAKANG
        db.collection("journals").add(newEntry)
            .addOnSuccessListener {
                // Saat berhasil, listener akan otomatis menyinkronkan data,
                // jadi kita tidak perlu melakukan apa-apa lagi di sini.
                Log.d("JournalViewModel", "Entry saved successfully to Firestore.")
                onComplete(true)
            }
            .addOnFailureListener { e ->
                // Jika gagal, hapus entri optimistis yang tadi kita tambahkan.
                _entries.value = _entries.value.filterNot { it.timestamp == newEntry.timestamp }
                Log.e("JournalViewModel", "Error adding document", e)
                onComplete(false)
            }
    }
}

// Factory tidak berubah
class JournalViewModelFactory(private val authViewModel: AuthViewModel) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JournalViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return JournalViewModel(authViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}