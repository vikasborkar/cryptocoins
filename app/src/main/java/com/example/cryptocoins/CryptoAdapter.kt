package com.example.cryptocoins

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptocoins.api.Crypto
import com.example.cryptocoins.api.DiffCallback
import com.example.cryptocoins.api.getIcon
import com.example.cryptocoins.databinding.ItemCryptoBinding

class CryptoAdapter : ListAdapter<Crypto, CryptoAdapter.CryptoHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CryptoHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCryptoBinding.inflate(inflater, parent, false)
        return CryptoHolder(binding)
    }

    override fun onBindViewHolder(holder: CryptoHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CryptoHolder(
        private val binding: ItemCryptoBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(crypto: Crypto) {
            binding.apply {
                tvName.text = crypto.name
                tvSymbol.text = crypto.symbol
                ivIcon.setImageResource(crypto.getIcon())
                ivNewTag.visibility = if (crypto.isNew) View.VISIBLE else View.GONE
            }
        }
    }
}