package com.example.mavidenergyapp

import com.example.mavidenergyapp.domains.CepResponse
import com.example.mavidenergyapp.domains.CidadeResponse
import com.example.mavidenergyapp.domains.ConsultaRequest
import com.example.mavidenergyapp.domains.ConsultaResponse
import com.example.mavidenergyapp.domains.DadosClimaticosResponse
import com.example.mavidenergyapp.domains.EnderecoRequest
import com.example.mavidenergyapp.domains.EnderecoResponse
import com.example.mavidenergyapp.domains.FornecedorResponse
import com.example.mavidenergyapp.domains.PaginatedResponse
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
import retrofit2.http.DELETE
import retrofit2.http.PUT
import retrofit2.http.Query


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

    @GET("/enderecos/{enderecoId}")
    fun buscarEnderecoPorId(@Path("enderecoId") enderecoId: String): Call<EnderecoResponse>

    @POST("/enderecos")
    fun adicionarEndereco(@Body enderecoRequest: EnderecoRequest): Call<EnderecoResponse>

    @PUT("/enderecos/{enderecoId}")
    fun atualizarEndereco(@Path("enderecoId") enderecoId: String, @Body enderecoRequest: EnderecoRequest): Call<EnderecoResponse>

    @DELETE("/enderecos/{enderecoId}")
    fun deletarEndereco(@Path("enderecoId") enderecoId: String): Call<Void>
}

interface ConsultaService {
    @POST("/consulta")
    fun gerarConsulta(@Body consultaRequest: ConsultaRequest): Call<ConsultaResponse>

    @GET("/consulta/pessoa/{pessoaId}")
    fun buscarConsultasPorPessoa(@Path("pessoaId") pessoaId: String): Call<List<ConsultaResponse>>


}

interface CepService {
    @GET("{cep}")
    suspend fun buscarCep(@Path("cep") cep: String): CepResponse
}

interface FornecedorService {
    @GET("/fornecedores/proximos")
    fun buscarFornecedoresProximos(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
    ): Call<List<FornecedorResponse>>
}





object Api {

    private const val URL = "191.232.170.13:8080"

//    val okHttpClient = OkHttpClient.Builder()
//        .addInterceptor(HttpLoggingInterceptor().apply {
//            level = HttpLoggingInterceptor.Level.BODY
//        })
//        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(URL)
        .addConverterFactory(GsonConverterFactory.create())
//        .client(okHttpClient)
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

    fun buildServiceConsulta(): ConsultaService {
        return retrofit.create(ConsultaService::class.java)
    }

    fun buildServiceFornecedor(): FornecedorService {
        return retrofit.create(FornecedorService::class.java)
    }
}