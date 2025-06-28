package com.example.memestorage.test.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.memestorage.databinding.FragmentMySavedMediaBinding
import com.example.memestorage.test.model.MySavedMediaModel
import com.example.memestorage.test.viewmodel.MySavedMediaViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.QuerySnapshot

class MySavedMediaFragment : Fragment() {

    private var _binding: FragmentMySavedMediaBinding? = null
    private val binding get() = _binding!!

    private lateinit var mySavedMediaViewModel: MySavedMediaViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMySavedMediaBinding.inflate(inflater, container, false)
        initViewModel()
        initUI()
        return binding.root
    }

    private fun initViewModel() {
        mySavedMediaViewModel = ViewModelProvider.AndroidViewModelFactory
            .getInstance(requireActivity().application)
            .create(MySavedMediaViewModel::class.java)
    }

    private fun initUI() {
        binding.btnAdd.setOnClickListener { addMySavedMedia() }
        binding.btnUpdate.setOnClickListener { updateMySavedMedia() }
        binding.btnDelete.setOnClickListener { deleteMySavedMedia() }
        binding.btnRead.setOnClickListener { readMySavedMedia() }
    }

    private fun addMySavedMedia() {
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

        val model = MySavedMediaModel(
            msmId = "",
            mediaId = mediaId,
            timeSaved = timeSaved
        )

        mySavedMediaViewModel.addMySavedMediaFirebase(model) { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Saved Media Added!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to add", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateMySavedMedia() {
        val msmId = binding.edtMediaId.text.toString().trim() // Tạm: msmId nhập từ edtMediaId
        val mediaId = binding.edtMediaId.text.toString().trim()
        val timeSavedStr = binding.edtTimeSaved.text.toString().trim()

        if (msmId.isEmpty()) {
            Toast.makeText(requireContext(), "Provide msmId to update", Toast.LENGTH_SHORT).show()
            return
        }

        val timeSaved = timeSavedStr.toIntOrNull()
        if (timeSaved == null) {
            Toast.makeText(requireContext(), "Time Saved must be Int", Toast.LENGTH_SHORT).show()
            return
        }

        val model = MySavedMediaModel(
            msmId = msmId,
            mediaId = mediaId,
            timeSaved = timeSaved
        )

        mySavedMediaViewModel.updateMySavedMediaFirebase(msmId, model) { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Saved Media Updated!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to update", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteMySavedMedia() {
        val msmId = binding.edtMediaId.text.toString().trim()
        if (msmId.isEmpty()) {
            Toast.makeText(requireContext(), "Provide msmId to delete", Toast.LENGTH_SHORT).show()
            return
        }

        mySavedMediaViewModel.deleteMySavedMediaFirebase(msmId) { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Saved Media Deleted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to delete", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun readMySavedMedia() {
        mySavedMediaViewModel.getMySavedMediaFirebase(OnCompleteListener<QuerySnapshot> { task ->
            if (task.isSuccessful) {
                val result = StringBuilder()
                for (doc in task.result!!) {
                    val item = doc.toObject(MySavedMediaModel::class.java)
                    result.append("msmId: ${item.msmId}, mediaId: ${item.mediaId}, timeSaved: ${item.timeSaved}\n")
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
