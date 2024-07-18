package com.example.noteapp

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.noteapp.databinding.ActivityAddBinding
import com.example.noteapp.databinding.ActivityFeedBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreKtxRegistrar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.lang.reflect.Field

class AddActivity : AppCompatActivity() {
    private lateinit var addBinding: ActivityAddBinding
    private lateinit var categorySpinner : Spinner
    private lateinit var db : FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        addBinding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(addBinding.root)

        // categorySpinner'i addBinding üzerinden başlat
        categorySpinner = addBinding.categorySpinner

        // Kategorileri spinner'a yükle
        val categories = listOf("Work", "Personal", "Study", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter

        db = Firebase.firestore

    }

    fun saveNote(view : View){
    // firebase store'ye kodalrı burada kaydedeceğiz

        var title = addBinding.titleEditText.text.toString()
        var content = addBinding.contentEditText.text.toString()
        var category = categorySpinner.selectedItem.toString()

        if (title.isEmpty() && content.isEmpty()){
            Toast.makeText(this,"Title and Content cannot be empty",Toast.LENGTH_LONG).show()
            return
        }

        // şuan giriş yapmış kullanıcı

        val curentUser =FirebaseAuth.getInstance().currentUser

        if (curentUser != null){

            var note = hashMapOf(
                "title" to title,
                "content" to content,
                "category" to category,
                "timestamp" to System.currentTimeMillis() // sunucu tarafından oluşturululan bir zaman damgansı ekler
            )

            // firestore ekleme
            db.collection("users").document(curentUser.uid).collection("notes")
                .add(note)
                .addOnSuccessListener {
                    Toast.makeText(this, "Not başarıyla eklendi", Toast.LENGTH_SHORT).show()
                    finish()
                }.addOnFailureListener {

                    Toast.makeText(this,"Not eklerken hata oluştu ",Toast.LENGTH_LONG).show()
                }


        }




    }
}