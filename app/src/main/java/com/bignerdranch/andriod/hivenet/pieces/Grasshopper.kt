package com.bignerdranch.andriod.hivenet.pieces
import com.bignerdranch.andriod.hivenet.dataclasses.HexSpace

class Grasshopper(color: Boolean) : HivePiece(color, "Grasshopper") {
    override fun getAvailableMoves(): Array<HexSpace> {
        // Implement logic for Grasshopper's available moves
        return arrayOf()
    }

    override fun move(destination: HexSpace) {
        // Implement logic for moving the Grasshopper
    }

}