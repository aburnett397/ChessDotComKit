package com.ceci.chess

import scala.collection.JavaConverters._
import scalafx.Includes._
import scalafx.scene.control.TableColumn._
import scalafx.scene.control.TableSelectionModel._
import scalafx.scene.control.TableView
import scalafx.beans.property.ObjectProperty
import scalafx.scene.layout.VBox
import scalafx.scene.layout.HBox
import scalafx.beans.property.BufferProperty
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.TableColumn
import scalafx.beans.property.StringProperty
import scalafx.scene.text.Font
import com.ceci.chess.lib.move.MoveList
import scalafx.scene.control.Button
import scalafx.beans.property.IntegerProperty
import javafx.collections.ListChangeListener
import scalafx.scene.control.TableCell
import scalafx.scene.control.TablePosition
import scalafx.scene.control.TabPane
import scalafx.scene.control.Tab
import scalafx.beans.property.DoubleProperty

object GamePane extends HBox {
  
  case class Move(val wi: Integer, val wm: String, val bi: Integer, val bm: String)
  
  val moves = new ObservableBuffer[Move]
  
  AppState.move.addListener((source, oldValue, newValue) => {
    if (newValue.intValue < 0)
      AppState.board.reset
    else AppState.board.moveTo(AppState.moveList.value, newValue.intValue)
  })
  def setMove(index: Int) = {
    AppState.move.value = math.max(-1, math.min((if (AppState.moveList.value != null) AppState.moveList.value.size else 0)-1, index))
  }
  def lastMove = if (AppState.moveList.value != null) AppState.moveList.value.size-1 else -1
  
  AppState.moveList.addListener((source, oldValue, newMoves) => {
    moves.clear
    if (newMoves != null)
      moves ++= (for {
        i <- 0 to newMoves.size-1
        if (i%2 == 0)
      } yield Move(i, newMoves.get(i).getSan, if (i < newMoves.size-1) i+1 else -1, if (i < newMoves.size-1) newMoves.get(i+1).getSan else ""))
  })
  
  val moveTable = new TableView(moves) {
    columnResizePolicy = TableView.ConstrainedResizePolicy
    columns ++= List(
        new TableColumn[Move, String]("White") {
          cellFactory = column => {
            val cell = new TableCell[Move, String]
            cell.text <== cell.item
            cell.onMouseClicked.value = event => setMove(2*cell.tableRow.value.indexProperty.value)
            cell
          }
          cellValueFactory = data => new StringProperty(data.value.wm)},
        new TableColumn[Move, String]("Black") {
          cellFactory = column => {
            val cell = new TableCell[Move, String]
            cell.text <== cell.item
            cell.onMouseClicked.value = event => setMove(2*cell.tableRow.value.indexProperty.value+1)
            cell
          }
          cellValueFactory = {data => new StringProperty(data.value.bm)}}
    )
    selectionModel.value.cellSelectionEnabled.value = true
    AppState.move.addListener((source, oldValue, newValue) => {
      if (newValue.intValue >= 0)
        selectionModel.value.select(newValue.intValue/2, columns.get(newValue.intValue()%2))
    })
  }
  val board = new PlayableBoardPane(AppState.board)
  val kibitzer = KibitzerPane(Kibitzer(AppState.board, 3))
  val analysis = new AnalysisPane
  val nav = new HBox {
    style = "-fx-spacing: 1em; -fx-alignment: center"
    children = Seq(
        new Button("<<") {
          onAction = _ => setMove(-1)
        },
        new Button("<") {
          onAction = _ => setMove(AppState.move.value-1)
        },
        new Button(">") {
          onAction = _ => setMove(AppState.move.value+1)
        },
        new Button(">>") {
          onAction = _ => setMove(lastMove)
        }
    )
  }
  val tools = new TabPane {
    tabs = Seq(
      new Tab() {
        text = "Kibitzer"
        content = kibitzer
        closable.value = false
      },
      new Tab() {
        text = "Analysis"
        content = analysis
        closable.value = false
      }
    )
  }
  
  children = Seq(
      new VBox {
        children = Seq(
            board,
            tools
        )
      },
      new VBox {
        children = Seq(
            moveTable,
            nav
        )
      }
  )
  
  minWidth <== moveTable.prefWidth
  minHeight <== tools.prefHeight
  board.width <== min(width-moveTable.width, height-13.5*Font.default.size)
  kibitzer.maxWidth <== board.width
  kibitzer.prefWidth <== board.width
  moveTable.minWidth = Font.default.size*20
  moveTable.prefWidth = Font.default.size*20
  moveTable.prefHeight <== board.height+kibitzer.height-nav.prefHeight
}