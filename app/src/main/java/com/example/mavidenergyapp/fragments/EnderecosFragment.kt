package com.example.mavidenergyapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mavidenergyapp.Api
import com.example.mavidenergyapp.CepService
import com.example.mavidenergyapp.R
import com.example.mavidenergyapp.SessionManager
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
            if (binding.layoutEditaEndereco.visibility == View.GONE) {
                binding.layoutEditaEndereco.animate().alpha(1.0f).setDuration(300).withStartAction {
                    binding.layoutEditaEndereco.visibility = View.VISIBLE
                    binding.recyclerEnderecos.visibility = View.GONE

                    // Limpar os campos ao abrir o formulário
                    binding.inputCep.text?.clear()
                    binding.inputLogradouro.text?.clear()
                    binding.inputNumero.text?.clear()
                    binding.inputCidade.text?.clear()
                    binding.inputEstado.text?.clear()

                }.start()
            } else {
                binding.layoutEditaEndereco.animate().alpha(0.0f).setDuration(300).withEndAction {
                    binding.layoutEditaEndereco.visibility = View.GONE
                    binding.recyclerEnderecos.visibility = View.VISIBLE
                }.start()
            }
        }

        // Carregar os endereços da pessoa
        loadEnderecos()

        // Consumir API de CEP ao sair do campo de CEP
        binding.inputCep.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val cep = binding.inputCep.text.toString()
                if (cep.isNotEmpty()) {
                    buscarCep(cep)
                }
            }
        }
        buscarTodasCidades()

        // Salvar endereço ao clicar no botão
        binding.buttonSalvarEndereco.setOnClickListener {
            saveEndereco()
        }
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

        // Configurar o RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = EnderecoAdapter(enderecos)
    }


    private var listaCidades: List<CidadeResponse> = listOf()
    private var cidadeIdSelecionada: String? = null

    private fun buscarTodasCidades() {
        val cidadeService = Api.buildServiceCidade()
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    cidadeService.buscarCidades().execute()
                }
                if (response.isSuccessful && response.body() != null) {
                    listaCidades = response.body()!!
                } else {
                    Toast.makeText(requireContext(), "Erro ao carregar cidades.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Erro de conexão: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }




    private var latitude: String? = null
    private var longitude: String? = null

    private fun buscarCep(cep: String) {
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    Retrofit.Builder()
                        .baseUrl("https://cep.awesomeapi.com.br/json/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(CepService::class.java)
                        .buscarCep(cep)
                }
                if (response != null) {
                    // Preencher os campos automaticamente
                    binding.inputLogradouro.setText(response.address)
                    binding.inputCidade.setText(response.city)
                    binding.inputEstado.setText(response.state)

                    val cidadeEncontrada = listaCidades.find { it.nomeCidade == response.city }

                    if (cidadeEncontrada != null) {
                        cidadeIdSelecionada = cidadeEncontrada.cidadeId
                    } else {
                        Toast.makeText(requireContext(), "Cidade não encontrada no banco.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "CEP não encontrado.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Erro ao buscar CEP: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun saveEndereco() {
        val cep = binding.inputCep.text.toString()
        val logradouro = binding.inputLogradouro.text.toString()
        val numero = binding.inputNumero.text.toString()
        val pessoaId = SessionManager.pessoaId

        if (cep.isEmpty() || logradouro.isEmpty() || numero.isEmpty() || cidadeIdSelecionada.isNullOrEmpty() || pessoaId.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Preencha todos os campos obrigatórios.", Toast.LENGTH_SHORT).show()
            return
        }

        val enderecoRequest = EnderecoRequest(
            cep = cep,
            logradouro = logradouro,
            numero = numero,
            latitude = latitude ?: "0.0",
            longitude = longitude ?: "0.0",
            cidadeId = cidadeIdSelecionada!!,
            pessoaId = pessoaId
        )

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    Api.buildServiceEndereco().adicionarEndereco(enderecoRequest).execute()
                }

                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Endereço salvo com sucesso!", Toast.LENGTH_SHORT).show()
                    binding.layoutEditaEndereco.animate().alpha(0.0f).setDuration(300).withEndAction {
                        binding.layoutEditaEndereco.visibility = View.GONE
                        binding.recyclerEnderecos.visibility = View.VISIBLE
                    }.start()
                    loadEnderecos() // Recarregar a lista de endereços
                } else {
                    Toast.makeText(requireContext(), "Erro ao salvar endereço: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Erro de conexão: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}