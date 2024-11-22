package com.example.mavidenergyapp.domains

import java.time.LocalDateTime

data class ConsultaRequest(
    val bandeira: String,
    val valorKwh: Double,
    val pessoaId: String,
    val enderecoId: String,
)

data class ConsultaResponse(
    val consultaId: String,
    val bandeira: String,
    val valorKwh: Double,
    val endereco: EnderecoResponse,
    val dataCriacao: String,
    val economiaPotencial: String,
    val valorSemDesconto: String,
    val valorComDesconto: String,
)
