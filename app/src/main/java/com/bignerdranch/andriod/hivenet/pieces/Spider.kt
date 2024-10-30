package com.bignerdranch.andriod.hivenet.pieces

import com.bignerdranch.andriod.hivenet.dataclasses.HexSpace

class Spider(color: Boolean) : HivePiece(color, HivePieceType.Spider) {
    override fun getAvailableMoves(): Array<HexSpace> {
        // Implement logic for Spider's available moves
        if (!isPlayed) {
            return arrayOf()
        }
        return arrayOf()
    }

    override fun canMove(destination: HexSpace): Boolean {
        // Implement logic to check if Spider can move to the destination
        val touchingPieces = destination.getTouchingPieces()
        return touchingPieces.count { it != null } == 2
    }

    override fun canPlace(destination: HexSpace): Boolean {
        // Implement logic to check if Spider can be placed at the destination
        val touchingPieces = destination.getTouchingPieces()
        return touchingPieces.count { it != null } == 2
    }
}