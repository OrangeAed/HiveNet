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
}