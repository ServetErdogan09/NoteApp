package com.example.noteapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.noteapp.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.checkerframework.common.returnsreceiver.qual.This

class RegisterActivity : AppCompatActivity() {
    private lateinit var registerBinding: ActivityRegisterBinding
    private  lateinit var auth : FirebaseAuth
    private var userEmail : String?= null
    private var userPassword : String? =null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        registerBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(registerBinding.root)

        auth = Firebase.auth
         val currentUer = auth.currentUser

        if (currentUer != null) {
            // başka activitye git
            val intent = Intent(this,FeedActivity::class.java)
            startActivity(intent)
            finish()
        }


    }

     fun signInClick(view : View){

         userEmail  = registerBinding.emailEditText.text.toString()
         userPassword = registerBinding.editTextTextPassword.text.toString()

         if (!userEmail.isNullOrEmpty() &&  !userPassword.isNullOrEmpty()){

             auth.signInWithEmailAndPassword(userEmail!!,userPassword!!).addOnSuccessListener {  // kullanıcı varsa

                 val intent = Intent(this,FeedActivity::class.java)
                 startActivity(intent)
                 finish()

             }.addOnFailureListener {  // kullanıcı yoksa
                 Toast.makeText(this,"Wrong email or password",Toast.LENGTH_LONG).show()
             }

         }else{
             Toast.makeText(this,"Enter email and password",Toast.LENGTH_LONG).show()

         }



    }


     fun signUpClick(view: View){

         userEmail  = registerBinding.emailEditText.text.toString()
         userPassword = registerBinding.editTextTextPassword.text.toString()

         if (!userEmail.isNullOrEmpty() && !userPassword.isNullOrEmpty() ) {

             auth.createUserWithEmailAndPassword(userEmail!!,userPassword!!).addOnSuccessListener {  // kullanıcı başarılı bir şekilde oluştu mu

                 Toast.makeText(this,"Authentication created",Toast.LENGTH_LONG).show()
                 val intent = Intent(this,FeedActivity::class.java)
                 startActivity(intent)
                 finish()

             }.addOnFailureListener {  // ullanıcı başarılı bir şekilde oluşmadıysa
            Toast.makeText(this,"Authentication failed.",Toast.LENGTH_LONG).show()
             }

         }else{
             Toast.makeText(this,"Password and Email",Toast.LENGTH_LONG).show()
         }




    }

}