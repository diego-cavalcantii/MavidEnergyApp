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
import com.example.mavidenergyapp.SharedViewModel
import com.example.mavidenergyapp.adapters.FornecedorAdapter
import com.example.mavidenergyapp.databinding.FragmentFornecedoresBinding
import com.example.mavidenergyapp.domains.FornecedorResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FornecedoresFragment : Fragment() {

    private var _binding: FragmentFornecedoresBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFornecedoresBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }

        sharedViewModel.resultadoConsulta.observe(viewLifecycleOwner) { resultado ->
            if (resultado != null) {
                loadFornecedores(resultado.endereco.latitude, resultado.endereco.longitude)
            }
        }
    }

    private fun loadFornecedores(latitude: Double, longitude: Double) {

        val fornecedorService = Api.buildServiceFornecedor()

        lifecycleScope.launch {
            try {
                // Chamada ao endpoint
                val response = withContext(Dispatchers.IO) {
                    fornecedorService.buscarFornecedoresProximos(latitude, longitude).execute()
                }

                if (response.isSuccessful) {
                    val fornecedores = response.body()
                    if (fornecedores != null) {
                        displayEnderecos(fornecedores)
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

    private fun displayEnderecos(fornecedores: List<FornecedorResponse>) {
        val recyclerView = binding.recyclerViewFornecedores

        // Configurar o RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Passa uma função vazia para onItemClick, pois esse fragment apenas exibe os endereços
        recyclerView.adapter = FornecedorAdapter(fornecedores)
    }



}