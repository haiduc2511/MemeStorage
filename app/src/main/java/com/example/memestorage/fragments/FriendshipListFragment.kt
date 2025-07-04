package com.example.memestorage.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.memestorage.databinding.FragmentFriendshipListBinding
import com.example.memestorage.adapterver2.FriendshipAdapter
import com.example.memestorage.test.model.UserFriendshipModel
import com.example.memestorage.test.viewmodel.UserFriendshipViewModel
import com.example.memestorage.utils.FirebaseHelper
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.QuerySnapshot

class FriendshipListFragment : Fragment() {

    private var _binding: FragmentFriendshipListBinding? = null
    private val binding get() = _binding!!
    private val myUserId = FirebaseHelper.getInstance().auth.currentUser!!.uid
    private lateinit var ufViewModel: UserFriendshipViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendshipListBinding.inflate(inflater, container, false)
        setupRecyclerView()
        initViewModel()
        fetchFriendships()
        return binding.root
    }

    private fun initViewModel() {
        ufViewModel = ViewModelProvider.AndroidViewModelFactory
            .getInstance(requireActivity().application)
            .create(UserFriendshipViewModel::class.java)
    }

    private fun setupRecyclerView() {
        val list = mutableListOf<UserFriendshipModel>() // Placeholder, fetch actual data here
        binding.rvFriendList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFriendList.adapter = FriendshipAdapter(list) { friendship ->
            // Handle item click, for example, open chat with selected friend
        }
    }

    private fun fetchFriendships() {
        ufViewModel.getUfFirebase(OnCompleteListener<QuerySnapshot> { task ->
            if (task.isSuccessful) {
                val list = mutableListOf<UserFriendshipModel>()
                for (doc in task.result!!) {
                    val item = doc.toObject(UserFriendshipModel::class.java)
                    list.add(item)
                }
                binding.rvFriendList.layoutManager = LinearLayoutManager(requireContext())
                binding.rvFriendList.adapter = FriendshipAdapter(list) { friendship ->
                    // Handle item click to open chat or perform action
                }
            }
        })
//        ufViewModel.getUfByUser1Id(myUserId, OnCompleteListener<QuerySnapshot> { task ->
//            if (task.isSuccessful) {
//                val list = mutableListOf<UserFriendshipModel>()
//                for (doc in task.result!!) {
//                    val item = doc.toObject(UserFriendshipModel::class.java)
//                    list.add(item)
//                }
//                ufViewModel.getUfByUser2Id(myUserId, OnCompleteListener<QuerySnapshot> { task ->
//                    if (task.isSuccessful) {
//                        for (doc in task.result!!) {
//                            val item = doc.toObject(UserFriendshipModel::class.java)
//                            list.add(item)
//                        }
//
//                        binding.rvFriendList.layoutManager = LinearLayoutManager(requireContext())
//                        binding.rvFriendList.adapter = FriendshipAdapter(list) { friendship ->
//                            // Handle item click to open chat or perform action
//                        }
//                    }
//                })
//
//            }
//        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
