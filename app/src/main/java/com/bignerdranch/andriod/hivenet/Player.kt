package com.bignerdranch.andriod.hivenet

data class Player(
    val board: Board,
    val unplayedPieces: List<HivePiece>,
    val isWhite: Boolean,
    var isTurn: Boolean
)