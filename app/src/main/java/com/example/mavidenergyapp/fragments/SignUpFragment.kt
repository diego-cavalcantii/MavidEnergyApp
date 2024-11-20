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
import com.example.mavidenergyapp.databinding.FragmentSignUpBinding
import com.example.mavidenergyapp.domains.Pessoa
import com.example.mavidenergyapp.domains.PessoaResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val apiService = Api.buildServicePessoa() // Instância da API

        binding.buttonSignUp.setOnClickListener {
            val email = binding.editTextEmailAddress.text.toString()
            val password = binding.editTextPassword.text.toString()
            val username = binding.editTextUserName.text.toString()

            // Validação básica
            if (email.isBlank() || password.isBlank() || username.isBlank()) {
                Toast.makeText(requireContext(), "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Criar objeto Pessoa (request)
            val pessoa = Pessoa(nome = username, email = email, senha = password)

            // Chamada da API
            lifecycleScope.launch {
                try {
                    val response: Response<PessoaResponse> = withContext(Dispatchers.IO) {
                        apiService.criarPessoaComUsuario(pessoa).execute()
                    }

                    if (response.isSuccessful) {
                        val pessoaCriada = response.body()
                        val pessoaId = pessoaCriada?.pessoaId

                        if (pessoaId != null) {
                            // Armazena o pessoaId no SessionManager
                            SessionManager.pessoaId = pessoaId

                            Toast.makeText(requireContext(), "Usuário criado com sucesso!", Toast.LENGTH_LONG).show()

                            // Navega para a próxima tela
                            findNavController().navigate(R.id.homeFragment)
                        } else {
                            Toast.makeText(requireContext(), "Erro: ID do usuário não encontrado.", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Erro: ${response.message()}", Toast.LENGTH_LONG).show()
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
