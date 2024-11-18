package com.bignerdranch.andriod.hivenet.dataclasses

import com.bignerdranch.andriod.hivenet.dataclasses.Game
import com.bignerdranch.andriod.hivenet.pieces.HivePiece
import com.bignerdranch.andriod.hivenet.pieces.QueenBee
import com.bignerdranch.andriod.hivenet.pieces.Spider
import com.bignerdranch.andriod.hivenet.pieces.Beetle
import com.bignerdranch.andriod.hivenet.pieces.Grasshopper
import com.bignerdranch.andriod.hivenet.pieces.Ant

data class Player(
    val game: Game,
    val unplayedPieces: List<HivePiece>,
    val playedPieces: MutableList<HivePiece> = mutableListOf(),
    val isWhite: Boolean,
    var isTurn: Boolean,
    var queenBee: QueenBee = unplayedPieces[0] as QueenBee,
    var spider1: Spider = unplayedPieces[1] as Spider,
    var spider2: Spider = unplayedPieces[2] as Spider,
    var beetle1: Beetle = unplayedPieces[3] as Beetle,
    var beetle2: Beetle = unplayedPieces[4] as Beetle,
    var grasshopper1: Grasshopper = unplayedPieces[5] as Grasshopper,
    var grasshopper2: Grasshopper = unplayedPieces[6] as Grasshopper,
    var grasshopper3: Grasshopper = unplayedPieces[7] as Grasshopper,
    var ant1: Ant = unplayedPieces[8] as Ant,
    var ant2: Ant = unplayedPieces[9] as Ant,
)