package com.mduranx64.kotlingames

import androidx.compose.runtime.*
import kotlin.math.abs

class Board {
    var whiteCapture by mutableStateOf(listOf<Piece>())
        private set
    var blackCapture by mutableStateOf(listOf<Piece>())
        private set
    var currentTurn by mutableStateOf(PieceColor.WHITE)
    private var inPassingPiece: Piece? = null

    var selectedPosition by mutableStateOf<Position?>(null)
        private set
    var promotedPosition by mutableStateOf<Position?>(null)
        private set
    var isBlackKingCaptured by mutableStateOf(false)
        private set
    var isWhiteKingCaptured by mutableStateOf(false)
        private set
    var isPiecePromoted by mutableStateOf(false)
        private set

    var pieces: Array<Array<Piece?>> by mutableStateOf(
        arrayOf(
            arrayOf(Piece(PieceType.ROOK, PieceColor.BLACK), Piece(PieceType.KNIGHT, PieceColor.BLACK), Piece(PieceType.BISHOP, PieceColor.BLACK), Piece(PieceType.QUEEN, PieceColor.BLACK),
                Piece(PieceType.KING, PieceColor.BLACK), Piece(PieceType.BISHOP, PieceColor.BLACK), Piece(PieceType.KNIGHT, PieceColor.BLACK), Piece(PieceType.ROOK, PieceColor.BLACK)),
            arrayOf(Piece(PieceType.PAWN, PieceColor.BLACK), Piece(PieceType.PAWN, PieceColor.BLACK), Piece(PieceType.PAWN, PieceColor.BLACK), Piece(PieceType.PAWN, PieceColor.BLACK),
                Piece(PieceType.PAWN, PieceColor.BLACK), Piece(PieceType.PAWN, PieceColor.BLACK), Piece(PieceType.PAWN, PieceColor.BLACK), Piece(PieceType.PAWN, PieceColor.BLACK)),
            arrayOfNulls<Piece?>(8),
            arrayOfNulls<Piece?>(8),
            arrayOfNulls<Piece?>(8),
            arrayOfNulls<Piece?>(8),
            arrayOf(Piece(PieceType.PAWN, PieceColor.WHITE), Piece(PieceType.PAWN, PieceColor.WHITE), Piece(PieceType.PAWN, PieceColor.WHITE), Piece(PieceType.PAWN, PieceColor.WHITE),
                Piece(PieceType.PAWN, PieceColor.WHITE), Piece(PieceType.PAWN, PieceColor.WHITE), Piece(PieceType.PAWN, PieceColor.WHITE), Piece(PieceType.PAWN, PieceColor.WHITE)),
            arrayOf(Piece(PieceType.ROOK, PieceColor.WHITE), Piece(PieceType.KNIGHT, PieceColor.WHITE), Piece(PieceType.BISHOP, PieceColor.WHITE), Piece(PieceType.QUEEN, PieceColor.WHITE),
                Piece(PieceType.KING, PieceColor.WHITE), Piece(PieceType.BISHOP, PieceColor.WHITE), Piece(PieceType.KNIGHT, PieceColor.WHITE), Piece(PieceType.ROOK, PieceColor.WHITE))
        )
    )
    private set

    fun isSelected(position: Position): Boolean {
        return selectedPosition == position
    }

    fun selectPiece(newPosition: Position) {
        val pieceAtPosition = getPieceAt(newPosition)
        if (selectedPosition == null && pieceAtPosition?.type != null && pieceAtPosition.color == currentTurn) {
            selectedPosition = newPosition
            return
        }

        if (selectedPosition == newPosition) {
            selectedPosition = null
            return
        }

        if (getPieceAt(newPosition)?.type == null && selectedPosition != null) {
            selectedPosition?.let { from ->
                if (movePiece(from, newPosition)) {
                    selectedPosition = null
                }
            }
            return
        }

        if (selectedPosition != null && pieceAtPosition?.type != null) {
            selectedPosition?.let { from ->
                if (getPieceAt(from)?.color == pieceAtPosition.color &&
                    getPieceAt(from)?.type == PieceType.ROOK &&
                    pieceAtPosition.type == PieceType.ROOK
                ) {
                    if (movePiece(from, newPosition)) {
                        selectedPosition = null
                    } else {
                        selectedPosition = newPosition
                    }
                } else if (getPieceAt(from)?.color == pieceAtPosition.color) {
                    selectedPosition = newPosition
                } else {
                    if (movePiece(from, newPosition)) {
                        selectedPosition = null
                    }
                }
            }
        }
    }

    fun promotePiece(type: PieceType) {
        promotedPosition?.let { position ->
            getPieceAt(position)?.let { piece ->
                piece.update(type, piece.color)
                promotedPosition = null
                isPiecePromoted = false
            }
        }
    }

    private fun changeTurn(color: PieceColor) {
        if (inPassingPiece != null && inPassingPiece?.color != color) {
            inPassingPiece = null
        }
        currentTurn = if (color == PieceColor.WHITE) PieceColor.BLACK else PieceColor.WHITE
    }

    fun getPieceAt(position: Position): Piece? {
        return pieces[position.x][position.y]
    }

