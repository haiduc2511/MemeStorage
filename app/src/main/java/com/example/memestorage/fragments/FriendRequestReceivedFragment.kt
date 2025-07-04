package com.example.memestorage.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.memestorage.databinding.FragmentFriendRequestReceivedBinding
import com.example.memestorage.adapterver2.FriendRequestAdapter
import com.example.memestorage.test.model.UserFriendRequestModel
import com.example.memestorage.test.viewmodel.UserFriendRequestViewModel
import com.example.memestorage.utils.FirebaseHelper
import com.google.firebase.firestore.QuerySnapshot
import com.google.android.gms.tasks.OnCompleteListener

class FriendRequestReceivedFragment : Fragment() {

    private var _binding: FragmentFriendRequestReceivedBinding? = null
    private val binding get() = _binding!!
    private lateinit var friendRequestViewModel: UserFriendRequestViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendRequestReceivedBinding.inflate(inflater, container, false)
        setupRecyclerView()
        initViewModel()
        fetchFriendRequests()
        return binding.root
    }

    private fun setupRecyclerView() {
        val requestList = mutableListOf<UserFriendRequestModel>() // Placeholder, fetch actual data here
        binding.rvFriendRequestReceived.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFriendRequestReceived.adapter = FriendRequestAdapter(requestList) { request ->
            // Handle request click, for example, accept or reject request
        }
    }
    private fun initViewModel() {
        friendRequestViewModel = ViewModelProvider.AndroidViewModelFactory
            .getInstance(requireActivity().application)
            .create(UserFriendRequestViewModel::class.java)
    }

    private fun fetchFriendRequests() {
        friendRequestViewModel.getUfrByUserRequested(FirebaseHelper.getInstance().auth.currentUser!!.uid, OnCompleteListener<QuerySnapshot> { task ->
            if (task.isSuccessful) {
                val list = mutableListOf<UserFriendRequestModel>()
                for (doc in task.result!!) {
                    val item = doc.toObject(UserFriendRequestModel::class.java)
                    list.add(item)
                }
                binding.rvFriendRequestReceived.layoutManager = LinearLayoutManager(requireContext())
                binding.rvFriendRequestReceived.adapter = FriendRequestAdapter(list) { request ->
                    // Handle item click to accept or reject friend request
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
