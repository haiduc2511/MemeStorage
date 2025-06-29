package com.example.memestorage.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.memestorage.R
import com.example.memestorage.databinding.ActivityLocketBinding
import com.example.memestorage.fragmentver2.FriendChatFragment
import com.example.memestorage.test.fragment.UserFriendCompatibilityFragment

class LocketActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLocketBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGoToTest.setOnClickListener {
            startActivity(Intent(this, TestActivity::class.java))
        }
        binding.btnOpenChat.setOnClickListener {
            showImageCategoryFragment()
        }
        binding.btnSeeDiary.setOnClickListener {

        }
    }


    private fun showImageCategoryFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer2, FriendChatFragment())
            .addToBackStack(null)
            .commit()
    }
}