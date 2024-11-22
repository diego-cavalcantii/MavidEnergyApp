package com.example.mavidenergyapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mavidenergyapp.R
import com.example.mavidenergyapp.domains.ConsultaResponse

class ConsultaAdapter(
    private val consultas:List<ConsultaResponse>,
    private val onItemClick: (ConsultaResponse) -> Unit
):RecyclerView.Adapter<ConsultaAdapter.ConsultaViewHolder>() {

    class ConsultaViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val bandeira: TextView = itemView.findViewById(R.id.textViewBandeira)
        val valorKwh: TextView = itemView.findViewById(R.id.textViewValorKwh)
        val dataCriacao: TextView = itemView.findViewById(R.id.textViewDataCriacao)
        val economiaPotencial: TextView = itemView.findViewById(R.id.textViewEconomiaPotencial)
        val valorSemDesconto: TextView = itemView.findViewById(R.id.textViewValorSemDesconto)
        val valorComDesconto: TextView = itemView.findViewById(R.id.textViewValorComDesconto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_consulta, parent, false)
        return ConsultaViewHolder(view)
    }



    override fun onBindViewHolder(holder: ConsultaViewHolder, position: Int) {
        val consulta = consultas[position]
        holder.bandeira.text = consulta.bandeira
        holder.valorKwh.text = "R$ ${consulta.valorKwh}"
        holder.dataCriacao.text = consulta.dataCriacao
        holder.economiaPotencial.text = "Economia potencial: R$ ${consulta.economiaPotencial}"
        holder.valorSemDesconto.text = "Valor sem desconto: R$ ${consulta.valorSemDesconto}"
        holder.valorComDesconto.text = "Valor com desconto: R$ ${consulta.valorComDesconto}"

        holder.itemView.setOnClickListener {
            onItemClick(consulta)
        }
    }

    override fun getItemCount(): Int = consultas.size


}