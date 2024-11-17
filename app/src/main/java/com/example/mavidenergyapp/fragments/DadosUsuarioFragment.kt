package com.example.mavidenergyapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.mavidenergyapp.R
import com.example.mavidenergyapp.databinding.FragmentDadosUsuarioBinding

class DadosUsuarioFragment : Fragment() {

    private var _binding: FragmentDadosUsuarioBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDadosUsuarioBinding.inflate(inflater, container, false)

        binding.layoutEditaEndereco.visibility = View.GONE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.buttonBack.setOnClickListener {
            findNavController().navigate(R.id.usuarioFragment)
        }
    }
}