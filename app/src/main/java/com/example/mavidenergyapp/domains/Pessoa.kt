package com.example.mavidenergyapp.domains

data class Pessoa (
    val nome: String,
    val email: String,
    val senha: String
)

data class PessoaResponse(
    val pessoaId: String,
    val nome: String,
    val email: String,
    val enderecos: List<EnderecoResponse> = emptyList() // Caso você precise lidar com endereços no futuro
)
