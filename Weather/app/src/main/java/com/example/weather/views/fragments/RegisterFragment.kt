package com.example.weather.views.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.weather.R
import com.example.weather.databinding.FragmentRegisterBinding
import com.example.weather.db.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterFragment : Fragment() {

    private val TAG = "RegisterFragment"

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var _auth: FirebaseAuth
    private lateinit var _databaseInst: FirebaseDatabase
    private lateinit var _databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _auth = FirebaseAuth.getInstance()
        _databaseInst = FirebaseDatabase.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnRegister.setOnClickListener {
            registerUser(
                binding.etName.text.toString(),
                binding.etEmail.text.toString().trim(),
                binding.etPw.text.toString()
            )
        }

        Log.d(TAG, "NavStack: ${findNavController().backStack}")

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun registerUser(name: String, email: String, password: String) {
        if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
            _auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            requireContext(),
                            "User created successfully",
                            Toast.LENGTH_LONG
                        ).show()

                        saveUserInDb()
                        findNavController().popBackStack()
                    } else {
                        Toast.makeText(requireContext(), "User creation failed", Toast.LENGTH_LONG)
                            .show()
                        Log.d(TAG, "Exception: ${task.exception}")
                    }
                }
        }
        else {
            Toast.makeText(requireContext(), "Wrong input", Toast.LENGTH_LONG).show()
        }
    }

    private fun saveUserInDb() {
        /* add node for user in DB */
        _databaseRef = _databaseInst.getReference("users")

        /* add user to DB */
        val userId = _auth.currentUser?.uid
        val user = User(
            _auth.currentUser?.email!!,
            listOf()
        )

        _databaseRef.child(userId!!).setValue(user)
    }
}