    fun movePiece(from: Position, to: Position): Boolean {
        val piece = getPieceAt(from) ?: return false
        if (piece.color != currentTurn) return false

        val destination = getPieceAt(to)
        var isMoved = false

        when (piece.type) {
            PieceType.PAWN -> {
                val direction = if (piece.color == PieceColor.WHITE) 1 else -1
                val startRow = if (piece.color == PieceColor.WHITE) 6 else 1
                val endRow = if (piece.color == PieceColor.WHITE) 0 else 7

                // Pawn movement: One or two squares forward
                if (destination == null && from.y == to.y) {
                    if (from.x - to.x == direction) {
                        piece.isFirstMove = false
                        move(piece, from, to)
                        isMoved = true
                    } else if (from.x - to.x == 2 * direction && piece.isFirstMove && from.x == startRow) {
                        piece.isFirstMove = false
                        move(piece, from, to)
                        isMoved = true
                        inPassingPiece = piece
                    }
                }

                // Pawn capturing or en passant
                if (destination != null && destination.color != piece.color) {
                    val xStep = from.x - to.x
                    val yStep = from.y - to.y
                    if (abs(xStep) == 1 && abs(yStep) == 1) {
                        move(piece, from, to)
                        isMoved = true
                    }
                }

                // Pawn promotion
                if (isMoved && to.x == endRow) {
                    promotedPosition = to
                    isPiecePromoted = true
                }
            }

            PieceType.ROOK -> {
                if (canMoveInLine(from, to)) {
                    move(piece, from, to)
                    isMoved = true
                }
            }

            PieceType.KNIGHT -> {
                val xStep = abs(from.x - to.x)
                val yStep = abs(from.y - to.y)
                if (xStep == 2 && yStep == 1 || xStep == 1 && yStep == 2) {
                    move(piece, from, to)
                    isMoved = true
                }
            }

            PieceType.BISHOP -> {
                if (canMoveDiagonally(from, to)) {
                    move(piece, from, to)
                    isMoved = true
                }
            }

            PieceType.QUEEN -> {
                if (canMoveInLine(from, to) || canMoveDiagonally(from, to)) {
                    move(piece, from, to)
                    isMoved = true
                }
            }

            PieceType.KING -> {
                val xStep = abs(from.x - to.x)
                val yStep = abs(from.y - to.y)
                if ((xStep == 1 && yStep == 0) || (xStep == 0 && yStep == 1) || (xStep == 1 && yStep == 1)) {
                    move(piece, from, to)
                    isMoved = true
                }
            }
        }

        if (isMoved) {
            changeTurn(piece.color)
        }

        return isMoved
    }

    private fun canMoveInLine(from: Position, to: Position): Boolean {
        if (from.x == to.x) {
            val range = if (from.y < to.y) (from.y + 1 until to.y) else (to.y + 1 until from.y)
            for (y in range) {
                if (getPieceAt(Position(from.x, y)) != null) return false
            }
            return true
        }
        if (from.y == to.y) {
            val range = if (from.x < to.x) (from.x + 1 until to.x) else (to.x + 1 until from.x)
            for (x in range) {
                if (getPieceAt(Position(x, from.y)) != null) return false
            }
            return true
        }
        return false
    }

    private fun canMoveDiagonally(from: Position, to: Position): Boolean {
        val xStep = from.x - to.x
        val yStep = from.y - to.y
        if (abs(xStep) == abs(yStep)) {
            val xDirection = if (xStep > 0) -1 else 1
            val yDirection = if (yStep > 0) -1 else 1

            var x = from.x + xDirection
            var y = from.y + yDirection
            while (x != to.x && y != to.y) {
                if (getPieceAt(Position(x, y)) != null) return false
                x += xDirection
                y += yDirection
            }
            return true
        }
        return false
    }

    private fun move(fromPiece: Piece, from: Position, to: Position) {
        val destination = getPieceAt(to)
        if (destination == null) {

            pieces[to.x][to.y] = fromPiece
            pieces[from.x][from.y] = null

            if (fromPiece.type == PieceType.PAWN) {
                if (fromPiece.color == PieceColor.WHITE && to.x == 0) {
                    promotedPosition = to
                    isPiecePromoted = true
                } else if (fromPiece.color == PieceColor.BLACK && to.x == 7) {
                    promotedPosition = to
                    isPiecePromoted = true
                }
            }
        } else {
            if (destination.color == PieceColor.WHITE) {
                whiteCapture = whiteCapture + destination
                if (destination.type == PieceType.KING) {
                    isWhiteKingCaptured = true
                }
            } else {
                blackCapture = blackCapture + destination
                if (destination.type == PieceType.KING) {
                    isBlackKingCaptured = true
                }
            }
            pieces[to.x][to.y] = fromPiece
            pieces[from.x][from.y] = null
            if (fromPiece.type == PieceType.PAWN) {
                if (fromPiece.color == PieceColor.WHITE && to.x == 0) {
                    promotedPosition = to
                    isPiecePromoted = true
                } else if (fromPiece.color == PieceColor.BLACK && to.x == 7) {
                    promotedPosition = to
                    isPiecePromoted = true
                }
            }
        }
    }
}