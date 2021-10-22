package com.example.weather.views.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.weather.R
import com.example.weather.databinding.FragmentStartBinding
import com.example.weather.utils.Const
import com.example.weather.utils.PermissionsChecker
import com.google.firebase.auth.FirebaseAuth

class StartFragment : Fragment() {

    private val TAG = "StartFragment"

    private var _binding: FragmentStartBinding? = null
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
        _binding = FragmentStartBinding.inflate(inflater, container, false)

        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_startFragment_to_loginFragment)
        }

        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_startFragment_to_registerFragment)
        }

        Log.d(TAG, "NavStack: ${findNavController().backStack}")

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        checkPermissions()
    }

    private fun checkPermissions() {
        if(!PermissionsChecker.hasLocalizationPermissions(requireContext(), Const.neededPermissions)) {
            // permissions not granted, request
            permissionsRequesterLauncher.launch(Const.neededPermissions)
        }
        else {
            // permissions already granted, check if user already signed in
            isUserAlreadySigned()
        }
    }

    private val permissionsRequesterLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (result.any { it.value != true }) {
            findNavController().navigate(R.id.action_viewPagerFragment_to_noPermissionsFragment)
        }
        else {
            isUserAlreadySigned()
        }
    }

    private fun isUserAlreadySigned() {
        _auth?.let { auth ->
            auth.currentUser?.let {
                // user already logged in. go to the current location view
                Log.d(TAG, "User already logged in, redirecting")
                findNavController().navigate(R.id.action_startFragment_to_viewPagerFragment)
            }
        }
    }
}