package com.ceci.chess

import scala.collection.JavaConverters._
import scalafx.Includes._
import scalafx.scene.layout.VBox
import scalafx.scene.canvas.Canvas
import scalafx.beans.property.IntegerProperty
import scalafx.beans.property.ObjectProperty
import com.ceci.chess.lib.move.MoveList
import scalafx.beans.property.FloatProperty
import scalafx.scene.layout.HBox
import scalafx.scene.control.Button
import scalafx.scene.paint.Color
import scalafx.scene.text.Font
import scalafx.scene.paint.Paint
import scalafx.scene.text.TextAlignment

class AnalysisPane extends VBox {
  
  val depthLim = IntegerProperty(5)
  val timeMin = FloatProperty(1)
  val timeMax = FloatProperty(5)
  val kibitzer = Kibitzer(AppState.board, 1)
  
  val depth = IntegerProperty(0)
  depth <== kibitzer.depths(0)
  val score = FloatProperty(0)
  score <== kibitzer.scores(0)
  AppState.game.addListener((source, oldValue, newValue) => stop)
  
  depth.onChange((source, oldValue, newValue) => {
    if (kibitzer.running.value && kibitzer.id == AppState.game.value) {
      val now = System.currentTimeMillis
      val since = (now-lastEval)/1000f
      if (since > timeMin.value && (since > timeMax.value || newValue.intValue >= depthLim.value)) {
        
        scores(move.value) = score.value
        lastEval = now
        if (move.value >= AppState.moveList.value.size-1)
          stop
        else {
          move.value = move.value+1
          kibitzer.board.moveTo(AppState.moveList.value, move.value)
        }
        canvas.draw
      }
    } else println("???")
  })
  
  var lastEval: Long = 0
  var scores: Array[Float] = null
  var barHover: Integer = -1
  var move = IntegerProperty(-1)
  move.addListener((source, prev, next) => if (kibitzer.id == AppState.game.value)
    AppState.move.value = next)
  
  def stop = kibitzer.stop
  def start = {
    if (!kibitzer.running.value && AppState.game.value != null) {
      move.value = 0
      scores = new Array(AppState.moveList.value.size)
      lastEval = System.currentTimeMillis
      AppState.board.reset
      kibitzer.eval(AppState.game.value)
      canvas.draw
    }
  }
  
  object canvas extends Canvas {
    
    val g = graphicsContext2D
    g.font = new Font(Font.default.name, .7*Font.default.size)
    g.setTextAlign(TextAlignment.CENTER)
    def draw = {
      val g = graphicsContext2D
      g.setLineWidth(1)
      g.setFill(Color.SteelBlue)
      g.fillRect(0, 0, width.value, height.value)
      if (scores != null && scores.length > 0) {
        val max = math.min(10, math.ceil(scores.foldLeft[Float](0)((res, score) => math.max(math.abs(res), math.abs(score)))))
        for (i <- 0 to scores.length-1) {
          
          g.setFill(if (scores(i) > 0) Color.White else Color.Black)
          val bar = math.min(.5*height.value, math.abs(scores(i)*.5*height.value/max))
          val bw = width.value*1.0/scores.length
          
          if (scores(i) > 0)
            g.fillRect(i*bw, .5*height.value-bar, bw, bar)
          else g.fillRect(i*bw, .5*height.value, bw, bar)
          
          if (i == barHover) {
            g.setFill(Color.Orange.opacity(.5))
            g.fillRect(i*bw, 0, bw, height.value)
            g.setFill(Color.Gray)
            g.strokeText(f"${scores(i)}%.1f", i*bw+.5*bw, g.font.size)
          }
          if (i == move.value) {
            g.setFill(Color.Red.opacity(.5))
            g.fillRect(i*bw, 0, bw, height.value)
          }
        }
      }
    }
    
    width.addListener((source, oldValue, newValue) => draw)
    height.addListener((source, oldValue, newValue) => draw)
  }
  
  val config = new HBox {
    children = Seq(
        new Button {
          disable <== AppState.game.isNull()
          text <== when (kibitzer.running) choose "Stop" otherwise "Start"
          onAction = _ => {
            if (kibitzer.running.value) stop
            else start
          }
        }
    )
  }
  
  children = Seq(
      config, 
      canvas
  )
  
  canvas.width <== width
  canvas.height <== height-config.height
  
  onMouseMoved.value = event => if (scores != null && scores.length > 0) {
    barHover = (event.x*scores.length/width.value).toInt
    canvas.draw
  }
  onMouseExited.value = event => if (scores != null && scores.length > 0) {
    barHover = -1
    canvas.draw
  }
  onMouseClicked.value = event => if (scores != null && scores.length > 0) {
    val index = (event.x*scores.length/width.value).toInt
    if (!kibitzer.running.value && AppState.moveList.value != null && index < AppState.moveList.value.size)
      move.value = index
  }
  move.addListener((source, oldValue, newValue) => canvas.draw)
}