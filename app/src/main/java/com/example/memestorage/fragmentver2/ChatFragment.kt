package com.example.memestorage.fragmentver2

import com.example.memestorage.adapterver2.FriendshipMessageAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.memestorage.databinding.FragmentChatBinding
import com.example.memestorage.test.viewmodel.UserFriendshipMessageViewModel

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private lateinit var ufmViewModel: UserFriendshipMessageViewModel
    private var friendshipId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        friendshipId = arguments?.getString("friendship_id")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        initViewModel()
        loadMessages()
        return binding.root
    }

    private fun initViewModel() {
        ufmViewModel = ViewModelProvider.AndroidViewModelFactory
            .getInstance(requireActivity().application)
            .create(UserFriendshipMessageViewModel::class.java)
    }

    private fun loadMessages() {
        friendshipId?.let { id ->
            ufmViewModel.getUfmByChatId(id) { task ->
                if (task.isSuccessful) {
                    val list = task.result!!.map { it.toObject(com.example.memestorage.test.model.UserFriendshipMessageModel::class.java) }
                    binding.rvChat.layoutManager = LinearLayoutManager(requireContext())
                    binding.rvChat.adapter = FriendshipMessageAdapter(list)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(friendshipId: String) = ChatFragment().apply {
            arguments = Bundle().apply {
                putString("friendship_id", friendshipId)
            }
        }
    }
}
