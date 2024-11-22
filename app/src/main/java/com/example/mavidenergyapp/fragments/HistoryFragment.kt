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
import com.example.mavidenergyapp.R
import com.example.mavidenergyapp.SessionManager
import com.example.mavidenergyapp.SharedViewModel
import com.example.mavidenergyapp.adapters.ConsultaAdapter
import com.example.mavidenergyapp.adapters.EnderecoAdapter
import com.example.mavidenergyapp.databinding.FragmentHistoryBinding
import com.example.mavidenergyapp.domains.ConsultaResponse
import com.example.mavidenergyapp.domains.EnderecoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SharedViewModel by activityViewModels()
    private var consultaIdSelecionada: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }

        loadConsultas()
    }

    private fun loadConsultas() {
        val pessoaId = SessionManager.pessoaId
        if (pessoaId == null) {
            Toast.makeText(requireContext(), "Erro: pessoaId não encontrado.", Toast.LENGTH_SHORT).show()
            return
        }

        val enderecoService = Api.buildServiceConsulta()

        lifecycleScope.launch {
            try {
                // Chamada ao endpoint
                val response = withContext(Dispatchers.IO) {
                    enderecoService.buscarConsultasPorPessoa(pessoaId).execute()
                }

                if (response.isSuccessful) {
                    val consultas = response.body()
                    if (!consultas.isNullOrEmpty()) {
                        displayConsultas(consultas)
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

    private fun displayConsultas(consultas: List<ConsultaResponse>) {
        val recyclerView = binding.recyclerConsultas

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = ConsultaAdapter(consultas) { consulta ->
            // Ação para um clique normal no item
            consultaIdSelecionada = consulta.consultaId
            Toast.makeText(requireContext(), "Consulta Selecionada", Toast.LENGTH_SHORT).show()

            // Aqui você pode adicionar qualquer outra lógica que aconteça após o clique no item
            // Por exemplo, atualizar a UI ou preparar para navegar para outro fragmento
            viewModel.resultadoConsulta.postValue(consulta)
            Toast.makeText(requireContext(), "Consulta Escolhida!", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.resultadoConsultaFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
