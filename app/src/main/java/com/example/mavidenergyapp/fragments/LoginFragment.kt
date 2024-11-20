package com.example.mavidenergyapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mavidenergyapp.Api
import com.example.mavidenergyapp.R
import com.example.mavidenergyapp.SessionManager
import com.example.mavidenergyapp.databinding.FragmentLoginBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!  // Safe property access

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        if (auth.currentUser != null) {
            findNavController().navigate(R.id.homeFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val apiService = Api.buildServicePessoa() // Instância da API

        binding.buttonSignUp.setOnClickListener {
            findNavController().navigate(R.id.signUpFragment)
        }

        binding.buttonLogin.setOnClickListener {
            val email = binding.editTextEmailAddress.text.toString()
            val password = binding.editTextPassword.text.toString()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(requireContext(), "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    // Chamada da API para obter todas as pessoas
                    val response = withContext(Dispatchers.IO) {
                        apiService.buscarPessoas().execute()
                    }

                    if (response.isSuccessful) {
                        val pessoas = response.body() ?: emptyList()
                        val pessoaEncontrada = pessoas.find { it.email == email }

                        if (pessoaEncontrada != null) {
                            // Armazena o pessoaId no SessionManager
                            SessionManager.pessoaId = pessoaEncontrada.pessoaId

                            // Aqui você pode fazer validações adicionais (ex.: senha) ou apenas navegar
                            Toast.makeText(requireContext(), "Login bem-sucedido!", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.homeFragment)
                        } else {
                            Toast.makeText(requireContext(), "Email não encontrado!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Erro: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Erro de conexão: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}