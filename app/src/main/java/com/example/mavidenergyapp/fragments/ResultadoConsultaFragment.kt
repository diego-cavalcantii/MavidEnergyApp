package com.example.mavidenergyapp.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController;
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mavidenergyapp.Api
import com.example.mavidenergyapp.Menu;
import com.example.mavidenergyapp.R;
import com.example.mavidenergyapp.SessionManager
import com.example.mavidenergyapp.SharedViewModel
import com.example.mavidenergyapp.adapters.EnderecoAdapter
import com.example.mavidenergyapp.adapters.FornecedorAdapter
import com.example.mavidenergyapp.databinding.FragmentResultadoConsultaBinding;
import com.example.mavidenergyapp.domains.EnderecoResponse
import com.example.mavidenergyapp.domains.FornecedorResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class ResultadoConsultaFragment : Fragment() {

    private var _binding: FragmentResultadoConsultaBinding? = null
    private val binding get() = _binding!!  // Safe property access
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private  var latitude: Double = 0.0
    private  var longitude: Double = 0.0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentResultadoConsultaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observar LiveData
        sharedViewModel.resultadoConsulta.observe(viewLifecycleOwner) { resultado ->
            if (resultado != null) {
                binding.textViewBandeira.text = "Bandeira: ${resultado.bandeira}"
                binding.textViewValorKwh.text = "Valor KWh: ${resultado.valorKwh}"
                binding.textViewDataCriacao.text = "Data da Consulta: ${resultado.dataCriacao}"
                binding.textViewEconomiaPotencial.text = "Economia Potencial: ${resultado.economiaPotencial}"
                binding.textViewValorSemDesconto.text = "Valor Sem Desconto: ${resultado.valorSemDesconto}"
                binding.textViewValorComDesconto.text = "Valor Com Desconto: ${resultado.valorComDesconto}"

            }

        }

        binding.buttonFornecedores.setOnClickListener {
            findNavController().navigate(R.id.fornecedoresFragment)
        }

        val menu = Menu(binding.root, findNavController())
        menu.setupMenu()
    }




}

