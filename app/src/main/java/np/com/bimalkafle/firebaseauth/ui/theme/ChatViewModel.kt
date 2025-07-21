// file: ui/theme/ChatViewModel.kt
package np.com.bimalkafle.firebaseauth.ui.theme

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.content
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import np.com.bimalkafle.firebaseauth.AuthViewModel

class ChatViewModel(private val authViewModel: AuthViewModel) : ViewModel() {

    val messageList = mutableStateListOf<MessageModel>()
    private val db = FirebaseFirestore.getInstance()
    private var messagesListener: ListenerRegistration? = null

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.0-flash-lite",
        apiKey = Constants.apiKey
    )

    fun startListening() {
        stopListening()
        val uid = authViewModel.currentUser?.uid
        if (uid == null) {
            Log.e("ChatViewModel", "startListening called but user is null. Aborting.")
            return
        }

        Log.d("ChatViewModel_Debug", "---- START LISTENING FOR UID: $uid ----")

        messagesListener = db.collection("users").document(uid).collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { chatSnapshot, error ->
                if (error != null) {
                    Log.e("ChatViewModel_Debug", "Chat listener failed.", error)
                    return@addSnapshotListener
                }
                if (chatSnapshot != null) {
                    Log.d("ChatViewModel_Debug", "Chat listener triggered. History size: ${chatSnapshot.size()}")
                    if (chatSnapshot.isEmpty) {
                        Log.d("ChatViewModel_Debug", "Chat history is EMPTY. Now fetching last mood...")
                        fetchLastMoodAndDisplayWelcomeMessage(uid)
                    } else {
                        Log.d("ChatViewModel_Debug", "Chat history FOUND. Displaying history.")
                        val messages = chatSnapshot.toObjects(MessageModel::class.java)
                        messageList.clear()
                        messageList.addAll(messages)
                    }
                } else {
                    Log.d("ChatViewModel_Debug", "Chat snapshot is NULL.")
                }
            }
    }

    private fun fetchLastMoodAndDisplayWelcomeMessage(uid: String) {
        db.collection("moods")
            .whereEqualTo("uid", uid)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { moodSnapshot ->
                if (moodSnapshot.isEmpty) {
                    Log.d("ChatViewModel_Debug", "Mood query successful, but NO mood entries found.")
                } else {
                    Log.d("ChatViewModel_Debug", "Mood query successful, found ${moodSnapshot.size()} mood entry.")
                }

                val lastMoodValue = moodSnapshot.documents.firstOrNull()?.getLong("moodValue")?.toInt()
                Log.d("ChatViewModel_Debug", "Extracted last mood value: $lastMoodValue")

                val welcomeText = when (lastMoodValue) {
                    1, 2 -> {
                        Log.d("ChatViewModel_Debug", "Chose SAD/ANXIOUS welcome message.")
                        "Halo, selamat datang kembali. Aku melihat catatan mood-mu kemarin. Apapun yang kamu rasakan hari ini, aku siap mendengarkan."
                    }
                    3 -> {
                        Log.d("ChatViewModel_Debug", "Chose NEUTRAL welcome message.")
                        "Halo, selamat datang kembali. Bagaimana kabarmu hari ini?"
                    }
                    4, 5 -> {
                        Log.d("ChatViewModel_Debug", "Chose HAPPY welcome message.")
                        "Halo! Senang melihatmu dalam mood yang baik lagi hari ini. Aku ikut senang! Ada cerita seru apa hari ini?"
                    }
                    else -> {
                        Log.d("ChatViewModel_Debug", "Chose DEFAULT welcome message.")
                        "Halo, aku CalmBot! 👋 Aku asisten pribadimu untuk membantu mengelola emosi dan kecemasan. Apa yang bisa aku bantu hari ini?"
                    }
                }
                val welcomeMessage = MessageModel(message = welcomeText, role = "model", timestamp = System.currentTimeMillis())

                Log.d("ChatViewModel_Debug", "Displaying welcome message: '$welcomeText'")
                messageList.clear()
                messageList.add(welcomeMessage)
            }
            .addOnFailureListener { e ->
                Log.e("ChatViewModel_Debug", "FAILED to fetch last mood.", e)
                val welcomeText = "Halo, aku CalmBot! 👋 Aku asisten pribadimu untuk membantu mengelola emosi dan kecemasan. Apa yang bisa aku bantu hari ini?"
                val welcomeMessage = MessageModel(message = welcomeText, role = "model", timestamp = System.currentTimeMillis())
                messageList.clear()
                messageList.add(welcomeMessage)
            }
    }

    fun stopListening() {
        messagesListener?.remove()
        Log.d("ChatViewModel_Debug", "---- STOPPED LISTENING ----")
    }

    override fun onCleared() {
        super.onCleared()
        stopListening()
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun sendMessage(question: String) {
        viewModelScope.launch {
            val uid = authViewModel.currentUser?.uid ?: return@launch

            // Hapus pesan selamat datang jika itu adalah satu-satunya pesan.
            if (messageList.size == 1 && messageList.first().role == "model") {
                messageList.clear()
            }

            try {
                val systemPrompt = """
                    Tujuanmu adalah menjadi CalmBot, seorang teman virtual yang sangat sabar, empatik, dan suportif. Anggap pengguna adalah teman dekatmu yang sedang curhat. Tugas utamamu adalah menjadi pendengar yang baik dan memberikan ruang aman bagi mereka untuk berekspresi.

                    ALUR PERCAKAPAN (SANGAT PENTING):
                    1.  Bedakan Niat Pengguna: Pertama, identifikasi niat pengguna. Apakah mereka sedang (A) mengungkapkan perasaan pribadi atau (B) bertanya informasi umum?
                        -   Jika (A) Mengungkapkan Perasaan (misal: "aku cemas"): Prioritaskan untuk mendengarkan. Berikan respons singkat yang memvalidasi perasaan mereka dan mengundang mereka untuk bercerita. Contoh: "Tentu saja, silakan bercerita. Aku di sini untuk mendengarkanmu." JANGAN langsung memberi saran.
                        -   Jika (B) Bertanya Informasi Umum (misal: "apa saja gejala kecemasan?"): Berikan jawaban informatif yang bersifat umum dan edukatif. Setelah itu, kamu boleh menawarkan fitur yang relevan, seperti 'bacaan singkat'. Jika pengguna tertarik untuk menggunakan fitur yang ditawarkan, langsung bimbing pengguna untuk beralih ke halaman homepage untuk fitur pernapasan, jurnal, bacaan, video, dan musik, serta halaman statistik untuk mood tracking atau pelacakan mood emosi.

                    2.  Gali Lebih Dalam (Hanya untuk alur A): Setelah pengguna bercerita tentang perasaannya, ajukan pertanyaan pendalaman yang singkat. Contoh: "Terima kasih sudah berbagi. Apa yang membuatmu merasa seperti itu?"
                    3.  Berikan Saran yang Relevan: Setelah memahami masalahnya (di alur A) atau setelah memberikan informasi (di alur B), tawarkan SATU saran fitur yang paling relevan sebagai sebuah pilihan.
                    
                    ATURAN FORMAT (INI SANGAT PENTING DAN WAJIB DIIKUTI):
                    -   JAWABANMU HARUS BERUPA TEKS BIASA (PLAIN TEXT).
                    -   JANGAN PERNAH, DALAM KONDISI APAPUN, MENGGUNAKAN FORMAT MARKDOWN. Ini termasuk:
                        -   Tanda bintang (*) untuk menebalkan teks atau untuk list.
                        -   Tanda bintang double (**) untuk menebalkan teks atau untuk list.
                        -   Tanda strip (-) untuk list.
                        -   Tanda pagar (#) untuk heading.
                        -   Garis bawah (_) untuk memiringkan teks.
                        -   Em Dash (—) untuk apapun.
                    -   Gunakan hanya tanda baca standar seperti titik dan koma.

                    ATURAN UTAMA:
                    1.  Bahasa dan Nada: Gunakan Bahasa Indonesia yang hangat dan personal. Jaga jawaban tetap singkat. jika pengguna bertanya dalam bahasa selain bahasa indonesia, beri tahu kalau kamu hanya bisa berbahasa indonesia.
                    2.  Identitas: Kamu adalah CalmBot, seorang teman pendengar. Jangan pernah menyebut dirimu AI.
                    3.  Fokus Topik: Tetap pada topik kesehatan mental. Tolak topik lain dengan sopan.
                    4.  Format Jawaban: Gunakan paragraf singkat yang alami. JANGAN PERNAH pakai format markdown (list, bold, em dash (—), tanda bintang (*), hanya gunakan simbol biasa seperti koma atau titik atau kutipan.
                    5.  Batasan Profesional: Kamu BUKAN terapis. Perbedaan utamanya adalah:
                        -   BOLEH: Memberikan informasi dan edukasi umum tentang kesehatan mental (misal: menjelaskan gejala umum kecemasan, apa itu mindfulness).
                        -   TIDAK BOLEH: Memberikan diagnosis kepada pengguna ("Sepertinya kamu mengalami...") atau menyarankan pengobatan spesifik. Selalu gunakan frasa seperti "Gejala umum dari kecemasan biasanya...", bukan "Gejala yang kamu alami adalah...".
                        -   Jika ada tanda krisis (menyakiti diri, bunuh diri), segera sarankan untuk menghubungi profesional dengan kalimat peduli: "Ini terdengar sangat serius dan aku khawatir denganmu. Keselamatanmu adalah yang terpenting. Tolong segera hubungi layanan darurat di nomor 119 atau seorang profesional kesehatan mental."
                        
                    
                """.trimIndent()

                val primingHistory = mutableListOf<Content>(
                    content("user") { text(systemPrompt) },
                    content("model") { text("Baik, saya mengerti.") }
                )
                val chatHistory = messageList.map { content(it.role) { text(it.message) } }
                primingHistory.addAll(chatHistory)

                val chat = generativeModel.startChat(history = primingHistory)

                val userMsg = MessageModel(message = question, role = "user", timestamp = System.currentTimeMillis())
                messageList.add(userMsg)
                saveMessage(uid, userMsg)

                val typingMsg = MessageModel("Typing...", "model", System.currentTimeMillis())
                messageList.add(typingMsg)

                val response = chat.sendMessage(question)

                messageList.remove(typingMsg)

                val botMsg = MessageModel(message = response.text.toString(), role = "model", timestamp = System.currentTimeMillis())
                messageList.add(botMsg)
                saveMessage(uid, botMsg)
            } catch (e: Exception) {
                messageList.removeLast()
                val errorMsg = MessageModel(message = "Error: ${e.message}", role = "model", timestamp = System.currentTimeMillis())
                messageList.add(errorMsg)
                saveMessage(uid, errorMsg)
            }
        }
    }

    private fun saveMessage(uid: String, message: MessageModel) {
        db.collection("users").document(uid).collection("messages").add(message)
    }
}

// --- Factory untuk ChatViewModel ---
class ChatViewModelFactory(private val authViewModel: AuthViewModel) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(authViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}