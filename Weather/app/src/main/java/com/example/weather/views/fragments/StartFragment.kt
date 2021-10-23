package com.example.weather.views.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.example.weather.R
import com.example.weather.databinding.FragmentStartBinding
import com.example.weather.utils.Const
import com.example.weather.utils.PermissionsChecker
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton.SIZE_WIDE
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class StartFragment : Fragment() {

    private val TAG = "StartFragment"

    private var _binding: FragmentStartBinding? = null
    private val binding get() = _binding!!

    private var _auth: FirebaseAuth? = null
    private lateinit var _googleResultLauncher: ActivityResultLauncher<Intent>

    private lateinit var _googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _auth = FirebaseAuth.getInstance()

        _googleResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (Activity.RESULT_OK == result.resultCode) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult((ApiException::class.java))
                    firebaseAuthWithGoogle(account)
                }
                catch (exception: ApiException) {
                    Toast.makeText(requireContext(), "Google authentication failed", Toast.LENGTH_LONG).show()
                    Log.d(TAG, "Google auth exception: ${exception}")
                }
            }
        }
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

        binding.googleSignInButton.setSize(SIZE_WIDE)
        binding.googleSignInButton.setOnClickListener {
            googleLogin()
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

    private fun googleLogin() {
        _googleSignInClient = GoogleSignIn.getClient(requireContext(), getGoogleSignOptions())
        val signInIntent = _googleSignInClient.signInIntent
        _googleResultLauncher.launch(signInIntent)
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
        _auth?.let { auth ->
            auth.signInWithCredential(credentials)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        findNavController().navigate(R.id.action_startFragment_to_viewPagerFragment)
                    }
                    else {
                        Log.d(TAG, "Google authorization failed")
                    }
                }
        }
    }

    private fun getGoogleSignOptions() = GoogleSignInOptions
        .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(requireContext().getString(R.string.google_default_web_client_id))
        .requestEmail()
        .build()

}