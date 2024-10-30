package com.bignerdranch.andriod.hivenet

data class HivePiece(
    val color: Boolean, // 0 for white, 1 for black
    val type: String,
    var isPlayed: Boolean = false,
    var x: Int? = null,
    var y: Int? = null
)