package com.bignerdranch.andriod.hivenet.pieces
import com.bignerdranch.andriod.hivenet.dataclasses.HexSpace

class Spider(color: Boolean) : HivePiece(color, "Spider") {
    override fun getAvailableMoves(): Array<HexSpace> {
        // Implement logic for Spider's available moves
        return arrayOf()
    }

    override fun move(destination: HexSpace) {
        // Implement logic for moving the Spider
    }
}