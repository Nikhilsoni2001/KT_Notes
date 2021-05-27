package com.nikhil.kt_notes.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.common.api.ApiException
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
import kotlinx.coroutines.withContext

class UpdateFragment : Fragment() {

    private lateinit var btnUpdateNote: MaterialButton
    private lateinit var elNoteTitle: TextInputLayout
    private lateinit var etNoteTitle: TextInputEditText
    private lateinit var elNoteContent: TextInputLayout
    private lateinit var etNoteContent: TextInputEditText
    private lateinit var viewModel: NotesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_update, container, false)
        viewModel = (activity as NotesActivity).viewModel

        val args: UpdateFragmentArgs by navArgs()
        initialization(view)

        val noteRef = args.notes
        val docId = noteRef.document_id
        val noteId = noteRef.note_id
        val noteTitle = noteRef.note_title
        val noteDesc = noteRef.note_description
        val fav = noteRef.note_favourite

        etNoteTitle.setText(noteTitle)
        etNoteContent.setText(noteDesc)

        btnUpdateNote.setOnClickListener {
            if (etNoteTitle.text?.trim().toString().isNotEmpty()) {
                if (etNoteContent.text?.trim().toString().isNotEmpty()) {
                    val map = mutableMapOf<String, Any>()
                    map["note_title"] = etNoteTitle.text?.trim().toString()
                    map["note_description"] = etNoteContent.text?.trim().toString()

                    val note = Note(
                        noteId,
                        docId,
                        etNoteTitle.text.toString(),
                        etNoteContent.text.toString(),
                        note_favourite = fav,
                        note_sync = false
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            viewModel.updateData(note, map)
                            withContext(Dispatchers.Main) {
                                findNavController().navigate(R.id.action_updateFragment_to_notesFragment)
                            }
                        } catch (e: ApiException) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                    }
                } else {
                    elNoteContent.error = "Please enter a valid Description!"
                }
            } else {
                elNoteTitle.error = "Please enter a valid Title"
            }
        }
        return view
    }

    private fun initialization(view: View) {
        btnUpdateNote = view.findViewById(R.id.btnUpdateNote)
        etNoteTitle = view.findViewById(R.id.etNoteTitle)
        elNoteTitle = view.findViewById(R.id.elNoteTitle)
        elNoteContent = view.findViewById(R.id.elNoteContent)
        etNoteContent = view.findViewById(R.id.etNoteContent)
    }
}