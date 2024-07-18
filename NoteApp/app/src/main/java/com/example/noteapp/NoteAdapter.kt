package com.example.noteapp

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.noteapp.databinding.RecyclerRowNoteBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class NoteAdapter(private val noteList: ArrayList<Note>) : RecyclerView.Adapter<NoteAdapter.NoteHolder>() {

    class NoteHolder(val binding: RecyclerRowNoteBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
        val binding = RecyclerRowNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteHolder(binding)
    }

    override fun getItemCount(): Int {
        return noteList.size
    }

    override fun onBindViewHolder(holder: NoteHolder, position: Int) {
        val note = noteList[position]

        holder.binding.titleTextView.text = note.title
        holder.binding.contentTextView.text = note.content
        holder.binding.categoryTextView.text = note.category

        holder.binding.deleteImageView.setOnClickListener {
            val adapterPosition = holder.adapterPosition
            if (adapterPosition != RecyclerView.NO_POSITION) {
                val deletedNote = noteList[adapterPosition]

                deleteNoteFromFirestore(deletedNote, holder)

                // Firestore'dan notu sildikten sonra listeyi güncelle
                noteList.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
            }
        }
    }

    private fun deleteNoteFromFirestore(note: Note, holder: NoteHolder) {
        val currentUserId = FirebaseAuth.getInstance().currentUser
        if (currentUserId != null) {
            val db = Firebase.firestore
            db.collection("users").document(currentUserId.uid).collection("notes").document(note.noteId)
                .delete()
                .addOnSuccessListener {
                   // Toast.makeText(holder.itemView.context, "Not silindi", Toast.LENGTH_SHORT).show()

                    // Silme başarılı olduğunda listeyi güncelle
                    val position = noteList.indexOf(note)
                    if (position != -1) {
                        noteList.removeAt(position)
                        notifyItemRemoved(position)
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(holder.itemView.context, "Error while deleting note ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
