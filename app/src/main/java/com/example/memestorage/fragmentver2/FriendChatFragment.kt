package com.example.memestorage.fragmentver2


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.memestorage.adapterver2.FriendshipAdapter3
import com.example.memestorage.databinding.FragmentFriendChatBinding
import com.example.memestorage.test.viewmodel.UserFriendshipViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.memestorage.test.model.UserFriendshipModel

class FriendChatFragment : Fragment() {

    private var _binding: FragmentFriendChatBinding? = null
    private val binding get() = _binding!!

    private lateinit var ufViewModel: UserFriendshipViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendChatBinding.inflate(inflater, container, false)
        initViewModel()
        loadFriendships()
        return binding.root
    }

    private fun initViewModel() {
        ufViewModel = ViewModelProvider.AndroidViewModelFactory
            .getInstance(requireActivity().application)
            .create(UserFriendshipViewModel::class.java)
    }

    private fun loadFriendships() {
        ufViewModel.getUfFirebase { task ->
            if (task.isSuccessful) {
                val list = mutableListOf<UserFriendshipModel>()
                for (doc in task.result!!) {
                    val item = doc.toObject(UserFriendshipModel::class.java)
                    list.add(item)
                }
                binding.rvFriendList.layoutManager = LinearLayoutManager(requireContext())
                binding.rvFriendList.adapter = FriendshipAdapter3(list) { friendship ->
                    openChatFragment(friendship.ufId)
                }
            }
        }
    }
    private fun openChatFragment(friendshipId: String) {
        val chatFragment = ChatFragment.newInstance(friendshipId)
        parentFragmentManager.beginTransaction()
            .replace(id, chatFragment) // hoặc R.id.fragment_container nếu xài container cha
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
