package com.example.mavidenergyapp

import com.example.mavidenergyapp.domains.Pessoa
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

const val URL = "http://10.0.2.2:8080"

interface CriarPessoaComUsuario {
    @POST("/pessoa")
    fun criarPessoaComUsuario(@Body pessoa: Pessoa): Call<Pessoa>
}

object Api {
    fun buildServicePessoa(): CriarPessoaComUsuario {
        val retrofit = Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(CriarPessoaComUsuario::class.java)
    }
}