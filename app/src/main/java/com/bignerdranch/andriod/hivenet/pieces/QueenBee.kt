package com.bignerdranch.andriod.hivenet.pieces
import com.bignerdranch.andriod.hivenet.dataclasses.HexSpace

class QueenBee(color: Boolean) : HivePiece(color, "QueenBee") {
    override fun getAvailableMoves(): Array<HexSpace> {
        // Implement logic for Queen Bee's available moves
        return arrayOf()
    }

    override fun move(destination: HexSpace) {
        // Implement logic for moving the Queen Bee
    }

}