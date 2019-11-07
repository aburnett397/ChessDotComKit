package com.ceci.chess

import com.ceci.chess.lib.Piece
import com.ceci.chess.lib.Side
import scalafx.beans.property.BooleanProperty
import com.ceci.chess.lib.move.Move
import com.ceci.chess.lib.Square
import com.ceci.chess.lib.move.MoveList

case class Board(val board: com.ceci.chess.lib.Board)
{
  val boardProperty = BooleanProperty(false)
  val whiteToMove = BooleanProperty(board.getSideToMove == Side.WHITE)
  
  def onBoardChanged =
  {
    boardProperty.set(!boardProperty.value)
    whiteToMove.value = board.getSideToMove == Side.WHITE
  }
    
  def pieceAt(index: Int): Piece = {
    board.getPiece(Square.squareAt(index))
  }
  
  def move(from: Int, to: Int) = {
    val move: Move = new Move(
        Square.squareAt(from), 
        Square.squareAt(to))
    if (board.doMove(move, true))
      onBoardChanged
  }
  
  def setTo(fen: String) = {
    board.loadFromFen(fen)
    onBoardChanged
  }
  
  def reset = {
    board.loadFromFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")
    onBoardChanged
  }
  
  def moveTo(moves: MoveList, index: Integer) = {
    board.loadFromFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")
    for (i <- 0 to index)
      board.doMove(moves.get(i))
    onBoardChanged
  }
}
