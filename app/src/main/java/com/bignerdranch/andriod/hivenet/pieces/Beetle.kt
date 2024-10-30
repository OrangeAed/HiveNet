package com.bignerdranch.andriod.hivenet.pieces
import com.bignerdranch.andriod.hivenet.dataclasses.HexSpace

class Beetle(color: Boolean) : HivePiece(color, HivePieceType.Beetle) {
    override fun getAvailableMoves(): Array<HexSpace> {
        // Implement logic for Beetle's available moves
        return arrayOf()
    }

    override fun move(destination: HexSpace) {
        // Implement logic for moving the Beetle
    }

}