package com.example.firebasenotes.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebasenotes.model.NotesState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class NotesViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth
    private val firestore = Firebase.firestore

    private val _notesData = MutableStateFlow<List<NotesState>>(emptyList())
    val notesData: StateFlow<List<NotesState>> = _notesData

    var state by mutableStateOf(NotesState())
        private set

    // CORRECCIÓN AQUÍ: Usamos mutableStateOf(0) en lugar de mutableIntStateOf
    // Esto arregla el error rojo y el de "Cannot infer type"
    var selectedColorIndex by mutableStateOf(0)

    var isLoading by mutableStateOf(false)
        private set

    var searchQuery by mutableStateOf("")
        private set

    // PALETA DE COLORES PASTEL
    val colorPalette = listOf(
        Color(0xFFFFFFFF), // Blanco
        Color(0xFFFDE68A), // Amarillo
        Color(0xFFBFDBFE), // Azul
        Color(0xFFBBF7D0), // Verde
        Color(0xFFFBCFE8), // Rosa
        Color(0xFFDDD6FE)  // Morado
    )

    fun onValue(value: String, text: String) {
        when (text) {
            "title" -> state = state.copy(title = value)
            "note" -> state = state.copy(note = value)
        }
    }

    fun onColorChange(index: Int) {
        selectedColorIndex = index
    }

    fun onSearchChange(query: String) {
        searchQuery = query
    }

    fun getFilteredNotes(): List<NotesState> {
        val query = searchQuery.lowercase()
        return _notesData.value.filter {
            it.title.lowercase().contains(query) || it.note.lowercase().contains(query)
        }
    }

    fun resetState() {
        state = state.copy(title = "", note = "")
        selectedColorIndex = 0
        isLoading = false
    }

    fun fetchNotes() {
        val email = auth.currentUser?.email
        firestore.collection("Notes")
            .whereEqualTo("emailUser", email.toString())
            .addSnapshotListener { querySnapshot, error ->
                if (error != null) return@addSnapshotListener
                val documents = mutableListOf<NotesState>()
                if (querySnapshot != null) {
                    for (document in querySnapshot) {
                        val myDocument = document.toObject(NotesState::class.java).copy(idDoc = document.id)
                        documents.add(myDocument)
                    }
                }
                _notesData.value = documents
            }
    }

    fun saveNewNote(title: String, note: String, onSuccess: () -> Unit) {
        val email = auth.currentUser?.email
        isLoading = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val newNote = hashMapOf(
                    "title" to title,
                    "note" to note,
                    "date" to formatDate(),
                    "emailUser" to email.toString(),
                    "colorIndex" to selectedColorIndex
                )
                firestore.collection("Notes").add(newNote).await()

                withContext(Dispatchers.Main) {
                    isLoading = false
                    onSuccess()
                }
            } catch (e: Exception) {
                Log.d("ERROR SAVE", "Error: ${e.localizedMessage}")
                withContext(Dispatchers.Main) { isLoading = false }
            }
        }
    }

    fun getNoteById(documentId: String) {
        isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val snapshot = firestore.collection("Notes").document(documentId).get().await()
                if (snapshot != null) {
                    val note = snapshot.toObject(NotesState::class.java)
                    withContext(Dispatchers.Main) {
                        state = state.copy(
                            title = note?.title ?: "",
                            note = note?.note ?: ""
                        )
                        selectedColorIndex = note?.colorIndex ?: 0
                    }
                }
            } catch (e: Exception) {
                Log.d("ERROR GET", "Error")
            } finally {
                withContext(Dispatchers.Main) { isLoading = false }
            }
        }
    }

    fun updateNote(idDoc: String, onSuccess: () -> Unit) {
        isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val editNote = hashMapOf(
                    "title" to state.title,
                    "note" to state.note,
                    "colorIndex" to selectedColorIndex
                )
                firestore.collection("Notes").document(idDoc).update(editNote as Map<String, Any>).await()

                withContext(Dispatchers.Main) {
                    isLoading = false
                    onSuccess()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { isLoading = false }
            }
        }
    }

    fun deleteNote(idDoc: String, onSuccess: () -> Unit) {
        isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                firestore.collection("Notes").document(idDoc).delete().await()
                withContext(Dispatchers.Main) {
                    isLoading = false
                    onSuccess()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { isLoading = false }
            }
        }
    }

    private fun formatDate(): String {
        val currentDate: Date = Calendar.getInstance().time
        val res = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return res.format(currentDate)
    }

    fun signOut() { auth.signOut() }

    fun getColor(index: Int): Color {
        return if (index in colorPalette.indices) colorPalette[index] else colorPalette[0]
    }
}