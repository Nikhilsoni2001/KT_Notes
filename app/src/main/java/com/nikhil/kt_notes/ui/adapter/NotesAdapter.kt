package com.nikhil.kt_notes.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nikhil.kt_notes.R
import com.nikhil.kt_notes.db.Note
import com.nikhil.kt_notes.ui.viewModel.NotesViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class NotesAdapter(private val viewModel: NotesViewModel) :
    RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {
    class NotesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNoteTitle: TextView = view.findViewById(R.id.tvNoteTitle)
        val tvNoteDescription: TextView = view.findViewById(R.id.tvNoteDescription)
        val cbFavourite: CheckBox = view.findViewById(R.id.cbFavourite)
        val tvSync: TextView = view.findViewById(R.id.tvSync)
    }

    private val differCallback = object : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.note_id == newItem.note_id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.notes_single_card, parent, false)
        return NotesViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val note = differ.currentList[position]
        holder.itemView.apply {
            holder.tvNoteTitle.text = note.note_title
            holder.tvNoteDescription.text = note.note_description
            holder.cbFavourite.isChecked = note.note_favourite

            holder.cbFavourite.apply {
                setOnClickListener {
                    note.note_favourite = !note.note_favourite
                    isChecked = note.note_favourite

                    val updatedNote = Note(
                        note.note_id,
                        note.document_id,
                        note.note_title,
                        note.note_description,
                        note.note_favourite,
                        note.note_sync
                    )
                    val map = mutableMapOf<String, Any>()
                    map["note_favourite"] = note.note_favourite
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            viewModel.updateData(updatedNote, map)
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }

            if (note.note_sync) holder.tvSync.text = "Sync"
            else holder.tvSync.text = "Not Sync"

            setOnClickListener {
                onItemClickListener?.let { it(note) }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((Note) -> Unit)? = null
    fun setOnItemClickListener(listener: (Note) -> Unit) {
        listener.also { onItemClickListener = it }
    }
}