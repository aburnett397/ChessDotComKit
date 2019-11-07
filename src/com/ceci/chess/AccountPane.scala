package com.ceci.chess

import scala.collection.JavaConverters._
import scalafx.Includes._
import scalafx.scene.layout.HBox
import scalafx.scene.layout.VBox
import scalafx.scene.control.ListView
import scalafx.scene.text.Font
import scalafx.scene.control.TextArea
import scalafx.scene.control.Label
import scalafx.scene.control.TextField
import scalafx.scene.control.Button
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import scalafx.beans.property.IntegerProperty
import scalafx.beans.property.ObjectProperty
import scalafx.scene.text.Text
import scalafx.beans.property.StringProperty
import scalafx.scene.layout.StackPane
import scalafx.scene.canvas.Canvas
import scalafx.scene.paint.Color
import scala.util.Try

object AccountPane extends HBox {
  
  val currentFetch = new ObjectProperty[Fetch]
  currentFetch.value = null
  val progress = new IntegerProperty
  val fetchName = new StringProperty
  
  val account = new TextField {
    prefWidth = Font.default.size*10
    onAction = event => fetch 
    disable <== currentFetch.isNotNull()
  }
  
  val archiveList = new ListView(LocalStorage.archives) {
    prefWidth = Font.default.size*20
    prefHeight = Font.default.size*20
    selectionModel.value.selectedItem.addListener((source, oldValue, newValue) => {
      if (newValue != null)
        account.text = newValue
    })
  }
  
  case class Fetch(val name: String) {
    progress.value = 0
    fetchName.value = s"Fetching $name..."
    val future: Future[GameArchive] = PubApi.fetchArchive(name, progress)
  }
  
  def fetch: Unit = if (currentFetch.value == null && account.text.value.length() > 0) {
    currentFetch.value = Fetch(account.text.value)
    currentFetch.value.future.onComplete((archive: Try[GameArchive]) => {
      if (archive.isFailure) archive.failed.get.printStackTrace
      currentFetch.set(null)})
  }
  
  object progressCanvas extends Canvas {
    width <== account.width
    height <== account.height
    mouseTransparent.value = true
    currentFetch.addListener((source, oldValue, NewValue) => draw)
    progress.addListener((source, oldValue, NewValue) => draw)
    def draw = {
      val g = graphicsContext2D
      g.clearRect(0, 0, width.value, height.value)
      if (currentFetch.value != null) {
        g.fill = Color.Red.opacity(.5)
        g.fillRect(0, 0, progress.value*width.value/100, height.value)
      }
    }
  }
  
  children = Seq(
      new VBox {
        style = "-fx-spacing: 1em; -fx-alignment: top-left"
        children = Seq(
              new HBox {
                style = "-fx-spacing: 1em; -fx-alignment: center"
                children = Seq(
                  archiveList,
                  new VBox {
                    style = "-fx-spacing: 1em; -fx-alignment: top-left"
                    children = Seq(
                        new Button("Set games") {
                          disable <== archiveList.selectionModel.value.selectedItem.isNull()
                          onAction = _ => {
                            AppState.clearGames
                            AppState.addGames(LocalStorage.read(archiveList.selectionModel.value.selectedItem.value))
                            Main.tabs.selectionModel.value.select(1)
                          }
                        },
                        new Button("Add to games") {
                          disable <== archiveList.selectionModel.value.selectedItem.isNull()
                          onAction = _ => AppState.addGames(LocalStorage.read(archiveList.selectionModel.value.selectedItem.value))
                        })
                  })
                },
              new HBox {
                style = "-fx-spacing: 1em; -fx-alignment: center-left"
                children = Seq(
                    new Label("Account"),
                    new StackPane {
                      children = Seq(
                          account,
                          progressCanvas
                      )
                    },
                    new Button("Fetch") {
                      disable <== currentFetch.isNotNull()
                      onAction = event => fetch
                    })
              }
            )
      })
}