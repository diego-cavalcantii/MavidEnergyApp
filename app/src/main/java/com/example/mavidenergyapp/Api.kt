package com.example.mavidenergyapp

import com.example.mavidenergyapp.domains.CidadeResponse
import com.example.mavidenergyapp.domains.DadosClimaticosResponse
import com.example.mavidenergyapp.domains.Pessoa
import com.example.mavidenergyapp.domains.PessoaResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface PessoaService {
    @POST("/pessoa")
    fun criarPessoaComUsuario(@Body pessoa: Pessoa): Call<PessoaResponse>

    @GET("/pessoa")
    fun buscarPessoas(): Call<List<PessoaResponse>>
}

interface CidadeService {
    @GET("/cidades")
    fun buscarCidades(): Call<List<CidadeResponse>>

    @GET("/cidades/{cidadeId}/dados-climaticos")
    fun getDadosClimaticos(
        @Path("cidadeId") cidadeId: String
    ): Call<DadosClimaticosResponse>
}


object Api {

    private const val URL = "http://192.168.0.25:8080"

    private val retrofit = Retrofit.Builder()
        .baseUrl(URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun buildServicePessoa(): PessoaService {
        return retrofit.create(PessoaService::class.java)
    }

    fun buildServiceCidade(): CidadeService {
        return retrofit.create(CidadeService::class.java)
    }
}