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
import com.example.mavidenergyapp.Menu
import com.example.mavidenergyapp.R
import com.example.mavidenergyapp.SessionManager
import com.example.mavidenergyapp.databinding.FragmentUsuarioBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UsuarioFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private var _binding: FragmentUsuarioBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
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

        val pessoaId = SessionManager.pessoaId

        if (pessoaId == null) {
            findNavController().navigate(R.id.loginFragment)
            return
        }

        // Carregar os dados do usuário
        loadUserData(pessoaId)

        // Configurar o menu
        val menu = Menu(binding.root, findNavController())
        menu.setupMenu()

        // Logout
        binding.logout.setOnClickListener {
            auth.signOut()
            findNavController().navigate(R.id.loginFragment)
        }

        // Navegar para os detalhes do usuário
        binding.buttonDados.setOnClickListener {
            findNavController().navigate(R.id.enderecosFragment)
        }

        // Navegar para o histórico
        binding.buttonHistorico.setOnClickListener {
            findNavController().navigate(R.id.historyFragment)
        }
    }

    private fun loadUserData(pessoaId: String) {
        val apiService = Api.buildServicePessoa()

        lifecycleScope.launch {
            try {
                // Executa a chamada em um Thread de Background
                val response = withContext(Dispatchers.IO) {
                    apiService.buscarPessoaPorId(pessoaId).execute()
                }

                if (response.isSuccessful) {
                    val pessoa = response.body()
                    if (pessoa != null) {
                        // Exibir os dados na tela
                        binding.textViewNomeUsuario.text = pessoa.nome.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase() else it.toString()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Nenhum dado encontrado.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Erro ao buscar dados: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Erro de conexão: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
