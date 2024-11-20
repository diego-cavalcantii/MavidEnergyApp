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
import com.example.mavidenergyapp.databinding.FragmentCidadeCuriositieBinding
import com.example.mavidenergyapp.domains.DadosClimaticosResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CidadeCuriositieFragment : Fragment() {

    private var _binding: FragmentCidadeCuriositieBinding? = null
    private val binding get() = _binding!!

    private var cidadeId: String? = null // Variável para armazenar o cidadeId recebido

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Receber o cidadeId do Bundle
        cidadeId = arguments?.getString("cidadeId")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCidadeCuriositieBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menu = Menu(binding.root, findNavController())
        menu.setupMenu()

        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }

        if (cidadeId != null) {
            loadDadosClimaticos(cidadeId!!)
        } else {
            Toast.makeText(requireContext(), "Erro: cidadeId não recebido.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadDadosClimaticos(cidadeId: String) {
        val cidadeService = Api.buildServiceCidade()

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    cidadeService.getDadosClimaticos(cidadeId).execute()
                }

                if (response.isSuccessful) {
                    val dadosClimaticos = response.body()
                    if (dadosClimaticos != null) {
                        displayDadosClimaticos(dadosClimaticos)
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

    private fun displayDadosClimaticos(dados: DadosClimaticosResponse) {
        binding.textViewCidade.text = "Cidade: ${dados.nomeCidade}, ${dados.nomeEstado}"
        binding.textViewClima.text = "Clima: ${dados.clima}"
        binding.textViewTemperatura.text = "Temperatura: ${dados.temperatura}"
        binding.textViewSensacao.text = "Sensação Térmica: ${dados.sensacaoTermica}"
        binding.textViewUmidade.text = "Umidade: ${dados.umidade}"
        binding.textViewVentoVelocidade.text = "Velocidade do Vento: ${dados.velocidadeVento}"
        binding.textViewVentoDirecao.text = "Direção do Vento: ${dados.direcaoVento}"
        binding.textViewCoberturaNuvens.text = "Cobertura de Nuvens: ${dados.coberturaNuvens}"
        binding.textViewNascerSol.text = "Nascer do Sol: ${dados.nascerDoSol}"
        binding.textViewPorSol.text = "Pôr do Sol: ${dados.porDoSol}"
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
