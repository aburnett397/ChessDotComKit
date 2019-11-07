package com.ceci.chess

import scalafx.beans.property.FloatProperty
import scalafx.beans.property.StringProperty
import java.io.File
import java.io.BufferedWriter
import java.io.BufferedReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.InputStreamReader
import scalafx.application.Platform
import scalafx.beans.property.BooleanProperty
import com.ceci.chess.lib.Side
import scalafx.beans.property.IntegerProperty

object Kibitzer
{
  val exe = new File("stockfish/stockfish_10_x64.exe")
  val pvId = ".*multipv ([0-9]+).*".r
  val score = ".*score cp ([0-9-]+).*".r
  val curmove = ".*currmove ([0-9A-Za-z]+).*".r
  val depth = ".*depth ([0-9]+).*".r
}

case class Kibitzer(val board: Board, val nLines: Int)
{
  val scores: Array[FloatProperty] = (0 to nLines-1).toArray.map(index => FloatProperty(0))
  val lines: Array[StringProperty] = (0 to nLines-1).toArray.map(index => StringProperty(""))
  val depths: Array[IntegerProperty] = (0 to nLines-1).toArray.map(index => IntegerProperty(0))
  
  def scanOutput(s: String, white: Boolean): Unit = {
    s match {
      case Kibitzer.pvId(sid) => Platform.runLater({
        val id = sid.toInt-1
        s match {
          case Kibitzer.score(score) => scores(id).set((if (white) score.toInt/100f else 1-score.toInt/100f))
          case _ => scores(id).set(0)
        }
        val line = s.lastIndexOf("pv ")
        if (line > 0)
          lines(id).set(s.substring(line+3))
        else s match {
          case Kibitzer.curmove(move) => lines(id).set(move)
          case _ => lines(id).set("")
        }
        s match {
          case Kibitzer.depth(depth) => depths(id).set(depth.toInt)
          case _ => depths(id).set(0)
        }
      })
      case _ => //println(s)
    }
  }
  
  
  val proc = Runtime.getRuntime.exec(Kibitzer.exe.getAbsolutePath)
  val out = new BufferedWriter(new OutputStreamWriter(proc.getOutputStream))
  val in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
  var white = board.whiteToMove.value
  
  new Thread {
    override def run = {
      var line: String = in.readLine()
      while (line != null) {
        scanOutput(line, white)
        line = in.readLine()
      }
      out.close
      in.close
      if (proc.isAlive)
        proc.destroy
    }
  }.start()
    
  val running = new BooleanProperty
  running.value = false
  var id: Any = null
  
  def eval: Unit = eval(null)
  def eval(id: Any): Unit = {  
    this.id = id
    white = board.board.getSideToMove == Side.WHITE
    out.write("stop");
  	out.newLine
  	out.flush
    out write("position fen "+board.board.getFen())
  	out.newLine
  	out.write("setoption name MultiPV value "+nLines);
  	out.newLine
  	out.write("go depth 30");
  	out.newLine
  	out.flush
  	running.value = true;
  }
  
  def stop = {
	  out.write("stop");
  	out.newLine
  	out.flush
  	running.value = false;
	}
  
  board.boardProperty.addListener((source, oldValue, newValue) => if (running.value) eval(id))
}
