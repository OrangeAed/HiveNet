package com.bignerdranch.andriod.hivenet.dataclasses

import com.bignerdranch.andriod.hivenet.pieces.*

class Game {

    val whitePlayer: Player = Player(this, createPieces(isWhite = true), isWhite = true, isTurn = true)
    val blackPlayer: Player = Player(this, createPieces(isWhite = false), isWhite = false, isTurn = false)
    val board: Board = Board(whitePlayer = whitePlayer, blackPlayer = blackPlayer)
    var isWhitePlayerTurn: Boolean = true
    var totalPlayedPieces: Int = 0
    var turnNumber: Int = 0

    companion object {
        fun createPieces(isWhite: Boolean): List<HivePiece> {
            return listOf(
                QueenBee(isWhite),
                Spider(isWhite), Spider(isWhite),
                Beetle(isWhite), Beetle(isWhite),
                Grasshopper(isWhite), Grasshopper(isWhite), Grasshopper(isWhite),
                Ant(isWhite), Ant(isWhite), Ant(isWhite)
            )
        }
    }

    private fun switchTurns() {
        if (hasPlayerWon(whitePlayer)) {
            handlePlayerWon(whitePlayer)
        } else if (hasPlayerWon(blackPlayer)) {
            handlePlayerWon(blackPlayer)
        }
        turnNumber += 1
        isWhitePlayerTurn = !isWhitePlayerTurn
        whitePlayer.isTurn = !whitePlayer.isTurn
        blackPlayer.isTurn = !blackPlayer.isTurn
    }

    private fun hasPlayerWon(player: Player): Boolean {
        return player.queenBee.isSurrounded()
    }

    private fun updateTotalPlayedPieces() {
        totalPlayedPieces = whitePlayer.playedPieces.size + blackPlayer.playedPieces.size
    }

    private fun handlePlayerWon(player: Player) {
        // replace this with better logic later
        if (hasPlayerWon(player)) {
            println("Player has won!")
        }
    }
}