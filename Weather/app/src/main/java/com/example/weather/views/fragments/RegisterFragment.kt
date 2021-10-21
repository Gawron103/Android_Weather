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
import com.google.firebase.auth.FirebaseAuth

class RegisterFragment : Fragment() {

    private val TAG = "RegisterFragment"

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private var _auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        binding.btnBack.setOnClickListener {
            val res = findNavController().popBackStack()
            Log.d(TAG, "back clicked. Res: $res")
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
        Log.d(TAG, "email: $email")
        Log.d(TAG, "password: $password")

        _auth?.let { auth ->
            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(requireContext(), "User created successfully", Toast.LENGTH_LONG).show()
                            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                        }
                        else {
                            Toast.makeText(requireContext(), "User creation failed", Toast.LENGTH_LONG).show()
                            Log.d(TAG, "Exception: ${task.exception}")
                        }
                    }
            }
            else {
                Toast.makeText(requireContext(), "Wrong input", Toast.LENGTH_LONG).show()
            }
        }
    }

}