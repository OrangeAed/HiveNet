package com.bignerdranch.andriod.hivenet.pieces

import com.bignerdranch.andriod.hivenet.dataclasses.HexSpace

abstract class HivePiece(
    private val color: Boolean, // 0 for white, 1 for black
    private val type: HivePieceType,
    var isPlayed: Boolean = false,
    var currentHexSpace: HexSpace? = null
) {
    fun getColor(): Boolean {
        return color
    }

    fun getType(): HivePieceType {
        return type
    }

    abstract fun getAvailableMoves(): Array<HexSpace>

    open fun move(destination: HexSpace) {
    }

    fun place(destination: HexSpace) {
    }

    abstract fun canMove(destination: HexSpace): Boolean

    abstract fun canPlace(destination: HexSpace): Boolean
}