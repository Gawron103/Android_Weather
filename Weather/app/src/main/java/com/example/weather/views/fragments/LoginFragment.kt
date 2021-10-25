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
import com.example.weather.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private val TAG = "LoginFragment"

    private var _binding: FragmentLoginBinding? = null
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
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.pbLogging.visibility = View.GONE

        binding.btnLogin.setOnClickListener {
            login(
                binding.etEmail.text.toString().trim(),
                binding.etPw.text.toString()
            )
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvNoAccount.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        Log.d(TAG, "NavStack: ${findNavController().backStack}")

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun login(email: String, password: String) {
        binding.pbLogging.visibility = View.VISIBLE
        binding.cvLoginInput.visibility = View.GONE

        _auth?.let { auth ->
            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(requireContext(), "User logged in successfully", Toast.LENGTH_LONG).show()
                            binding.pbLogging.visibility = View.GONE
                            findNavController().navigate(R.id.action_loginFragment_to_viewPagerFragment)
                        }
                        else {
                            Toast.makeText(requireContext(), "Wrong email or password", Toast.LENGTH_LONG).show()
                            Log.d(TAG, "Exception: ${task.exception}")
                            binding.pbLogging.visibility = View.GONE
                            binding.cvLoginInput.visibility = View.VISIBLE
                        }
                    }
            }
            else {
                Toast.makeText(requireContext(), "Wrong input", Toast.LENGTH_LONG).show()
                binding.pbLogging.visibility = View.GONE
                binding.cvLoginInput.visibility = View.VISIBLE
            }
        }
    }

}