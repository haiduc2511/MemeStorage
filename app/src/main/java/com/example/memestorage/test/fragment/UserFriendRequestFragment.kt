package com.example.memestorage.test.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.memestorage.databinding.FragmentUserFriendRequestBinding
import com.example.memestorage.test.model.UserFriendRequestModel
import com.example.memestorage.test.viewmodel.UserFriendRequestViewModel
import com.example.memestorage.utils.FirebaseHelper
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.QuerySnapshot

class UserFriendRequestFragment : Fragment() {

    private var _binding: FragmentUserFriendRequestBinding? = null
    private val binding get() = _binding!!
    private val myUserId = FirebaseHelper.getInstance().auth.currentUser!!.uid

    private lateinit var ufrViewModel: UserFriendRequestViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserFriendRequestBinding.inflate(inflater, container, false)
        initViewModel()
        initUI()
        return binding.root
    }

    private fun initViewModel() {
        ufrViewModel = ViewModelProvider.AndroidViewModelFactory
            .getInstance(requireActivity().application)
            .create(UserFriendRequestViewModel::class.java)
    }

    private fun initUI() {
        binding.btnAdd.setOnClickListener { addUfr() }
        binding.btnUpdate.setOnClickListener { updateUfr() }
        binding.btnDelete.setOnClickListener { deleteUfr() }
        binding.btnRead.setOnClickListener { readUfr() }
    }

    private fun addUfr() {
        val userRequested = binding.edtUserRequested.text.toString().trim()
        val userRequesting = binding.edtUserRequesting.text.toString().trim()
        val status = binding.edtStatus.text.toString().trim()

        if (userRequested.isEmpty() || userRequesting.isEmpty()) {
            Toast.makeText(requireContext(), "User fields cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val model = UserFriendRequestModel(
            ufrId = "",
            userRequested = myUserId,
            userRequesting = userRequesting,
            status = status
        )

        ufrViewModel.addUfrFirebase(model) { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Friend Request Added!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to add", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUfr() {
        val ufrId = binding.edtUserRequested.text.toString().trim() // Tạm nhập ufrId từ userRequested
        val userRequested = binding.edtUserRequested.text.toString().trim()
        val userRequesting = binding.edtUserRequesting.text.toString().trim()
        val status = binding.edtStatus.text.toString().trim()

        if (ufrId.isEmpty()) {
            Toast.makeText(requireContext(), "Provide ufrId to update", Toast.LENGTH_SHORT).show()
            return
        }

        val model = UserFriendRequestModel(
            ufrId = ufrId,
            userRequested = userRequested,
            userRequesting = userRequesting,
            status = status
        )

        ufrViewModel.updateUfrFirebase(ufrId, model) { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Friend Request Updated!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to update", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteUfr() {
        val ufrId = binding.edtUserRequested.text.toString().trim()
        if (ufrId.isEmpty()) {
            Toast.makeText(requireContext(), "Provide ufrId to delete", Toast.LENGTH_SHORT).show()
            return
        }

        ufrViewModel.deleteUfrFirebase(ufrId) { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Friend Request Deleted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to delete", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun readUfr() {
        ufrViewModel.getUfrFirebase(OnCompleteListener<QuerySnapshot> { task ->
            if (task.isSuccessful) {
                val result = StringBuilder()
                for (doc in task.result!!) {
                    val item = doc.toObject(UserFriendRequestModel::class.java)
                    result.append("ufrId: ${item.ufrId}, userRequested: ${item.userRequested}, userRequesting: ${item.userRequesting}, status: ${item.status}\n")
                }
                binding.tvResult.text = result.toString()
            } else {
                Toast.makeText(requireContext(), "Failed to read", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
