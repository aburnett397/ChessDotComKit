package com.ceci.chess

import scalafx.Includes._
import scalafx.scene.layout.VBox
import scalafx.scene.layout.HBox
import scalafx.scene.text.Text
import javafx.scene.text.Font
import scalafx.scene.layout.Pane
import scalafx.scene.control.Button
import javafx.scene.paint.Paint
import javafx.beans.binding.ObjectBinding
import scalafx.scene.paint.Color
import scalafx.scene.layout.StackPane
import scalafx.scene.shape.Rectangle

case class KibitzerPane(val kibitzer: Kibitzer) extends VBox {
  
  children = List(
      new Button {
        text <== when (kibitzer.running) choose "Stop" otherwise "Start"
        onAction = _ => if (kibitzer.running.value) kibitzer.stop else kibitzer.eval
      }
    )++(0 to kibitzer.nLines-1).toList.map(i => new HBox {
    children = Seq(
        new StackPane {
          children = Seq(
            new Rectangle {
              fill <== new ObjectBinding[Paint] {
                bind(kibitzer.scores(i))
                override def computeValue = if (kibitzer.scores(i).value < 0) Color.Black else Color.White
              }
              width = 3*Font.getDefault.size
              height = 3*Font.getDefault.size
            },
            new Text {
              style="-fx-font-weight: bold"
              disable <== !kibitzer.running
              text <== kibitzer.scores(i).asString("%.2f")
              fill <== new ObjectBinding[Paint] {
                bind(kibitzer.scores(i))
                override def computeValue = if (kibitzer.scores(i).value < 0) Color.White else Color.Black
              }
              relocate(0, .4*Font.getDefault.size)
            }
          )
        },
        new Pane {
          prefHeight = 3*Font.getDefault.size
          children = Seq(
            new Text {
              style = "-fx-text-alignment: left"
              disable <== !kibitzer.running
              text <== kibitzer.lines(i)
              prefWidth <== KibitzerPane.this.prefWidth-3*Font.getDefault.size
              relocate(0, .4*Font.getDefault.size)
            }
          )
        }
    )
    
  })
}
