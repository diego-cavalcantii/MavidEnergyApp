package com.example.mavidenergyapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mavidenergyapp.R
import com.example.mavidenergyapp.domains.FornecedorResponse

class FornecedorAdapter(private val fornecedores: List<FornecedorResponse>) :
    RecyclerView.Adapter<FornecedorAdapter.FornecedorViewHolder>() {

    class FornecedorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewNome: TextView = view.findViewById(R.id.textViewNomeFornecedor)
        val textViewCNPJ: TextView = view.findViewById(R.id.textViewCNPJ)
        val textViewTelefone: TextView = view.findViewById(R.id.textViewTelefone)
        val textViewEmail: TextView = view.findViewById(R.id.textViewEmail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FornecedorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_fornecedor, parent, false)
        return FornecedorViewHolder(view)
    }

    override fun onBindViewHolder(holder: FornecedorViewHolder, position: Int) {
        val fornecedor = fornecedores[position]
        holder.textViewNome.text = fornecedor.nomeFornecedor
        holder.textViewCNPJ.text = fornecedor.cnpj
        holder.textViewTelefone.text = fornecedor.telefone
        holder.textViewEmail.text = fornecedor.email
    }

    override fun getItemCount() = fornecedores.size
}
