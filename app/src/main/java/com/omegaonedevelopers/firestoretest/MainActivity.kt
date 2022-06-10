package com.omegaonedevelopers.firestoretest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.omegaonedevelopers.firestoretest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    var userView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.editUserBtn.setOnClickListener {
            if(userView == null){
                Toast.makeText(this@MainActivity, "No User Selected", Toast.LENGTH_SHORT).show()
            }
        }

    }
}