package com.example.mavidenergyapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mavidenergyapp.R
import com.example.mavidenergyapp.domains.EnderecoResponse

class EnderecoAdapter(
    private val enderecos: List<EnderecoResponse>
) : RecyclerView.Adapter<EnderecoAdapter.EnderecoViewHolder>() {

    class EnderecoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val logradouro: TextView = itemView.findViewById(R.id.textViewLogradouro)
        val numero: TextView = itemView.findViewById(R.id.textViewNumero)
        val cep: TextView = itemView.findViewById(R.id.textViewCep)
        val cidade: TextView = itemView.findViewById(R.id.textViewCidade)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EnderecoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_endereco, parent, false)
        return EnderecoViewHolder(view)
    }

    override fun onBindViewHolder(holder: EnderecoViewHolder, position: Int) {
        val endereco = enderecos[position]
        holder.logradouro.text = endereco.logradouro
        holder.numero.text = "Número: ${endereco.numero}"
        holder.cep.text = "CEP: ${endereco.cep}"
        holder.cidade.text = "${endereco.nomeCidade}, ${endereco.siglaEstado}"
    }

    override fun getItemCount(): Int = enderecos.size
}
