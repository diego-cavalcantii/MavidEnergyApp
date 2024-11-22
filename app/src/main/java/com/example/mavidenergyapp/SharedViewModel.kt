package com.example.mavidenergyapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mavidenergyapp.domains.ConsultaResponse

class SharedViewModel : ViewModel() {
    val resultadoConsulta: MutableLiveData<ConsultaResponse> = MutableLiveData()
    val selectedEnderecoId = MutableLiveData<String>()
}
