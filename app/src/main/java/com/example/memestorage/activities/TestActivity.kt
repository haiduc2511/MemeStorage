package com.example.memestorage.activities

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.memestorage.R
import com.example.memestorage.adapters.ButtonAdapter
import com.example.memestorage.databinding.ActivityTestBinding
import com.example.memestorage.fragments.ImageFragment
import com.example.memestorage.test.fragment.MediaLikeFragment
import com.example.memestorage.test.fragment.UserFriendCompatibilityFragment

class TestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTestBinding

    private val buttons = listOf(
        "go_to_user_friend_diary_media_fragment",
        "go_to_users_fragment",
        "go_to_categories_fragment",
        "go_to_media_likes_fragment",
        "go_to_media_saves_fragment"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = ButtonAdapter(buttons) { buttonText ->
            when (buttonText) {
                "go_to_categories_fragment" -> {
                    showImageCategoryFragment()
                }
                else -> {
                    Toast.makeText(this, "Bạn bấm: $buttonText", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun showImageCategoryFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, UserFriendCompatibilityFragment())
            .addToBackStack(null)
            .commit()
    }
}
