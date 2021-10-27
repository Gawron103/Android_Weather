package com.example.weather.views.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.weather.R
import com.example.weather.databinding.FragmentCurrentLocationBinding
import com.example.weather.databinding.FragmentWeatherBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class WeatherFragment : Fragment() {

    private val TAG = "WeatherFragment"

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)

        setupToolbar()
        setupBottomNav()

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

        private fun setupToolbar() {
        binding.toolbar.inflateMenu(R.menu.menu)

        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_action_logout -> {
                    val user = FirebaseAuth.getInstance().currentUser!!
                    Log.d(TAG, "Provider id: ${user.providerData[user.providerData.size - 1].providerId}")
                    when (user.providerData[user.providerData.size - 1].providerId) {
                        "password" -> {
                            signOutEmail()
                            true
                        }
                        "google.com" -> {
                            signOutGoogle()
                            true
                        }
                        else -> {
                            signOutEmail()
                            true
                        }
                    }
                }
                else -> {
                    super.onOptionsItemSelected(item)
                }
            }
        }
    }

    private fun setupBottomNav() {
        val nestedNavHostFragment = childFragmentManager.findFragmentById(R.id.weatherNavHost) as NavHostFragment
        val navController = nestedNavHostFragment.navController
        binding.bnNavigation.setupWithNavController(navController)
    }

    private fun signOutEmail() {
        FirebaseAuth.getInstance().signOut()
        findNavController().popBackStack(R.id.startFragment, false)
    }

    private fun signOutGoogle() {
        FirebaseAuth.getInstance().signOut()
        val googleClient = GoogleSignIn.getClient(requireContext(), getGoogleSignOptions())
        googleClient.signOut().addOnCompleteListener {
            googleClient.revokeAccess().addOnCompleteListener {
                findNavController().popBackStack(R.id.startFragment, false)
            }
        }
    }

    private fun getGoogleSignOptions() = GoogleSignInOptions
        .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(R.string.google_default_web_client_id.toString())
        .requestEmail()
        .build()

}