package com.example.unidad8practica1.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.unidad8practica1.Comunidad
import com.example.unidad8practica1.R

class ComunidadAdapter(val comuniLista:List<Comunidad>, private val onClickListener: (Comunidad) -> Unit):
    RecyclerView.Adapter<ComunidadViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComunidadViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.item_bandera, parent, false)
        return ComunidadViewHolder(itemView)
    }

    override fun getItemCount():Int{
        return comuniLista.size
    }

    override fun onBindViewHolder(holder: ComunidadViewHolder, position:Int){
        val item=comuniLista[position]
        holder.render(item, onClickListener)
    }
}