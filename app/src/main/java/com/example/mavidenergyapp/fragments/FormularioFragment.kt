package com.example.mavidenergyapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mavidenergyapp.Api
import com.example.mavidenergyapp.R
import com.example.mavidenergyapp.SessionManager
import com.example.mavidenergyapp.SharedViewModel
import com.example.mavidenergyapp.adapters.EnderecoAdapter
import com.example.mavidenergyapp.databinding.FragmentFormularioBinding
import com.example.mavidenergyapp.domains.ConsultaRequest
import com.example.mavidenergyapp.domains.EnderecoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FormularioFragment : Fragment() {

    private var _binding: FragmentFormularioBinding? = null
    private val binding get() = _binding!!
    private var bandeiraSelecionada: String? = null
    private var enderecoIdSelecionado: String? = null
    private val viewModel: SharedViewModel by activityViewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormularioBinding.inflate(inflater, container, false)


        binding.layoutLuz.visibility = View.VISIBLE
        binding.layoutEnderecos.visibility = View.GONE
        binding.layoutPreResultado.visibility = View.GONE

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

        binding.buttonEndereco.setOnClickListener {
            binding.layoutLuz.visibility = View.GONE
            binding.layoutEnderecos.visibility = View.VISIBLE
            loadEnderecos()
        }


        val bandeiras = arrayOf("Verde", "Amarela", "Vermelha")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, bandeiras)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerBandeira.adapter = adapter


        binding.spinnerBandeira.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                bandeiraSelecionada = bandeiras[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                bandeiraSelecionada = null
            }
        }
        binding.buttonSalvarConsulta.setOnClickListener {
            saveConsulta()
            findNavController().navigate(R.id.resultadoConsultaFragment)
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

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = EnderecoAdapter(enderecos, { endereco ->
            // Ação para um clique normal no item
            enderecoIdSelecionado = endereco.enderecoId
            Toast.makeText(requireContext(), "Endereço selecionado: ${endereco.logradouro}", Toast.LENGTH_SHORT).show()

            // Aqui você pode adicionar qualquer outra lógica que aconteça após o clique no item
            // Por exemplo, atualizar a UI ou preparar para navegar para outro fragmento
            binding.layoutEnderecos.visibility = View.GONE
            binding.layoutPreResultado.visibility = View.VISIBLE
        }, { endereco ->
            // Ação para o clique no botão editar
            viewModel.selectedEnderecoId.postValue(endereco.enderecoId)
            findNavController().navigate(R.id.editaEnderecoFragment)
        }, { endereco ->
            // Ação para o clique no botão excluir
            deleteEndereco(endereco.enderecoId)
            findNavController().navigateUp()
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




    private fun saveConsulta() {
        val valorKwh = binding.editTextKWH.text.toString()
        val pessoaId = SessionManager.pessoaId

        if (valorKwh.isEmpty() || bandeiraSelecionada.isNullOrEmpty() || enderecoIdSelecionado.isNullOrEmpty() || pessoaId.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Preencha todos os campos obrigatórios.", Toast.LENGTH_SHORT).show()
            return
        }

        val consultaRequest = ConsultaRequest(
            bandeira = bandeiraSelecionada!!,
            valorKwh = valorKwh.toDouble(),
            pessoaId = pessoaId,
            enderecoId = enderecoIdSelecionado!!
        )

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    Api.buildServiceConsulta().gerarConsulta(consultaRequest).execute()
                }

                if (response.isSuccessful) {
                    // Aqui, atualize o LiveData com o corpo da resposta
                    viewModel.resultadoConsulta.postValue(response.body())
                    Toast.makeText(requireContext(), "Consulta gerada com sucesso!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.resultadoConsultaFragment)
                } else {
                    Toast.makeText(requireContext(), "Erro ao criar consulta: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Erro: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}