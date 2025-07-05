package com.example.memestorage.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.memestorage.databinding.FragmentFriendRequestReceivedBinding
import com.example.memestorage.adapterver2.FriendRequestAdapter
import com.example.memestorage.test.model.UserFriendRequestModel
import com.example.memestorage.test.model.UserFriendshipModel
import com.example.memestorage.test.viewmodel.UserFriendRequestViewModel
import com.example.memestorage.test.viewmodel.UserFriendshipViewModel
import com.example.memestorage.utils.FirebaseHelper
import com.google.firebase.firestore.QuerySnapshot
import com.google.android.gms.tasks.OnCompleteListener

class FriendRequestReceivedFragment : Fragment() {

    private var _binding: FragmentFriendRequestReceivedBinding? = null
    private val binding get() = _binding!!
    private lateinit var friendRequestViewModel: UserFriendRequestViewModel
    private lateinit var friendshipViewModel: UserFriendshipViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendRequestReceivedBinding.inflate(inflater, container, false)
//        setupRecyclerView()
        initViewModel()
        fetchFriendRequests()
        return binding.root
    }

//    private fun setupRecyclerView() {
//        val requestList = mutableListOf<UserFriendRequestModel>() // Placeholder, fetch actual data here
//        binding.rvFriendRequestReceived.layoutManager = LinearLayoutManager(requireContext())
//        binding.rvFriendRequestReceived.adapter = FriendRequestAdapter(requestList) { request ->
//            // Handle request click, for example, accept or reject request
//        }
//    }
    private fun initViewModel() {
        friendRequestViewModel = ViewModelProvider.AndroidViewModelFactory
            .getInstance(requireActivity().application)
            .create(UserFriendRequestViewModel::class.java)
        friendshipViewModel = ViewModelProvider.AndroidViewModelFactory
            .getInstance(requireActivity().application)
            .create(UserFriendshipViewModel::class.java)

    }
    private fun addFriendship(request: UserFriendRequestModel) {
        // Create a new Friendship model from the friend request data
        val friendship = UserFriendshipModel(
            user1Id = request.userRequesting ?: "",
            user2Id = request.userRequested ?: ""
        )

        // Add Friendship using FriendshipViewModel
        friendshipViewModel.addUfFirebase(friendship, OnCompleteListener { addTask ->
            if (addTask.isSuccessful) {
                // Successfully added Friendship, now delete the FriendRequest
                friendRequestViewModel.deleteUfrFirebase(request.ufrId) {

                }
                Toast.makeText(requireContext(), "Friendship accepted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to add Friendship", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun rejectFriendRequest(request: UserFriendRequestModel) {
        // Delete FriendRequest using FriendRequestViewModel
        friendRequestViewModel.deleteUfrFirebase(request.ufrId, OnCompleteListener { deleteTask ->
            if (deleteTask.isSuccessful) {
                Toast.makeText(requireContext(), "Friend request rejected", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to reject friend request", Toast.LENGTH_SHORT).show()
            }
        })
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
                binding.rvFriendRequestReceived.adapter = FriendRequestAdapter(list, { request ->
                    // Handle item click to accept or reject friend request
                }, { request ->
                    // Handle accept
                    addFriendship(request)
                    Toast.makeText(requireContext(), "Accepted friend request from: ${request.userRequesting}", Toast.LENGTH_SHORT).show()
                }, { request ->
                    // Handle reject
                    rejectFriendRequest(request)
                    Toast.makeText(requireContext(), "Rejected friend request from: ${request.userRequesting}", Toast.LENGTH_SHORT).show()
                })

            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
