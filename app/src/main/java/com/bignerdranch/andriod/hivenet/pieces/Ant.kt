package com.bignerdranch.andriod.hivenet.pieces
import com.bignerdranch.andriod.hivenet.dataclasses.HexSpace

class Ant(color: Boolean) : HivePiece(color, HivePieceType.Ant) {
    override fun getAvailableMoves(): Array<HexSpace> {
        // Implement logic for Ant's available moves
        return arrayOf()
    }

    override fun move(destination: HexSpace) {
        // Implement logic for moving the Ant
    }

}