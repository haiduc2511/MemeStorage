package com.example.memestorage.test.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.memestorage.databinding.FragmentMyLikedMediaBinding
import com.example.memestorage.test.model.MyLikedMediaModel
import com.example.memestorage.test.viewmodel.MyLikedMediaViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.QuerySnapshot

class MyLikedMediaFragment : Fragment() {

    private var _binding: FragmentMyLikedMediaBinding? = null
    private val binding get() = _binding!!

    private lateinit var myLikedMediaViewModel: MyLikedMediaViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyLikedMediaBinding.inflate(inflater, container, false)
        initViewModel()
        initUI()
        return binding.root
    }

    private fun initViewModel() {
        myLikedMediaViewModel = ViewModelProvider.AndroidViewModelFactory
            .getInstance(requireActivity().application)
            .create(MyLikedMediaViewModel::class.java)
    }

    private fun initUI() {
        binding.btnAdd.setOnClickListener { addMyLikedMedia() }
        binding.btnUpdate.setOnClickListener { updateMyLikedMedia() }
        binding.btnDelete.setOnClickListener { deleteMyLikedMedia() }
        binding.btnRead.setOnClickListener { readMyLikedMedia() }
    }

    private fun addMyLikedMedia() {
        val mediaId = binding.edtMediaId.text.toString().trim()
        val timeSavedStr = binding.edtTimeSaved.text.toString().trim()

        if (mediaId.isEmpty() || timeSavedStr.isEmpty()) {
            Toast.makeText(requireContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val timeSaved = timeSavedStr.toIntOrNull()
        if (timeSaved == null) {
            Toast.makeText(requireContext(), "Time Saved must be Int", Toast.LENGTH_SHORT).show()
            return
        }

        val model = MyLikedMediaModel(
            mlmId = "",
            mediaId = mediaId,
            timeSaved = timeSaved
        )

        myLikedMediaViewModel.addMyLikedMediaFirebase(model) { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Liked Media Added!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to add", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateMyLikedMedia() {
        val mlmId = binding.edtMediaId.text.toString().trim() // Tạm: mlmId nhập từ edtMediaId
        val mediaId = binding.edtMediaId.text.toString().trim()
        val timeSavedStr = binding.edtTimeSaved.text.toString().trim()

        if (mlmId.isEmpty()) {
            Toast.makeText(requireContext(), "Provide mlmId to update", Toast.LENGTH_SHORT).show()
            return
        }

        val timeSaved = timeSavedStr.toIntOrNull()
        if (timeSaved == null) {
            Toast.makeText(requireContext(), "Time Saved must be Int", Toast.LENGTH_SHORT).show()
            return
        }

        val model = MyLikedMediaModel(
            mlmId = mlmId,
            mediaId = mediaId,
            timeSaved = timeSaved
        )

        myLikedMediaViewModel.updateMyLikedMediaFirebase(mlmId, model) { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Liked Media Updated!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to update", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteMyLikedMedia() {
        val mlmId = binding.edtMediaId.text.toString().trim()
        if (mlmId.isEmpty()) {
            Toast.makeText(requireContext(), "Provide mlmId to delete", Toast.LENGTH_SHORT).show()
            return
        }

        myLikedMediaViewModel.deleteMyLikedMediaFirebase(mlmId) { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Liked Media Deleted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to delete", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun readMyLikedMedia() {
        myLikedMediaViewModel.getMyLikedMediaFirebase(OnCompleteListener<QuerySnapshot> { task ->
            if (task.isSuccessful) {
                val result = StringBuilder()
                for (doc in task.result!!) {
                    val item = doc.toObject(MyLikedMediaModel::class.java)
                    result.append("mlmId: ${item.mlmId}, mediaId: ${item.mediaId}, timeSaved: ${item.timeSaved}\n")
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
