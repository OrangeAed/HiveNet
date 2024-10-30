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
        // Implement logic to check if Grasshopper can move to the destination
        val touchingPieces = destination.getTouchingPieces()
        return touchingPieces.any { it != null }
    }

    override fun canPlace(destination: HexSpace): Boolean {
        // Implement logic to check if Grasshopper can be placed at the destination
        val touchingPieces = destination.getTouchingPieces()
        return touchingPieces.any { it != null }
    }
}