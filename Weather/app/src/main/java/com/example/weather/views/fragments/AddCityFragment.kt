package com.example.weather.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.weather.R
import com.example.weather.databinding.FragmentAddCityBinding
import com.example.weather.utils.InputValidator

class AddCityFragment : Fragment() {

    private var _binding: FragmentAddCityBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddCityBinding.inflate(inflater, container, false)

        binding.btnAddCity.setOnClickListener {
            val isInputCityValid = InputValidator.checkInput(binding.etCityNameInput.text.toString())
            if (isInputCityValid) {
                val action = AddCityFragmentDirections.actionFromAddCityToCitiesList(
                    binding.etCityNameInput.text.toString()
                )
                findNavController().navigate(action)
            }
            else {
                Toast.makeText(requireContext(), "Input not valid", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnBackToCitiesView.setOnClickListener {
            findNavController().navigate(R.id.action_FromAddCityToCitiesList)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}