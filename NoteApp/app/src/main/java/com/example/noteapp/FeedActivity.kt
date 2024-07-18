package com.example.noteapp

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.noteapp.databinding.ActivityFeedBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FeedActivity : AppCompatActivity() {
    private lateinit var feedBinding: ActivityFeedBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var categorySpinner: Spinner
    private lateinit var db : FirebaseFirestore
    private val categories = listOf("All", "Work", "Personal", "Study", "Other")
    private lateinit var noteArrayList: ArrayList<Note>

    private lateinit var noteAdapter : NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        feedBinding = ActivityFeedBinding.inflate(layoutInflater)
        setContentView(feedBinding.root)


        categorySpinner = feedBinding.spinnerCategories
        auth =Firebase.auth
        db = Firebase.firestore

        noteArrayList = ArrayList<Note>()
        setupRecyclerView()
        setupSpinner()

        fetchNotes("All") // Başlangıçta tüm notları gösterir
    }
    // snipere kuracağız

    private fun setupSpinner(){

        val adapter = ArrayAdapter(this,android.R.layout.simple_spinner_item , categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter=adapter

        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedCategory = categories[position]
                fetchNotes(selectedCategory)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Boş bırakabilirsiniz ya da bir işlem ekleyebilirsiniz
            }
        }

    }

    private fun setupRecyclerView() {
              noteAdapter = NoteAdapter(noteArrayList)
              feedBinding.recyclerViewNotes.adapter = noteAdapter
              feedBinding.recyclerViewNotes.layoutManager = LinearLayoutManager(this)
    }

    private fun fetchNotes(selectedCategory: String) {
        val current = FirebaseAuth.getInstance().currentUser

        if (current != null) {
            var query: Query = db.collection("users").document(current.uid).collection("notes")

            if (selectedCategory != "All") {
                query = query.whereEqualTo("category", selectedCategory)
            }

            query.get()
                .addOnSuccessListener { documents ->
                    noteArrayList.clear()
                    if (documents.isEmpty) {
                        Toast.makeText(this, "No notes found", Toast.LENGTH_SHORT).show()
                    } else {
                        for (document in documents) {
                            val title = document.get("title") as? String ?: "Başlık yok"
                            val content = document.get("content") as? String ?: "İçerik yok"
                            val category = document.get("category") as? String ?: "Kategori yok"
                            val timestamp = document.get("timestamp") as? Long ?: 0L
                            val noteId = document.id
                            val note = Note(title, content, category, noteId)
                            noteArrayList.add(note)
                        }
                    }
                    noteAdapter.notifyDataSetChanged() // RecyclerView'in güncellenmesi
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error fetching notes: ${e.message}", e)
                    Toast.makeText(this, "Error while taking notes", Toast.LENGTH_SHORT).show()

                }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.not_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (R.id.sign_out == item.itemId ){
            auth.signOut()
            val intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun addNote(view: View){
        val intent = Intent(this,AddActivity::class.java)
        startActivity(intent)
    }
}