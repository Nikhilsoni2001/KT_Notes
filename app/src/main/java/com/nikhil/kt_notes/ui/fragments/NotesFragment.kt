package com.nikhil.kt_notes.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.nikhil.kt_notes.R
import com.nikhil.kt_notes.ui.activities.NotesActivity
import com.nikhil.kt_notes.ui.adapter.NotesAdapter
import com.nikhil.kt_notes.ui.viewModel.NotesViewModel

class NotesFragment : Fragment() {

    private lateinit var createNotesFab: FloatingActionButton
    private lateinit var rvNotes: RecyclerView
    lateinit var viewModel: NotesViewModel
    lateinit var notesAdapter: NotesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_notes, container, false)
        initialization(view)


        notesAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("notes", it)
            }
            findNavController().navigate(R.id.action_notesFragment_to_updateFragment, bundle)
        }

        rvNotes.apply {
            adapter = notesAdapter
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }

        viewModel.getAllNotes().observe(viewLifecycleOwner, Observer { notes ->
            Log.d("NOTES", "onCreateView: " + notes)
            notesAdapter.differ.submitList(notes)
        })

//        Delete on swipe
        val itemTouchHelper = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val note = notesAdapter.differ.currentList[position]
                viewModel.delete(note)
                Snackbar.make(
                    rvNotes,
                    "${note.note_title} Deleted Successfully!!",
                    Snackbar.LENGTH_LONG
                ).setAction("Undo", View.OnClickListener { viewModel.upsert(note) }).show()
            }
        }

        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(rvNotes)

        createNotesFab.setOnClickListener { findNavController().navigate(R.id.action_notesFragment_to_createNoteFragment) }
        return view
    }

    private fun initialization(view: View) {
        viewModel = (activity as NotesActivity).viewModel
        notesAdapter = NotesAdapter()
        createNotesFab = view.findViewById(R.id.createNotesFabButton)
        rvNotes = view.findViewById(R.id.rvNotes)
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar!!.hide()
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar!!.show()
    }
}