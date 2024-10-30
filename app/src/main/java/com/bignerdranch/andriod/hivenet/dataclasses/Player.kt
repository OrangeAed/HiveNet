package com.bignerdranch.andriod.hivenet.dataclasses

import com.bignerdranch.andriod.hivenet.pieces.HivePiece

data class Player(
    val board: Board,
    val unplayedPieces: List<HivePiece>,
    val isWhite: Boolean,
    var isTurn: Boolean
)