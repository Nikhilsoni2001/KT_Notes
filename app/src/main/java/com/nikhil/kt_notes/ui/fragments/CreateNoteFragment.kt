package com.nikhil.kt_notes.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.nikhil.kt_notes.R
import com.nikhil.kt_notes.db.Note
import com.nikhil.kt_notes.ui.activities.NotesActivity
import com.nikhil.kt_notes.ui.viewModel.NotesViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateNoteFragment : Fragment() {

    private lateinit var btnCreateNote: MaterialButton
    private lateinit var elNoteTitle: TextInputLayout
    private lateinit var etNoteTitle: TextInputEditText
    private lateinit var elNoteContent: TextInputLayout
    private lateinit var etNoteContent: TextInputEditText
    private lateinit var viewModel: NotesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_note, container, false)

        viewModel = (activity as NotesActivity).viewModel

        initialization(view)

        btnCreateNote.setOnClickListener {
            val noteTitle = etNoteTitle.text.toString()
            val id: String? = ""
            val noteContent = etNoteContent.text.toString()

            if (noteTitle.trim().isNotEmpty()) {
                if (noteContent.trim().isNotEmpty()) {

                    CoroutineScope(Dispatchers.IO).launch {
                        val note = Note(0, "", noteTitle, noteContent, false, false)
                        viewModel.upsert(note)
                    }

                    findNavController().navigate(R.id.action_createNoteFragment_to_notesFragment)
                } else {
                    elNoteContent.error = "Please enter some content."
                }
            } else {
                elNoteTitle.error = "Please enter a Title."
            }
        }
        return view
    }

    private fun initialization(view: View) {
        btnCreateNote = view.findViewById(R.id.btnCreateNote)
        elNoteTitle = view.findViewById(R.id.elNoteTitle)
        etNoteTitle = view.findViewById(R.id.etNoteTitle)
        elNoteContent = view.findViewById(R.id.elNoteContent)
        etNoteContent = view.findViewById(R.id.etNoteContent)
    }
}