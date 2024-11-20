package com.example.mavidenergyapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mavidenergyapp.Api
import com.example.mavidenergyapp.Menu
import com.example.mavidenergyapp.R
import com.example.mavidenergyapp.databinding.FragmentCuriositiesBinding
import com.example.mavidenergyapp.domains.CidadeResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CuriositiesFragment : Fragment() {

    private var _binding: FragmentCuriositiesBinding? = null
    private val binding get() = _binding!!

    private lateinit var cidades: List<CidadeResponse> // Armazena a lista de cidades
    private var cidadeSelecionada: CidadeResponse? = null // Armazena a cidade selecionada

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCuriositiesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menu = Menu(binding.root, findNavController())
        menu.setupMenu()

        // Carrega as cidades no Spinner
        loadCidades()

        // Configura o botão
        binding.buttonCidadeCuriositie.setOnClickListener {
            if (cidadeSelecionada != null) {
                val bundle = Bundle().apply {
                    putString("cidadeId", cidadeSelecionada!!.cidadeId)
                }
                findNavController().navigate(R.id.cidadeCuriositieFragment, bundle)
            } else {
                Toast.makeText(requireContext(), "Selecione uma cidade antes de continuar.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadCidades() {
        val cidadeService = Api.buildServiceCidade()

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    cidadeService.buscarCidades().execute()
                }

                if (response.isSuccessful) {
                    cidades = response.body() ?: emptyList()

                    if (cidades.isNotEmpty()) {
                        val nomesCidades = cidades.map { it.nomeCidade }

                        // Configura o Adapter do Spinner
                        val adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            nomesCidades
                        )
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.spinnerCidades.adapter = adapter

                        // Listener do Spinner para atualizar a cidade selecionada
                        binding.spinnerCidades.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                cidadeSelecionada = cidades[position]
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                cidadeSelecionada = null
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), "Nenhuma cidade encontrada.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Erro ao buscar cidades: ${response.message()}", Toast.LENGTH_SHORT).show()
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
