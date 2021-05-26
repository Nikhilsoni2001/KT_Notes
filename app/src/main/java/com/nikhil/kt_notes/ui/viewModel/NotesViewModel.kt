package com.nikhil.kt_notes.ui.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nikhil.kt_notes.db.Note
import com.nikhil.kt_notes.repositories.NotesRepository
import kotlinx.coroutines.launch

class NotesViewModel(private val context: Context, private val repository: NotesRepository) :
    ViewModel() {
    fun upsert(note: Note) = viewModelScope.launch { repository.upsert(note) }
    fun delete(note: Note) = viewModelScope.launch { repository.delete(note) }
    fun getAllNotes() = repository.getAllNotes()
    fun deleteAllNotes() = repository.deleteAllNotes()
}