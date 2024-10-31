package com.bignerdranch.andriod.hivenet.pieces

import com.bignerdranch.andriod.hivenet.dataclasses.HexSpace

class Beetle(color: Boolean) : HivePiece(color, HivePieceType.Beetle) {
    var pieceUnderneath: HivePiece? = null

    override fun getAvailableMoves(): Array<HexSpace> {
        // Implement logic for Beetle's available moves
        if (!isPlayed) {
            return arrayOf()
        }
        return arrayOf()
    }

    override fun move(destination: HexSpace) {
        // Implement logic for moving the Beetle
        if (canMove(destination)) {
            if (pieceUnderneath != null) {
                currentHexSpace?.hivePiece = pieceUnderneath
            }
            else {
                currentHexSpace?.hivePiece = null
            }
            pieceUnderneath = destination.hivePiece // either the piece or null if the tile is empty
            destination.hivePiece = this
            currentHexSpace = destination
        }
    }

    override fun canMove(destination: HexSpace): Boolean {
        // Implement logic to check if Beetle can move to the destination
        val touchingPieces = destination.getTouchingPieces()
        return touchingPieces.any { it != null }
    }

    override fun canPlace(destination: HexSpace): Boolean {
        // Implement logic to check if Beetle can be placed at the destination
        val touchingPieces = destination.getTouchingPieces()
        return touchingPieces.any { it != null }
    }
}