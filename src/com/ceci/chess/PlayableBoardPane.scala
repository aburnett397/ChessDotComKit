package com.ceci.chess

import scalafx.event.EventHandler
import com.ceci.chess.lib.Piece
import com.ceci.chess.lib.Side
import scalafx.scene.Node
import scalafx.scene.input.PickResult
import scala.collection.JavaConverters._
import scalafx.Includes._

class PlayableBoardPane(override val board: Board) extends BoardPane(board)
{
  var from: Option[Int] = None
  var dragx: Double = 0
  var dragy: Double = 0
  
  override def pieceAt(i: Int): Piece = 
    if (from != null && i == from.getOrElse(-1))
      Piece.NONE
    else super.pieceAt(i)
  
  override def draw: Unit = {
    super.draw
    if (from != null) from.foreach(index => {
      val w = squareWidth.value
      val h = squareWidth.value
      val pw = BoardPaneConstants.piecew
      val ph = BoardPaneConstants.pieceh
      val g = graphicsContext2D
      
      val piece = super.pieceAt(index)
      val pieceIndex = BoardPaneConstants.piecesOrder(piece.getPieceType.ordinal())
      g.drawImage(BoardPaneConstants.pieces, 
          pieceIndex*pw, (if (piece.getPieceSide() == Side.WHITE) 0 else 1)*ph, pw, ph, 
          dragx-w/2, dragy-w/2, w, h)
    })
  }
  
  onMousePressed = (event => {
    from match {
      case None => 
        squareAt(event.getX, event.getY).foreach(square => 
          if (board.pieceAt(square) != Piece.NONE) {
            from = Some(square)
            dragx = event.x
            dragy = event.y
            draw
          })
      case _ =>
    }
  })
  
  onMouseDragged = (event => {
    from.foreach(index => {
      dragx = event.x
      dragy = event.y
      draw
    })
  })
  
  onMouseReleased = (event => {
    from.foreach(s1 => {
        from = None
        squareAt(event.getX, event.getY) match {
          case Some(s2) => board.move(s1, s2)
          case _ => 
        }
        draw
    })
  })
  
  Kibitzer(board, 1)
}
