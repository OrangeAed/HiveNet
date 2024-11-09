package com.bignerdranch.andriod.hivenet.pieces

import com.bignerdranch.andriod.hivenet.dataclasses.HexSpace

class QueenBee(color: Boolean) : HivePiece(color, HivePieceType.QueenBee) {
    override fun getAvailableMoves(): Array<HexSpace> {
        // Implement logic for Queen Bee's available moves
        if (!isPlayed) {
            return arrayOf()
        }
        return arrayOf()
    }

    override fun canMove(destination: HexSpace): Boolean {
        return true
    }

    override fun canPlace(destination: HexSpace): Boolean {
        // Implement logic to check if Queen Bee can be placed at the destination
        return true
    }

    fun isSurrounded(): Boolean {
        // Implement logic to check if Queen Bee is surrounded
        if (!isPlayed) {
            return false
        }
        if (currentHexSpace?.top == null) return false
        if (currentHexSpace?.bottom == null) return false
        if (currentHexSpace?.topLeft == null) return false
        if (currentHexSpace?.topRight == null) return false
        if (currentHexSpace?.bottomLeft == null) return false
        if (currentHexSpace?.bottomRight == null) return false

        return true
    }
}