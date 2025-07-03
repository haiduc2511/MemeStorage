package com.example.memestorage.test.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.memestorage.adapterver2.UserFriendDiaryMediaAdapter
import com.example.memestorage.databinding.FragmentUserFriendDiaryMediaBinding
import com.example.memestorage.test.model.UserFriendDiaryMediaModel
import com.example.memestorage.test.viewmodel.UserFriendDiaryMediaViewModel

class UserFriendDiaryMediaFragment : Fragment() {

    private var _binding: FragmentUserFriendDiaryMediaBinding? = null
    private val binding get() = _binding!!

    private lateinit var ufdmViewModel: UserFriendDiaryMediaViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserFriendDiaryMediaBinding.inflate(inflater, container, false)
        initViewModel()
        loadUserFriendDiaryMedia()
        return binding.root
    }

    private fun initViewModel() {
        ufdmViewModel = ViewModelProvider.AndroidViewModelFactory
            .getInstance(requireActivity().application)
            .create(UserFriendDiaryMediaViewModel::class.java)
    }

    private fun loadUserFriendDiaryMedia() {
        ufdmViewModel.getUfdmFirebase { task ->
            if (task.isSuccessful) {
                val list = mutableListOf<UserFriendDiaryMediaModel>()
                for (doc in task.result!!) {
                    val item = doc.toObject(UserFriendDiaryMediaModel::class.java)
                    list.add(item)
                }
                binding.rvUserFriendDiaryMedia.layoutManager = LinearLayoutManager(requireContext())
                binding.rvUserFriendDiaryMedia.adapter = UserFriendDiaryMediaAdapter(list)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
