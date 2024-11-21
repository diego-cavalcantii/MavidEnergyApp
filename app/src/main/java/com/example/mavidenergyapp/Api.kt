package com.example.mavidenergyapp

import com.example.mavidenergyapp.domains.CepResponse
import com.example.mavidenergyapp.domains.CidadeResponse
import com.example.mavidenergyapp.domains.DadosClimaticosResponse
import com.example.mavidenergyapp.domains.EnderecoRequest
import com.example.mavidenergyapp.domains.EnderecoResponse
import com.example.mavidenergyapp.domains.Pessoa
import com.example.mavidenergyapp.domains.PessoaResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.Response


interface PessoaService {
    @POST("/pessoa")
    fun criarPessoaComUsuario(@Body pessoa: Pessoa): Call<PessoaResponse>

    @GET("/pessoa")
    fun buscarPessoas(): Call<List<PessoaResponse>>

    @GET("/pessoa/{pessoaId}")
    fun buscarPessoaPorId(@Path("pessoaId") pessoaId: String): Call<PessoaResponse>
}

interface CidadeService {
    @GET("/cidades")
    fun buscarCidades(): Call<List<CidadeResponse>>

    @GET("/cidades/{cidadeId}/dados-climaticos")
    fun getDadosClimaticos(
        @Path("cidadeId") cidadeId: String
    ): Call<DadosClimaticosResponse>
}

interface EnderecoService {
    @GET("/enderecos/pessoa/{pessoaId}")
    fun buscarEnderecoPorPessoa(@Path("pessoaId") pessoaId: String): Call<List<EnderecoResponse>>

    @POST("/enderecos")
    fun adicionarEndereco(@Body enderecoRequest: EnderecoRequest): Call<EnderecoResponse>


}

interface CepService {
    @GET("{cep}")
    suspend fun buscarCep(@Path("cep") cep: String): CepResponse
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

    fun buildServiceEndereco(): EnderecoService {
        return retrofit.create(EnderecoService::class.java)
    }

    fun buildServiceCep(): CepService {
        return retrofit.create(CepService::class.java)
    }
}