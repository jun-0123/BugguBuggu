package com.example.buggubuggu.widget

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.buggubuggu.R
import com.example.buggubuggu.model.ApiResponse
import com.example.buggubuggu.model.Item
import com.example.buggubuggu.model.Items

class ItemsAdapter:RecyclerView.Adapter<ItemsAdapter.ItemsViewHolder>() {
    private var listener:OnItemSelected? = null
    private var data:List<Item> = listOf()

    fun updateData(data: ApiResponse){
        Log.d("ItemsAdapter", "Data updated size: ${data.body.items.item} items")
        this.data = data.body.items.item
        notifyItemRangeChanged(0, data.body.items.item.size)
    }

    fun addListener(listener:OnItemSelected){
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_item, parent, false)
        return ItemsViewHolder(view)
    }

    override fun getItemCount(): Int = data.size
    override fun onBindViewHolder(holder: ItemsViewHolder, position: Int) {
        holder.bind(data[position])
    }

    fun interface OnItemSelected {
        fun onItemSelected(item: Item)
    }

    inner class ItemsViewHolder(v: View): RecyclerView.ViewHolder(v){
        private val textViewName: TextView = v.findViewById(R.id.textViewName)
        private val textViewSector: TextView = v.findViewById(R.id.textViewSectors)
        private val textViewDate: TextView = v.findViewById(R.id.textViewDate)
        private val textViewAddress: TextView = v.findViewById(R.id.textViewAddress)
        private lateinit var item: Item
        init {
            v.setOnClickListener{
                listener?.onItemSelected(item)
            }
        }
        fun bind(item: Item){
            this.item = item
            textViewName.text = item.BSSH_NM
            textViewSector.text = item.INDUTY
            textViewDate.text = item.APNTDT
            textViewAddress.text = item.LOCPLC
        }

    }
}