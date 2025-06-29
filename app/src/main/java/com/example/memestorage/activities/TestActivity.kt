package com.example.memestorage.activities

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.memestorage.R
import com.example.memestorage.adapters.ButtonAdapter
import com.example.memestorage.databinding.ActivityTestBinding
import com.example.memestorage.fragments.ImageFragment
import com.example.memestorage.test.fragment.MediaCommentFragment
import com.example.memestorage.test.fragment.MediaLikeFragment
import com.example.memestorage.test.fragment.MediaSaveFragment
import com.example.memestorage.test.fragment.MyLikedMediaFragment
import com.example.memestorage.test.fragment.MySavedMediaFragment
import com.example.memestorage.test.fragment.UserDiaryMediaAccessFragment
import com.example.memestorage.test.fragment.UserDiaryMediaFragment
import com.example.memestorage.test.fragment.UserFriendCompatibilityActionFragment
import com.example.memestorage.test.fragment.UserFriendCompatibilityFragment
import com.example.memestorage.test.fragment.UserFriendRequestFragment
import com.example.memestorage.test.fragment.UserFriendshipChatFragment
import com.example.memestorage.test.fragment.UserFriendshipFragment
import com.example.memestorage.test.fragment.UserFriendshipMessageFragment

class TestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTestBinding

    private val buttons = listOf(
        "go_to_MediaCommentFragment",
        "go_to_MediaLikeFragment",
        "go_to_MediaSaveFragment",
        "go_to_MyLikedMediaFragment",
        "go_to_MySavedMediaFragment",
        "go_to_UserDiaryMediaAccessFragment",
        "go_to_UserDiaryMediaFragment",
        "go_to_UserFriendCompatibilityActionFragment",
        "go_to_UserFriendCompatibilityFragment",
        "go_to_UserFriendRequestFragment",
        "go_to_UserFriendshipChatFragment",
        "go_to_UserFriendshipFragment",
        "go_to_UserFriendshipMessageFragment"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = ButtonAdapter(buttons) { buttonText ->
            when (buttonText) {
                "go_to_MediaCommentFragment" -> showFragment(MediaCommentFragment())
                "go_to_MediaLikeFragment" -> showFragment(MediaLikeFragment())
                "go_to_MediaSaveFragment" -> showFragment(MediaSaveFragment())
                "go_to_MyLikedMediaFragment" -> showFragment(MyLikedMediaFragment())
                "go_to_MySavedMediaFragment" -> showFragment(MySavedMediaFragment())
                "go_to_UserDiaryMediaAccessFragment" -> showFragment(UserDiaryMediaAccessFragment())
                "go_to_UserDiaryMediaFragment" -> showFragment(UserDiaryMediaFragment())
                "go_to_UserFriendCompatibilityActionFragment" -> showFragment(
                    UserFriendCompatibilityActionFragment()
                )
                "go_to_UserFriendCompatibilityFragment" -> showFragment(UserFriendCompatibilityFragment())
                "go_to_UserFriendRequestFragment" -> showFragment(UserFriendRequestFragment())
                "go_to_UserFriendshipChatFragment" -> showFragment(UserFriendshipChatFragment())
                "go_to_UserFriendshipFragment" -> showFragment(UserFriendshipFragment())
                "go_to_UserFriendshipMessageFragment" -> showFragment(UserFriendshipMessageFragment())
                else -> Toast.makeText(this, "Bạn bấm: $buttonText", Toast.LENGTH_SHORT).show()
            }
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }
}
