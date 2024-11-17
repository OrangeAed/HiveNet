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

    fun isSurrounded(): Boolean {
        // Implement logic to check if Queen Bee is surrounded
        if (!isPlayed) {
            return false
        }
        // If an adjacent space is not null and there is not a piece there, return false
        if (currentHexSpace?.top != null && currentHexSpace?.top?.hivePiece != null) return false
        if (currentHexSpace?.bottom != null && currentHexSpace?.bottom?.hivePiece != null) return false
        if (currentHexSpace?.topLeft != null && currentHexSpace?.topLeft?.hivePiece != null) return false
        if (currentHexSpace?.topRight != null && currentHexSpace?.topRight?.hivePiece != null) return false
        if (currentHexSpace?.bottomLeft != null && currentHexSpace?.bottomLeft?.hivePiece != null) return false
        if (currentHexSpace?.bottomRight != null && currentHexSpace?.bottomRight?.hivePiece != null) return false

        return true
    }
}