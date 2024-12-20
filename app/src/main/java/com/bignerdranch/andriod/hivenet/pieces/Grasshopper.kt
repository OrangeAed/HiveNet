package com.bignerdranch.andriod.hivenet.pieces

import com.bignerdranch.andriod.hivenet.dataclasses.HexSpace

class Grasshopper(color: Boolean) : HivePiece(color, HivePieceType.Grasshopper) {
    override fun getAvailableMoves(): Array<HexSpace> {
        // Implement logic for Grasshopper's available moves
        if (!isPlayed) {
            return arrayOf()
        }
        return arrayOf()
    }

    override fun canMove(destination: HexSpace): Boolean {
        return true
    }

    override fun canPlace(destination: HexSpace): Boolean {
        return true
    }
}