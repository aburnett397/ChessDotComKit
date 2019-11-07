package com.ceci.chess

import scala.collection.Map
import scala.concurrent.Future
import scala.concurrent.forkjoin._
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import scala.util.parsing.json._
import com.ceci.chess.lib.game.GameResult
import scalafx.beans.property.IntegerProperty
import scalafx.application.Platform
import com.ceci.chess.lib.pgn.PgnHolder
import scala.util.matching.Regex.MatchIterator
import scala.util.matching.Regex.MatchIterator
import scala.util.matching.Regex

class CC[T] { def unapply(a:Any):Option[T] = Some(a.asInstanceOf[T]) }
object M extends CC[Map[String, Any]]
object L extends CC[List[Any]]
object S extends CC[String]

object PubApi
{
  def fetchArchive(player: String, progress: IntegerProperty = null): Future[GameArchive] = Future {
    
    println(s"Fetching $player")
    val archiveUrl = s"https://api.chess.com/pub/player/$player/games/archives"
    val urlsSeq = for {
      Some(M(map)) <- List(JSON.parseFull(scala.io.Source.fromURL(archiveUrl).mkString))
      L(list) = map("archives")
      urls <- list
    } yield urls
    val urls: List[String] = urlsSeq.asInstanceOf[List[String]]
    
    val games: List[GameData] = for {
      i <- urls.indices.toList
      Some(M(map)) <- List(JSON.parseFull(scala.io.Source.fromURL(urls(i)).mkString))
      _ = if (progress != null) Platform.runLater({progress.set(i*100/urls.length)})
      L(games) = map("games")
      M(game) <- games
      S(pgn) = game("pgn")
    } yield GameData(urls(i), player, pgn)
    
    val archive = GameArchive(player, games)
    LocalStorage.write(archive)
    println(s"Done fetching $player")
    archive
  }
}

object PgnFields
{
  val date = """[.\s\S]*UTCDate "(\d\d\d\d)\.(\d\d)\.(\d\d)"[.\s\S]*""".r
  val time = """[.\s\S]*UTCTime "(\d\d):(\d\d):(\d\d)"[.\s\S]*""".r
  val white = """[.\s\S]*White "([^"]*)"[.\s\S]*""".r
  val black = """[.\s\S]*Black "([^"]*)"[.\s\S]*""".r
  val whiteElo = """[.\s\S]*WhiteElo "([^"]*)"[.\s\S]*""".r
  val blackElo = """[.\s\S]*BlackElo "([^"]*)"[.\s\S]*""".r
  val result = """[.\s\S]*Result "([^"]*)"[.\s\S]*""".r
  val timeControl = """[.\s\S]*TimeControl "([^"]*)"[.\s\S]*""".r
  val lastMove = """[.\s\S]*]} ([\d]+)([\.]+)\s+[A-Za-z\d-=+#]+\s+\{\[%clk\s+[\d:.]+]}\s[012/-]+$""".r
  val termination = """[.\s\S]*Termination "([^"]*)"[.\s\S]*""".r
}

@SerialVersionUID(5487265134L)
case class GameData(val url: String, val player: String, val pgn: String) extends Serializable {
  
  @SerialVersionUID(871545314638713687L)
  class Result(val name: String) extends Serializable
  object Win extends Result("Win")
  object Loss extends Result("Loss")
  object Draw extends Result("Draw")
//  val date: Long = (for {
//    PgnFields.date(year, month, day) <- pgn
//    PgnFields.time(hour, minute, second) <- pgn
//  } yield java.time.Instant.parse(s"$year-$month-${day}T$hour:$minute:${second}Z").getEpochSecond).headOption.getOrElse(0)
  val date: Long = pgn match {
    case PgnFields.date(year, month, day) => pgn match {
      case PgnFields.time(hour, minute, second) => java.time.Instant.parse(s"$year-$month-${day}T$hour:$minute:${second}Z").getEpochSecond
      case _ => 0
    }
    case _ => 0
  }
  val white: String = pgn match {
    case PgnFields.white(player) => player
    case _ => "???"}
  val black: String = pgn match {
    case PgnFields.black(player) => player
    case _ => "???"}
  val result: String = pgn match {
    case PgnFields.result(result) => result
    case _ => "???"}
  val outcome: Result = {
    val res = result
    res match {
      case "1/2-1/2" => Draw
      case "1-0" => if (white.equals(player)) Win else Loss
      case "0-1" => if (white.equals(player)) Loss else Win
      case _ => new Result(res)
    }
  }
  val whiteElo: Int = pgn match {
    case PgnFields.whiteElo(elo) => elo.toInt
    case _ => 0}
  val blackElo: Int = pgn match {
    case PgnFields.blackElo(elo) => elo.toInt
    case _ => 0}
  val termination: String = pgn match {
    case PgnFields.termination(termination) => termination
    case _ => "???"}
  val nMoves: Int = pgn match {
    case PgnFields.lastMove(num, dots) => num.toInt
    case _ => 0}
  val nHalfMoves: Int = pgn match {
    case PgnFields.lastMove(num, dots) => 2*num.toInt+dots.length/2-1
    case _ => 0}
  val timeControl: String = pgn match {
    case PgnFields.timeControl(result) => result
    case _ => "???"}
  
  def game: com.ceci.chess.lib.game.Game = {
    val holder = new PgnHolder(pgn, false)
    holder.loadPgn
    holder.getGame.get(0).loadMoveText
    holder.getGame.get(0)
  }
  
  override def hashCode: Int = url.hashCode
}

@SerialVersionUID(31578454136L)
case class GameArchive(val player: String, val games: List[GameData]) extends Serializable {
  
}