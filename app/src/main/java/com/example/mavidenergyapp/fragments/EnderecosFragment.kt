package com.example.mavidenergyapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mavidenergyapp.Api
import com.example.mavidenergyapp.CepService
import com.example.mavidenergyapp.R
import com.example.mavidenergyapp.SessionManager
import com.example.mavidenergyapp.SharedViewModel
import com.example.mavidenergyapp.adapters.EnderecoAdapter
import com.example.mavidenergyapp.databinding.FragmentEnderecosBinding
import com.example.mavidenergyapp.domains.CidadeResponse
import com.example.mavidenergyapp.domains.EnderecoRequest
import com.example.mavidenergyapp.domains.EnderecoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

class EnderecosFragment : Fragment() {

    private var _binding: FragmentEnderecosBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEnderecosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.buttonAddEndereco.setOnClickListener {
            findNavController().navigate(R.id.adicionaEnderecoFragment)
        }

        // Carregar os endereços da pessoa
        loadEnderecos()

    }

    private fun loadEnderecos() {
        val pessoaId = SessionManager.pessoaId
        if (pessoaId == null) {
            Toast.makeText(requireContext(), "Erro: pessoaId não encontrado.", Toast.LENGTH_SHORT).show()
            return
        }

        val enderecoService = Api.buildServiceEndereco()

        lifecycleScope.launch {
            try {
                // Chamada ao endpoint
                val response = withContext(Dispatchers.IO) {
                    enderecoService.buscarEnderecoPorPessoa(pessoaId).execute()
                }

                if (response.isSuccessful) {
                    val enderecos = response.body()
                    if (!enderecos.isNullOrEmpty()) {
                        displayEnderecos(enderecos)
                    } else {
                        Toast.makeText(requireContext(), "Nenhum endereço encontrado.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Erro: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Erro de conexão: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayEnderecos(enderecos: List<EnderecoResponse>) {
        val recyclerView = binding.recyclerEnderecos

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = EnderecoAdapter(enderecos, { endereco ->
            // Ação para um clique normal no item
        }, { endereco ->
            // Ação para o clique no botão editar
            sharedViewModel.selectedEnderecoId.value = endereco.enderecoId
            findNavController().navigate(R.id.editaEnderecoFragment)
        }, { endereco ->
            // Ação para o clique no botão excluir
            deleteEndereco(endereco.enderecoId)
        })
    }

    private fun deleteEndereco(enderecoId: String) {
        val enderecoService = Api.buildServiceEndereco()

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    enderecoService.deletarEndereco(enderecoId).execute()
                }

                if (response.isSuccessful) {
                    if (isAdded) { // Checa se o fragmento ainda está ativo
                        Toast.makeText(requireContext(), "Endereço excluído com sucesso!", Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp() // Navega para cima somente se o fragmento estiver ativo
                    }
                } else {
                    if (isAdded) {
                        Toast.makeText(requireContext(), "Erro ao excluir endereço: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                if (isAdded) {
                    Toast.makeText(requireContext(), "Erro de conexão: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}