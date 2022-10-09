package com.example.dogapi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class DogAdapter(val images: List<String>): RecyclerView.Adapter<DogAdapter.ViewHolder>() {

    // 1. Creamos viewHolder
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val imageView: ImageView = view.findViewById(R.id.imageItemList)

        fun bind(image: String) {
            Picasso.get().load(image).into(imageView)
        }
    }

    // Creamos la vista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_list, parent, false)
        return ViewHolder(view)
    }

    // cuales la posision de cala item con los parametros
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = images[position]
        holder.bind(item)
    }

    // cantidad de items
    override fun getItemCount(): Int {
        return images.size
    }
}