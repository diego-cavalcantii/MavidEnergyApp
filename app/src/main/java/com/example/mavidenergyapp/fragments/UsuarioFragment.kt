package com.example.mavidenergyapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.mavidenergyapp.Menu
import com.example.mavidenergyapp.R
import com.example.mavidenergyapp.databinding.FragmentUsuarioBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class UsuarioFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private var _binding: FragmentUsuarioBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUsuarioBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menu = Menu(binding.root, findNavController())
        menu.setupMenu()

        binding.logout.setOnClickListener {
            auth.signOut()
            findNavController().navigate(R.id.loginFragment)
        }

        binding.buttonDados.setOnClickListener{
            findNavController().navigate(R.id.dadosUsuarioFragment)
        }

        binding.buttonHistorico.setOnClickListener{
            findNavController().navigate(R.id.historyFragment)
        }
    }

}