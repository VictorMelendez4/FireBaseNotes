package com.example.firebasenotes.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.tasks.await // <--- IMPORTANTE: ESTA LIBRERÍA ES LA MAGIA
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

    var isLoading by mutableStateOf(false)
        private set

    fun onValue(value: String, text: String) {
        when (text) {
            "title" -> state = state.copy(title = value)
            "note" -> state = state.copy(note = value)
        }
    }

    fun resetState() {
        state = state.copy(title = "", note = "")
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

    // --- GUARDAR CON AWAIT (FORMA ROBUSTA) ---
    fun saveNewNote(title: String, note: String, onSuccess: () -> Unit) {
        val email = auth.currentUser?.email
        isLoading = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val newNote = hashMapOf(
                    "title" to title,
                    "note" to note,
                    "date" to formatDate(),
                    "emailUser" to email.toString()
                )
                // .await() obliga a esperar aquí hasta que termine
                firestore.collection("Notes").add(newNote).await()

                // Si llegamos a esta línea, es que guardó con éxito
                withContext(Dispatchers.Main) {
                    isLoading = false
                    onSuccess()
                }
            } catch (e: Exception) {
                // Si algo falla, caemos aquí inmediatamente
                Log.d("ERROR SAVE", "Error: ${e.localizedMessage}")
                withContext(Dispatchers.Main) {
                    isLoading = false
                }
            }
        }
    }

    // --- OBTENER POR ID ---
    fun getNoteById(documentId: String) {
        isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // .await() espera la respuesta
                val snapshot = firestore.collection("Notes").document(documentId).get().await()
                if (snapshot != null) {
                    val note = snapshot.toObject(NotesState::class.java)
                    withContext(Dispatchers.Main) {
                        state = state.copy(
                            title = note?.title ?: "",
                            note = note?.note ?: ""
                        )
                    }
                }
            } catch (e: Exception) {
                Log.d("ERROR GET", "Error: ${e.localizedMessage}")
            } finally {
                // Pase lo que pase, apagamos la carga
                withContext(Dispatchers.Main) {
                    isLoading = false
                }
            }
        }
    }

    // --- ACTUALIZAR CON AWAIT ---
    fun updateNote(idDoc: String, onSuccess: () -> Unit) {
        isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val editNote = hashMapOf(
                    "title" to state.title,
                    "note" to state.note,
                )
                // .await() espera a que termine la actualización
                firestore.collection("Notes").document(idDoc).update(editNote as Map<String, Any>).await()

                // Éxito
                withContext(Dispatchers.Main) {
                    isLoading = false
                    onSuccess()
                }
            } catch (e: Exception) {
                // Error
                Log.d("ERROR EDIT", "Error: ${e.localizedMessage}")
                withContext(Dispatchers.Main) {
                    isLoading = false
                }
            }
        }
    }

    // --- ELIMINAR CON AWAIT ---
    fun deleteNote(idDoc: String, onSuccess: () -> Unit) {
        isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // .await() espera a que termine el borrado
                firestore.collection("Notes").document(idDoc).delete().await()

                // Éxito
                withContext(Dispatchers.Main) {
                    isLoading = false
                    onSuccess()
                }
            } catch (e: Exception) {
                // Error
                Log.d("ERROR DELETE", "Error: ${e.localizedMessage}")
                withContext(Dispatchers.Main) {
                    isLoading = false
                }
            }
        }
    }

    private fun formatDate(): String {
        val currentDate: Date = Calendar.getInstance().time
        val res = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return res.format(currentDate)
    }

    fun signOut() {
        auth.signOut()
    }
}