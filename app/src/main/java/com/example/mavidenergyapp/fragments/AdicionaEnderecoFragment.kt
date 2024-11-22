package com.example.mavidenergyapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mavidenergyapp.Api
import com.example.mavidenergyapp.CepService
import com.example.mavidenergyapp.SessionManager
import com.example.mavidenergyapp.databinding.FragmentAdicionaEnderecoBinding
import com.example.mavidenergyapp.domains.CidadeResponse
import com.example.mavidenergyapp.domains.EnderecoRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AdicionaEnderecoFragment : Fragment() {

    private var _binding: FragmentAdicionaEnderecoBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentAdicionaEnderecoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }

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
                    latitude = response.lat
                    longitude = response.lng

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
                    }.start()

                    findNavController().navigateUp()
                } else {
                    Toast.makeText(requireContext(), "Erro ao salvar endereço: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Erro de conexão: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


}