package com.example.mavidenergyapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.example.mavidenergyapp.databinding.FragmentEditaEnderecoBinding
import com.example.mavidenergyapp.domains.CidadeResponse
import com.example.mavidenergyapp.domains.EnderecoRequest
import com.example.mavidenergyapp.domains.EnderecoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class EditaEnderecoFragment : Fragment() {

    private var _binding: FragmentEditaEnderecoBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var enderecoAtual: EnderecoResponse? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditaEnderecoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }

        sharedViewModel.selectedEnderecoId.observe(viewLifecycleOwner) { enderecoId ->
            if (enderecoId != null) {
                buscarEndereco(enderecoId)
                binding.buttonSalvarEndereco.setOnClickListener {
                    alteraEndereco(enderecoId )
                    findNavController().navigateUp()
                }
            }
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
    }

    private fun buscarEndereco(enderecoId: String) {
        val enderecoService = Api.buildServiceEndereco()
        val cidadeService = Api.buildServiceCidade()

        lifecycleScope.launch {
            try {
                // Chamada ao endpoint
                val response = withContext(Dispatchers.IO) {
                    enderecoService.buscarEnderecoPorId(enderecoId).execute()

                }
                val responseCidade = withContext(Dispatchers.IO) {
                    cidadeService.buscarCidades().execute()
                }

                if (response.isSuccessful) {
                    val endereco = response.body()
                    if (endereco != null) {
                        displayEndereco(endereco)
                        responseCidade.body()?.let { cidade ->
                           var cidadeId = cidade.find { it.nomeCidade == endereco.nomeCidade }
                            cidadeIdSelecionada = cidadeId?.cidadeId
                        }
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

    private fun displayEndereco(endereco: EnderecoResponse) {
        binding.inputCep.setText(endereco.cep)
        binding.inputLogradouro.setText(endereco.logradouro)
        binding.inputNumero.setText(endereco.numero)
        binding.inputCep.setText(endereco.cep)
        latitude = endereco.latitude.toString()
        longitude = endereco.longitude.toString()

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



    private fun alteraEndereco(enderecoId: String) {
        if (!isAdded) return  // Verifica se o Fragmento ainda está ativo

        val cep = binding.inputCep.text.toString().takeIf { it.isNotEmpty() } ?: enderecoAtual?.cep ?: ""
        val logradouro = binding.inputLogradouro.text.toString().takeIf { it.isNotEmpty() } ?: enderecoAtual?.logradouro ?: ""
        val numero = binding.inputNumero.text.toString().takeIf { it.isNotEmpty() } ?: enderecoAtual?.numero ?: ""
        val pessoaId = SessionManager.pessoaId ?: return

        if (logradouro.isEmpty() || numero.isEmpty()) {
            Toast.makeText(context, "Preencha todos os campos obrigatórios.", Toast.LENGTH_SHORT).show()
            return
        }

        val enderecoRequest = EnderecoRequest(
            cep = cep,
            logradouro = logradouro,
            numero = numero,
            latitude = latitude ?: "0.0",
            longitude = longitude ?: "0.0",
            cidadeId = cidadeIdSelecionada ?: "",
            pessoaId = pessoaId
        )

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    Api.buildServiceEndereco().atualizarEndereco(enderecoId, enderecoRequest).execute()
                }
                if (!isAdded) return@launch  // Verifica novamente antes de atualizar a UI

                if (response.isSuccessful) {
                    Toast.makeText(context, "Endereço atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                } else {
                    Toast.makeText(context, "Erro ao atualizar endereço: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                if (!isAdded) return@launch
                Toast.makeText(context, "Erro de conexão: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}