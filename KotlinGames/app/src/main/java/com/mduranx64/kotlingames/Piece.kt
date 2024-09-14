package com.mduranx64.kotlingames

import androidx.annotation.DrawableRes

data class Position(val x: Int, val y: Int)

enum class PieceType {
    KING, QUEEN, ROOK, KNIGHT, BISHOP, PAWN
}

enum class PieceColor {
    WHITE, BLACK
}

class Piece(var type: PieceType, var color: PieceColor) {
    var isFirstMove: Boolean = true

    fun update(type: PieceType, color: PieceColor) {
        this.type = type
        this.color = color
    }

    val symbol: String
        get() = when (color) {
            PieceColor.WHITE -> when (type) {
                PieceType.KING -> "♚"
                PieceType.QUEEN -> "♛"
                PieceType.ROOK -> "♜"
                PieceType.KNIGHT -> "♞"
                PieceType.BISHOP -> "♝"
                PieceType.PAWN -> "♟"
            }
            PieceColor.BLACK -> when (type) {
                PieceType.KING -> "♔"
                PieceType.QUEEN -> "♕"
                PieceType.ROOK -> "♖"
                PieceType.KNIGHT -> "♘"
                PieceType.BISHOP -> "♗"
                PieceType.PAWN -> "♙"
            }
        }
}

val Piece.pieceImage: Int
    @DrawableRes
    get() = when (color) {
        PieceColor.WHITE -> when (type) {
            PieceType.BISHOP -> R.drawable.w_bishop
            PieceType.KING -> R.drawable.w_king
            PieceType.KNIGHT -> R.drawable.w_knight
            PieceType.PAWN -> R.drawable.w_pawn
            PieceType.QUEEN -> R.drawable.w_queen
            PieceType.ROOK -> R.drawable.w_rook
        }
        PieceColor.BLACK -> when (type) {
            PieceType.BISHOP -> R.drawable.b_bishop
            PieceType.KING -> R.drawable.b_king
            PieceType.KNIGHT -> R.drawable.b_knight
            PieceType.PAWN -> R.drawable.b_pawn
            PieceType.QUEEN -> R.drawable.b_queen
            PieceType.ROOK -> R.drawable.b_rook
        }
    }
