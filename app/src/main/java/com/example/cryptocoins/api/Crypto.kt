package com.example.cryptocoins.api

import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.DiffUtil
import com.example.cryptocoins.R
import com.google.gson.annotations.SerializedName

data class Crypto(
    val name: String,
    val symbol: String,
    @SerializedName("is_new") val isNew: Boolean,
    @SerializedName("is_active") val isActive: Boolean,
    val type: String
)

@DrawableRes
fun Crypto.getIcon(): Int {
    return when {
        this.type == "coin" && this.isActive -> R.drawable.ic_coin_active
        this.type == "coin" && !this.isActive -> R.drawable.ic_coin_inactive
        this.type == "token" -> R.drawable.ic_token
        else -> 0 //todo placeholder image
    }
}

object DiffCallback : DiffUtil.ItemCallback<Crypto>() {
    override fun areItemsTheSame(oldItem: Crypto, newItem: Crypto) =
        oldItem.symbol == newItem.symbol

    override fun areContentsTheSame(oldItem: Crypto, newItem: Crypto) = oldItem == newItem
}

/*
*{
    "name": "Bitcoin",
    "symbol": "BTC",
    "is_new": false,
    "is_active": true,
    "type": "coin"
  }*
* */