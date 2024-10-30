package com.bignerdranch.andriod.hivenet.dataclasses

import com.bignerdranch.andriod.hivenet.pieces.HivePiece

data class HexSpace(
    val x: Int,
    val y: Int,
    val board: Board,
    var hivePiece: HivePiece? = null
) {
    fun getTouchingPieces(): List<HivePiece?> {
        val touchingCoordinates = listOf(
            Pair(x - 1, y), Pair(x + 1, y),
            Pair(x, y - 1), Pair(x, y + 1),
            Pair(x - 1, y + 1), Pair(x + 1, y - 1)
        )
        return touchingCoordinates.map { (x, y) ->
            board.hexSpaces.getOrNull(x)?.getOrNull(y)?.hivePiece
        }
    }
}