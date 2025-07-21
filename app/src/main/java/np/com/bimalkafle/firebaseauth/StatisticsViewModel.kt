// file: StatisticsViewModel.kt
package np.com.bimalkafle.firebaseauth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// The MoodEntry data class from MoodEntry.kt is used here.
// Make sure it is: data class MoodEntry(val uid: String = "", ..., val timestamp: Long? = null)

class StatisticsViewModel(private val authViewModel: AuthViewModel) : ViewModel() {

    private val db = Firebase.firestore

    private val _uiState = MutableStateFlow(StatisticsState())
    val uiState = _uiState.asStateFlow()

    init {
        loadAllData()
    }

    private fun loadAllData() {
        loadJournalCount()
        loadMindfulnessCount()
        loadMoodsForWeek()
    }

    private fun loadJournalCount() {
        val uid = authViewModel.currentUser?.uid ?: return
        db.collection("journals")
            .whereEqualTo("uid", uid)
            .get()
            .addOnSuccessListener { snapshot ->
                _uiState.update { it.copy(journalEntries = snapshot.size()) }
            }
    }

    private fun loadMindfulnessCount() {
        _uiState.update { it.copy(mindfulnessSessions = 5) }
    }

    // --- THIS IS THE CRITICAL FIX: MANUAL PARSING ---
    private fun loadMoodsForWeek() {
        val uid = authViewModel.currentUser?.uid ?: return
        db.collection("moods")
            .whereEqualTo("uid", uid)
            .limit(30)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("FirestoreError", "Listen failed.", error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    // We will build the list manually instead of using toObjects()
                    val moodList = mutableListOf<MoodEntry>()
                    for (document in snapshot.documents) {
                        try {
                            // Safely get each field, providing a default value if it's null or the wrong type
                            val mood = MoodEntry(
                                uid = document.getString("uid") ?: "",
                                moodValue = document.getLong("moodValue")?.toInt() ?: 0,
                                dateString = document.getString("dateString") ?: "",
                                timestamp = document.getLong("timestamp") // This will be null if field is missing
                            )
                            moodList.add(mood)
                        } catch (e: Exception) {
                            // If a single document is malformed, log it and skip it instead of crashing
                            Log.e("FirestoreParseError", "Failed to parse document ${document.id}", e)
                        }
                    }
                    val sortedMoods = moodList.sortedByDescending { it.timestamp ?: 0L }
                    _uiState.update { it.copy(moods = sortedMoods) }
                }
            }
    }

    // The rest of the ViewModel remains the same as the last working version
    fun logOrUpdateMood(date: LocalDate, moodValue: Int) {
        val uid = authViewModel.currentUser?.uid ?: return
        val dateString = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        val newMood = MoodEntry(
            uid = uid,
            moodValue = moodValue,
            dateString = dateString,
            timestamp = System.currentTimeMillis()
        )

        _uiState.update { currentState ->
            val currentMoods = currentState.moods.toMutableList()
            val existingEntryIndex = currentMoods.indexOfFirst { it.dateString == dateString }

            if (existingEntryIndex != -1) {
                currentMoods[existingEntryIndex] = newMood
            } else {
                currentMoods.add(newMood)
            }
            val sortedMoods = currentMoods.sortedByDescending { it.timestamp ?: 0L }
            currentState.copy(moods = sortedMoods)
        }

        viewModelScope.launch {
            val documentId = "${uid}_${dateString}"
            db.collection("moods").document(documentId).set(newMood)
        }
    }
}

// Factory does not need to change
class StatisticsViewModelFactory(private val authViewModel: AuthViewModel) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatisticsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StatisticsViewModel(authViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

// UI State data class does not need to change
data class StatisticsState(
    val journalEntries: Int = 0,
    val mindfulnessSessions: Int = 0,
    val moods: List<MoodEntry> = emptyList()
)