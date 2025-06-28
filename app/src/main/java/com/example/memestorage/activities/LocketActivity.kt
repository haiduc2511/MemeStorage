package com.example.memestorage.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.memestorage.databinding.ActivityLocketBinding

class LocketActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLocketBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGoToTest.setOnClickListener {
            startActivity(Intent(this, TestActivity::class.java))
        }
    }
}