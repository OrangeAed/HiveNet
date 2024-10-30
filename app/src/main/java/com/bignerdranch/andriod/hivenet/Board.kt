package com.bignerdranch.andriod.hivenet

data class Board(
    val width: Int = 11,
    val height: Int = 11,
    val hexSpaces: Array<Array<HexSpace?>> = Array(width) { x -> Array(height) { y -> null } },
    val player1: Player,
    val player2: Player
) {
    init {
        for (x in 0 until width) {
            for (y in 0 until height) {
                hexSpaces[x][y] = HexSpace(x, y, this)
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Board

        if (width != other.width) return false
        if (height != other.height) return false
        if (!hexSpaces.contentDeepEquals(other.hexSpaces)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = width
        result = 31 * result + height
        result = 31 * result + hexSpaces.contentDeepHashCode()
        return result
    }
}