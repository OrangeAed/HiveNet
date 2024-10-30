package com.bignerdranch.andriod.hivenet.pieces

import com.bignerdranch.andriod.hivenet.dataclasses.HexSpace

class Ant(color: Boolean) : HivePiece(color, HivePieceType.Ant) {
    override fun getAvailableMoves(): Array<HexSpace> {
        // Implement logic for Ant's available moves
        if (!isPlayed) {
            return arrayOf()
        }
        return arrayOf()
    }

    override fun canMove(destination: HexSpace): Boolean {
        // Implement logic to check if Ant can move to the destination
        val touchingPieces = destination.getTouchingPieces()
        return touchingPieces.any { it != null }
    }

    override fun canPlace(destination: HexSpace): Boolean {
        // Implement logic to check if Ant can be placed at the destination
        val touchingPieces = destination.getTouchingPieces()
        return touchingPieces.any { it != null }
    }
}