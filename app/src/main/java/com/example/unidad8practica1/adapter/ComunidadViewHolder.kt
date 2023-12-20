package com.example.unidad8practica1.adapter

import android.view.ContextMenu
import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.unidad8practica1.Comunidad
import com.example.unidad8practica1.R
import com.example.unidad8practica1.databinding.ItemBanderaBinding


class ComunidadViewHolder(view: View): ViewHolder(view), View.OnCreateContextMenuListener {
    val binding= ItemBanderaBinding.bind(view)

    private lateinit var comunidad: Comunidad


    fun render(item: Comunidad, OnClickListener: (Comunidad)->Unit){
        comunidad = item
        binding.tvnombre.text=item.nombre
        binding.ivbandera.setImageResource(item.imagen)

        itemView.setOnClickListener{
            OnClickListener(item)
        }
        itemView.setOnCreateContextMenuListener(this)
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {

        menu?.setHeaderTitle(comunidad.nombre)
        menu?.add(this.adapterPosition, R.id.context_edit,1, "Editar")
        menu?.add(this.adapterPosition, R.id.context_delete,0,"Eliminar")
    }
}