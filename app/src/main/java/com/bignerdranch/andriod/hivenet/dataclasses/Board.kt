package com.bignerdranch.andriod.hivenet.dataclasses

data class Board(
    val width: Int = 11,
    val height: Int = 11,
    val hexSpaces: Array<Array<HexSpace?>> = Array(width) { x -> Array(height) { y -> null } },
    val whitePlayer: Player,
    val blackPlayer: Player
) {
    init {

    }

    override fun equals(other: Any?): Boolean {
        return true
    }

    override fun hashCode(): Int {
        var result = width
        result = 31 * result + height
        result = 31 * result + hexSpaces.contentDeepHashCode()
        return result
    }
}