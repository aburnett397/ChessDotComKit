package com.ceci.chess

import com.ceci.chess.lib.Piece
import com.ceci.chess.lib.Side

import scalafx.scene.layout.GridPane
import scalafx.scene.canvas.Canvas
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color
import scalafx.scene.image.Image
import scalafx.scene.shape.Rectangle
import javafx.scene.text.Font
import scalafx.Includes._
import scala.collection.JavaConverters._
import scalafx.beans.property.BooleanProperty
import scalafx.scene.control.ContextMenu
import scalafx.scene.control.MenuItem
import scalafx.scene.control.MenuItem._
import scalafx.beans.property.IntegerProperty
import scalafx.beans.property.DoubleProperty
import scalafx.scene.Scene
import scalafx.scene.Scene._
import scalafx.scene.canvas.Canvas._
import javafx.beans.binding.Bindings

object BoardPaneConstants
{
  val pieces: Image = new Image(getClass.getResourceAsStream("pieces.png"))
  val piecew = pieces.width.value/6
  val pieceh = pieces.height.value/2
  val piecesOrder = Array(5, 3, 2, 4, 1, 0)
}

class BoardPane(val board: Board) extends Canvas
{
    height <== width
    val squareWidth = DoubleProperty(1)
    squareWidth <== width/8
    squareWidth.addListener((_, _, _) => draw)
    
    board.boardProperty.addListener((source, oldValue, newValue) => draw)
    val flip = BooleanProperty(false)
    flip.addListener((source, oldValue, newValue) => draw)
    
    val menu = new ContextMenu {
      items ++= Seq(
          new MenuItem("Flip") {
            onAction = _ => flip.value = !flip.value
          }
      )
    }
    onContextMenuRequested = e => menu.show(this, e.screenX, e.screenY)
    
    def squareAt(x: Double, y: Double): Option[Int] = {
      val i = (x/squareWidth.value).toInt
      val j = 7-(y/squareWidth.value).toInt
      if (i >= 0 && i < 8 && j >= 0 && j < 8)
        Some(if (flip.value) (7-j)*8+(7-i) else j*8+i)
      else None
    }
    
    def pieceAt(i: Int): Piece =
      board.pieceAt(i)
    
    def draw: Unit =
    {
        val w = squareWidth.value
        val h = squareWidth.value
        val pw = BoardPaneConstants.piecew
        val ph = BoardPaneConstants.pieceh
        val g = graphicsContext2D
        for (index <- 0 to 63) {
          val i = if (flip.value) 63-index else index
          val x0 = (i%8)*w
          val y0 = (7-i/8)*h
          
          g.fill = if ((i/8+i%8)%2 == 0) Color.SteelBlue else Color.White
          g.fillRect(x0, y0, w, h)
          
          val piece = pieceAt(index)
          if (piece != Piece.NONE)
          {
              val pieceIndex = BoardPaneConstants.piecesOrder(piece.getPieceType.ordinal())
              g.drawImage(BoardPaneConstants.pieces, 
                  pieceIndex*pw, (if (piece.getPieceSide() == Side.WHITE) 0 else 1)*ph, pw, ph, 
                  x0, y0, w, h)
          }
        }
    }
    draw
}