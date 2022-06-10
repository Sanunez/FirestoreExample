package com.omegaonedevelopers.firestoretest

import android.app.Activity
import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.protobuf.Empty
import com.omegaonedevelopers.firestoretest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    var tag: String = "FSDB"
    var userView: View? = null
    var userIndex: Int? = null
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    var users : MutableList<Map<String,Any>> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        fetchUsers()
        setContentView(binding.root)
        binding.editUserBtn.setOnClickListener {
            (this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(binding.root.windowToken,0)
            if(userView == null){
                Toast.makeText(this@MainActivity, "No User Selected", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            update()
        }

        binding.deleteUserBtn.setOnClickListener {
            if(userView == null){
                Toast.makeText(this@MainActivity, "No User Selected", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            delete()
        }

        binding.addUserBtn.setOnClickListener {
            (this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(binding.root.windowToken,0)
            if( binding.name.text.isEmpty() || binding.age.text.isEmpty() || binding.email.text.isEmpty() || binding.gender.text.isEmpty()){
                Toast.makeText(MainActivity@this, "Fields not complete", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(userView != null){
                Toast.makeText(MainActivity@this, "Deselect User before adding", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            create()
        }

    }

    fun create(){
        var firstName = binding.name.text.toString()
        var age = binding.age.text.toString().toInt()
        var email = binding.email.text.toString()
        var gender = binding.gender.text.toString()
        var id = db.collection("Users").document().id
        db.collection("Users").document(id).set(
            hashMapOf(
            "firstName" to firstName,
            "lastName" to "",
            "age" to age,
            "email" to email,
            "gender" to gender,
            "userId" to id
            )
        ).addOnFailureListener { Toast.makeText(MainActivity@this, "User Failed to be added", Toast.LENGTH_SHORT).show() }
         .addOnSuccessListener {
            Toast.makeText(MainActivity@this, "User Successfully added", Toast.LENGTH_SHORT).show()
            fetchUsers()
        }
    }

    fun retrieve(){
        db!!.collection("Users").get().addOnCompleteListener { task ->
            if(!task.isSuccessful){
                Log.i(tag, "Failed to Fetch")
                return@addOnCompleteListener
            }
            for(document in task.result.documents){
                users!!.add(document.data!!)
            }
            updateUI()
        }
    }

    fun update(){
        var tempId = users.get(userIndex!!).get("userId").toString()
        var firstName = binding.name.text.toString()
        var age = binding.age.text.toString().toInt()
        var email = binding.email.text.toString()
        var gender = binding.gender.text.toString()
        db.collection("Users").document(tempId).update(
            mutableMapOf(
                "firstName" to firstName,
                "lastName" to "",
                "age" to age,
                "email" to email,
                "gender" to gender,
            ) as MutableMap<String, Any>
        ).addOnFailureListener { Toast.makeText(MainActivity@this, "User Failed to be added", Toast.LENGTH_SHORT).show() }
            .addOnSuccessListener {
                Toast.makeText(MainActivity@this, "User Successfully added", Toast.LENGTH_SHORT).show()
                fetchUsers()
            }
    }

    fun delete() {
        var userId = users.get(userIndex!!).get("userId").toString()
        db.collection("Users").document(userId).delete()
            .addOnFailureListener { Toast.makeText(MainActivity@this, "Unable to delete user", Toast.LENGTH_SHORT).show() }
            .addOnSuccessListener {
                Toast.makeText(MainActivity@this, "Successfully deleted user", Toast.LENGTH_SHORT).show()
                users.removeAt(userIndex!!)
                userIndex = null
                userView = null
                fetchUsers()
            }
    }

    fun clearUI(){
        users.clear()
        binding.userLayout.removeAllViews()
        binding.noUsersTxt.visibility = View.VISIBLE
        binding.name.text.clear()
        binding.age.text.clear()
        binding.email.text.clear()
        binding.gender.text.clear()
    }

    fun fetchUsers(){
        clearUI()
        retrieve()
    }

    fun updateUI(){
        if(users.size < 1){ return }
        binding.noUsersTxt.visibility = View.GONE
        users.forEachIndexed { index, user ->
            var userlayout = layoutInflater.inflate(R.layout.user_cell_layout, binding.userLayout, false)
            userlayout.findViewById<TextView>(R.id.name).text = "${user.get("firstName")} ${user.get("lastName")}"
            userlayout.findViewById<TextView>(R.id.age).text = "${user.get("age")}"
            userlayout.findViewById<TextView>(R.id.email).text = "${user.get("email")}"
            userlayout.findViewById<TextView>(R.id.gender).text = "${user.get("gender")}"
            userlayout.setOnClickListener {
                //If selecting same item
                if(it == userView){
                    //Clear details
                    binding.name.text.clear()
                    binding.age.text.clear()
                    binding.email.text.clear()
                    binding.gender.text.clear()
                    //Deselect Item
                    userView!!.setBackgroundColor(Color.WHITE)
                    userIndex = null
                    userView = null
                } else {
                    //Reset Previous selection
                    if (userView != null) {
                        userView!!.setBackgroundColor(Color.WHITE)
                    }
                    userIndex = index
                    userView = it
                    userView!!.setBackgroundColor(Color.LTGRAY)
                    //Update details
                    var tempUser = users.get(userIndex!!)
                    binding.name.setText(tempUser.get("firstName").toString())
                    binding.email.setText(tempUser.get("email").toString())
                    binding.age.setText(tempUser.get("age").toString())
                    binding.gender.setText(tempUser.get("gender").toString())
                }
            }
            binding.userLayout.addView(userlayout)
        }
    }
}
