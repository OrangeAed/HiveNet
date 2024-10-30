package com.bignerdranch.andriod.hivenet.pieces

import com.bignerdranch.andriod.hivenet.dataclasses.HexSpace

abstract class HivePiece(
    private val color: Boolean, // 0 for white, 1 for black
    private val type: String,
    var isPlayed: Boolean = false,
    var x: Int? = null,
    var y: Int? = null
) {
    fun getColor(): Boolean {
        return color
    }

    fun getType(): String {
        return type
    }

    abstract fun getAvailableMoves(): Array<HexSpace>

    abstract fun move(destination: HexSpace)

    fun place(destination: HexSpace) {
        destination.hivePiece = this
        x = destination.x
        y = destination.y
        isPlayed = true
    }
}