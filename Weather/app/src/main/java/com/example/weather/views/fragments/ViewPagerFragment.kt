package com.example.weather.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.weather.R
import com.example.weather.databinding.FragmentViewPagerBinding
import com.example.weather.views.adapters.ViewPagerAdapter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class ViewPagerFragment : Fragment() {

    private val TAG = "ViewPagerFragment"

    private var _binding: FragmentViewPagerBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewPagerBinding.inflate(inflater, container, false)

        val fragments = arrayListOf(
            CurrentLocationFragment(),
            CitiesPageFragment()
        )

        val adapter = ViewPagerAdapter(
            fragments,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        binding.viewPager.adapter = adapter

        setupToolbar()

        return binding.root
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

    private fun signOutEmail() {
        FirebaseAuth.getInstance().signOut()
        findNavController().popBackStack(R.id.startFragment, false)
    }

    private fun signOutGoogle() {
        FirebaseAuth.getInstance().signOut()
        val googleClient = GoogleSignIn.getClient(requireContext(), getGoogleSignOptions())
        googleClient.signOut().addOnCompleteListener {
            findNavController().popBackStack(R.id.startFragment, false)
        }
    }

    private fun getGoogleSignOptions() = GoogleSignInOptions
        .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(R.string.google_default_web_client_id.toString())
        .requestEmail()
        .build()

}