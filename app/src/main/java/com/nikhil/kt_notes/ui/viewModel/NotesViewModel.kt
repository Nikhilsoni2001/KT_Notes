package com.nikhil.kt_notes.ui.viewModel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.nikhil.kt_notes.db.Note
import com.nikhil.kt_notes.repositories.NotesRepository
import com.nikhil.kt_notes.ui.activities.NotesActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception

class NotesViewModel(private val context: Context, private val repository: NotesRepository) :
    ViewModel() {
    fun upsert(note: Note) = viewModelScope.launch { repository.upsert(note) }
    fun delete(note: Note) = viewModelScope.launch { repository.delete(note) }
    fun getAllNotes() = repository.getAllNotes()
    fun deleteAllNotes() = repository.deleteAllNotes()

    private var noteCollectionRef =
        FirebaseFirestore.getInstance().collection(NotesActivity.collection_name!!)

    suspend fun saveNote(note: Note): String {
        var id: String? = null
        val job = viewModelScope.launch {
            try {
                id = noteCollectionRef.add(note).await().id
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Note saved successfully!", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        job.join()

        return id.toString()
    }

    suspend fun retrieveNotes(): List<Note> {
        val noteList = mutableListOf<Note>()
        val job = viewModelScope.launch {
            try {
                val querySnapshot = noteCollectionRef.get().await()

                for (document in querySnapshot.documents) {
                    val notes = document.toObject(Note::class.java)
                    if (notes != null) {
                        notes.document_id = document.id
                        noteList.add(notes)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        job.join()
        return noteList
    }

    suspend fun updateData(note: Note, newNote: Map<String, Any>) = viewModelScope.launch {
        val noteQuery =
            noteCollectionRef.whereEqualTo("document_id", note.document_id.toString()).get().await()
        if (noteQuery.documents.isNotEmpty()) {
            for (document in noteQuery) {
                noteCollectionRef.document(document.id).set(newNote, SetOptions.merge()).await()
            }
        }
    }

    suspend fun deleteData(note: Note) {
        val job = viewModelScope.launch {
            try {
                noteCollectionRef.document(note.document_id.toString()).delete().await()
            } catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }
        job.join()
    }
}