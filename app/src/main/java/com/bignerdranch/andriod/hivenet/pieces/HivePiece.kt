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

    fun move(destination: HexSpace) {
        currentHexSpace?.hivePiece = null
        if (canMove(destination)) {
            currentHexSpace?.hivePiece = null
            destination.hivePiece = this
            currentHexSpace = destination
        }
    }

    fun place(destination: HexSpace) {
        if (canPlace(destination)) {
            destination.hivePiece = this
            currentHexSpace = destination
            isPlayed = true
        }
    }

    abstract fun canMove(destination: HexSpace): Boolean

    abstract fun canPlace(destination: HexSpace): Boolean
}