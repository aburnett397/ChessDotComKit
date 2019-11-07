package com.ceci.chess

import scala.collection.JavaConverters._
import scalafx.Includes._
import scalafx.collections.ObservableBuffer
import scalafx.beans.property.ObjectProperty
import scalafx.beans.property.IntegerProperty
import scalafx.beans.property.BooleanProperty
import scalafx.beans.property.StringProperty
import com.ceci.chess.lib.game.Game
import com.ceci.chess.lib.move.MoveList
import scalafx.collections.ObservableSet
import scalafx.collections.ObservableHashSet

object AppState {
  
  //val games = new ObservableBuffer[GameData] {onChange((buffer, changes) => applyFilter)}
  val games = new ObservableHashSet[GameData] {onChange((buffer, changes) => applyFilter)}
  val filteredGames = new ObservableBuffer[GameData]
  val game = new ObjectProperty[Game]
  game.value = null
  val moveList = new ObjectProperty[MoveList]
  moveList.value = null
  val move = IntegerProperty(-1)
  game.addListener((source, oldValue, newValue) => {
    moveList.value = if (newValue == null) null else newValue.getHalfMoves
    move.value = -1
  })
  val board = Board(new com.ceci.chess.lib.Board)
  val useFilter = BooleanProperty(false)
  useFilter.onChange((source, oldValue, newValue) => applyFilter)
  val before = IntegerProperty(-1)
  before.onChange((source, oldValue, newValue) => applyFilter)
  val after = IntegerProperty(-1)
  after.onChange((source, oldValue, newValue) => applyFilter)
  val timeControl = StringProperty("")
  timeControl.onChange((source, oldValue, newValue) => applyFilter)
  
  def applyFilter: Unit = {
    filteredGames.clear
    filteredGames ++= games.toList.filter(filter(_)).sortWith((g1, g2) => g1.date < g2.date)
  }
  def filter(game: GameData): Boolean = {
    !useFilter.value || 
      (timeControl.value.length == 0 || timeControl.value.equals(game.timeControl)) && 
        (before.value < 0 || game.date < before.value) && 
        (after.value < 0 || game.date > after.value)
  }
  
  def clearGames = games.clear()
  def addGames(archive: GameArchive) = {
    clearGames
    games ++= archive.games
  }
}
