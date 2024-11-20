package com.example.mavidenergyapp.domains

data class DadosClimaticosResponse(
    val nomeCidade: String,
    val nomeEstado: String,
    val clima: String,
    val temperatura: String,
    val sensacaoTermica: String,
    val umidade: String,
    val velocidadeVento: String,
    val direcaoVento: String,
    val coberturaNuvens: String,
    val nascerDoSol: String,
    val porDoSol: String
)

