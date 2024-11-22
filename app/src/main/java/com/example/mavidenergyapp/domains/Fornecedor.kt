package com.example.mavidenergyapp.domains

data class FornecedorResponse (
    val nomeFornecedor: String,
    val cnpj: String,
    val telefone: String,
    val email: String,
    val endereco: EnderecoResponse
)
