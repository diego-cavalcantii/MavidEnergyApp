package com.example.mavidenergyapp.domains

data class EnderecoResponse(
    val cep: String,
    val logradouro: String,
    val numero: String,
    val nomeCidade: String,
    val nomeEstado: String,
    val siglaEstado: String,
    val latitude: Double?,
    val longitude: Double?
)

data class EnderecoRequest(
    val cep: String,
    val logradouro: String,
    val numero: String,
    var latitude: String,
    var longitude: String,
    val cidadeId: String,
    val pessoaId: String
)

data class CepResponse(
    val cep: String,
    val address: String,
    val city: String,
    val state: String,
    val lat: String,
    val lng: String
)

