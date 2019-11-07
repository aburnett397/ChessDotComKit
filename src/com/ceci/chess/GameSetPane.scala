package com.ceci.chess

import scala.collection.JavaConverters._
import scalafx.Includes._
import scalafx.scene.control.TableColumn._
import scalafx.scene.layout.HBox
import scalafx.scene.layout.VBox
import scalafx.scene.text.Font
import scalafx.scene.control.TableView
import scalafx.scene.control.TableColumn
import scalafx.beans.property.StringProperty
import scalafx.scene.control.CheckBox
import scalafx.scene.control.Label
import scalafx.scene.control.TextField
import scalafx.beans.property.ObjectProperty
import scalafx.scene.control.TableCell
import scalafx.scene.text.Text
import scalafx.scene.text.TextAlignment
import scalafx.scene.control.ContentDisplay
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.ZoneId
import scalafx.scene.text.TextFlow
import scalafx.beans.binding.StringBinding
import javafx.beans.binding.ObjectBinding
import scalafx.scene.control.Button
import scalafx.scene.layout.FlowPane
import scalafx.scene.layout.Region
import scalafx.beans.property.IntegerProperty

object GameSetPane extends HBox {
  
  def playerCellFactory = (column: TableColumn[GameData, (String, Int)]) => new TableCell[GameData, (String, Int)] {
    textAlignment = TextAlignment.CENTER
    contentDisplay = ContentDisplay.BOTTOM
    item.onChange {(_, _, value: (String, Int)) => 
      text = if (value == null) "" else value._1
      style = "-fx-alignment: center"
      graphic = if (value == null) null else new Text(""+value._2) {style = "-fx-font-size: .8em; -fx-text-alignment: center"}
    }
  }
  
  def dateCellFactory = (column: TableColumn[GameData, Instant]) => new TableCell[GameData, Instant] {
    textAlignment = TextAlignment.CENTER
    contentDisplay = ContentDisplay.BOTTOM
    item.onChange {(_, _, value: Instant) => 
      text = if (value == null) "" else DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withZone(ZoneId.systemDefault()).format(value)
      style = "-fx-alignment: center"
      graphic = if (value == null) null 
        else new Text(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM).withZone(ZoneId.systemDefault()).format(value)) {
          style = "-fx-font-size: .8em; -fx-text-alignment: center"}
    }
  }
  
  val gameTable = new TableView(AppState.filteredGames) {
    prefWidth = Font.default.size*60
    prefHeight = Font.default.size*40
    columnResizePolicy = TableView.ConstrainedResizePolicy
    columns ++= List(
        new TableColumn[GameData, Instant]("Date") {
          cellFactory = dateCellFactory
          cellValueFactory = {data => new ObjectProperty[Instant] {value = java.time.Instant.ofEpochSecond(data.value.date)}}},
        new TableColumn[GameData, (String, Int)]("White") {
          cellFactory = playerCellFactory
          cellValueFactory = {data => new ObjectProperty[(String, Int)] {value = (data.value.white, data.value.whiteElo)}}},
        new TableColumn[GameData, (String, Int)]("Black") {
          cellFactory = playerCellFactory
          cellValueFactory = {data => new ObjectProperty[(String, Int)] {value = (data.value.black, data.value.blackElo)}}},
        new TableColumn[GameData, String]("Result") {
          cellValueFactory = {data => new StringProperty(data.value.outcome.name)}},
        new TableColumn[GameData, String]("Time") {
          cellValueFactory = {data => new StringProperty(data.value.timeControl)}},
        new TableColumn[GameData, Int]("Moves") {
          cellValueFactory = {data => new ObjectProperty[Int] {value = data.value.nMoves}}}
    )
    selectionModel.value.selectedItem.addListener((source, oldValue, newValue) => {
      AppState.game.value = if (newValue == null) null else newValue.asInstanceOf[GameData].game
    })
  }
  
  children = Seq(
      gameTable,
      new VBox {
        style = "-fx-spacing: 1em"
        children = Seq(
            new TextFlow {
              prefWidth = Region.USE_COMPUTED_SIZE
              children = Seq(
                  new Text("Games: "), 
                  new Label{text <== new ObjectBinding[String] {
                    bind(AppState.filteredGames)
                    override def computeValue: String = ""+AppState.filteredGames.length}},
                  new Text("   "),
                  new Button("Clear") {
                    onAction = _ => AppState.games.clear()  
                  }
              )},
            new CheckBox("Use filter") {
              AppState.useFilter <== selected
            },
            new HBox {
              style = "-fx-spacing: 1em; -fx-alignment: center-left"
              children = Seq(
                  new Text("Time"),
                  new TextField {
                    prefWidth = 5*Font.default.size
                    onAction = _ => AppState.timeControl.value = text.value
                  }
              )
            }
        )
      }
  )
}
