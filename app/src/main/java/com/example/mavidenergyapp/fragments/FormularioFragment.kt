package com.example.mavidenergyapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.mavidenergyapp.R
import com.example.mavidenergyapp.databinding.FragmentFormularioBinding

class FormularioFragment : Fragment() {

    private var _binding: FragmentFormularioBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormularioBinding.inflate(inflater, container, false)

        binding.ajudaKWH.visibility = View.GONE
        binding.layoutLuz.visibility = View.VISIBLE
        binding.layoutEnderecos.visibility = View.GONE
        binding.layoutNovoEndereco.visibility = View.GONE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backFormulario.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }

        binding.buttonContaLuz.setOnClickListener{
            binding.ajudaKWH.visibility = View.VISIBLE
        }

        binding.root.setOnClickListener{
            binding.ajudaKWH.visibility = View.GONE
        }

        binding.buttonEndereco.setOnClickListener {
            binding.layoutLuz.visibility = View.GONE
            binding.layoutEnderecos.visibility = View.VISIBLE
        }

        binding.buttonAdicionarEndereco.setOnClickListener {
            binding.layoutEnderecos.visibility = View.GONE
            binding.layoutNovoEndereco.visibility = View.VISIBLE
        }
    }


}