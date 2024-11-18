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
        currentHexSpace?.hivePiece = null
        currentHexSpace = destination
        destination.hivePiece = this
    }

    fun place(destination: HexSpace) {
        currentHexSpace = destination
        destination.hivePiece = this
        isPlayed = true
    }

    abstract fun canMove(destination: HexSpace): Boolean

    open fun canPlace(destination: HexSpace): Boolean {
        // if there is already a piece in that space, return false
        if (destination.hivePiece != null) {
            return false
        }
        val touchingPieces = destination.getTouchingPieces()
        // if there is a piece of the opposite color touching the destination, return false
        if (touchingPieces.any { it.getColor() != this.color }) {
            return false
        }
        // if there is a piece of the same color touching the destination, return true
        return touchingPieces.isNotEmpty()
    }
}