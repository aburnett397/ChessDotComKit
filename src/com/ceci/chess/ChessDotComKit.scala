
package com.ceci.chess

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.paint.Color
import scalafx.scene.layout.VBox
import scalafx.scene.control.TabPane
import scalafx.scene.control.Tab
import scalafx.scene.layout.Pane
import javafx.scene.Group

object Main extends JFXApp 
{
  var tabs = new TabPane {
    tabs = Seq(
      new Tab() {
        text = "Account"
        content = AccountPane
        closable.value = false
      },
      new Tab() {
        text = "Games"
        content = GameSetPane
        closable.value = false
      },
      new Tab() {
        text = "Board"
        content = GamePane
        closable.value = false
      })
  }
  stage = new JFXApp.PrimaryStage 
  {
    title.value = "ChessDotCom Kit"
    onCloseRequest = _ => System.exit(0)
    scene = new Scene(new Pane, 1280, 720) {
      fill = Color.LightBlue
      content = tabs
      tabs.prefWidth <== width
      tabs.prefHeight <== height
    }
//    val r = """[.\s\S]*]} ([\d]+)([\.]+)\s+[A-Za-z\d-=+#]+\s+\{\[%clk\s+[\d:.]+]}\s[012/-]+"[.\s\S]*""".r
//    val pgn = """[Event \"Live Chess\"]\n[Site \"Chess.com\"]\n[Date \"2019.10.02\"]\n[Round \"-\"]\n[White \"whenImDrunkOrHigh\"]\n[Black \"heccap13\"]\n[Result \"1-0\"]\n[ECO \"D00\"]\n[ECOUrl \"https://www.chess.com/openings/D00-Queens-Pawn-Opening-Mason-Attack\"]\n[CurrentPosition \"8/6pk/p4p2/1p1r3p/2p5/2P2NbP/PP2R1P1/5RK1 b - -\"]\n[Timezone \"UTC\"]\n[UTCDate \"2019.10.02\"]\n[UTCTime \"14:29:16\"]\n[WhiteElo \"1549\"]\n[BlackElo \"1586\"]\n[TimeControl \"300+5\"]\n[Termination \"whenImDrunkOrHigh won by resignation\"]\n[StartTime \"14:29:16\"]\n[EndDate \"2019.10.02\"]\n[EndTime \"14:45:37\"]\n[Link \"https://www.chess.com/live/game/4083569399\"]\n\n1. d4 {[%clk 0:05:04.9]} 1... d5 {[%clk 0:05:02.5]} 2. Bf4 {[%clk 0:05:03.8]} 2... Nf6 {[%clk 0:05:05]} 3. e3 {[%clk 0:05:07.6]} 3... e6 {[%clk 0:05:08.5]} 4. Bd3 {[%clk 0:05:10.8]} 4... Nbd7 {[%clk 0:05:11.5]} 5. Nf3 {[%clk 0:05:13.4]} 5... c5 {[%clk 0:05:15]} 6. c3 {[%clk 0:05:17.1]} 6... a6 {[%clk 0:05:18]} 7. h3 {[%clk 0:05:18.4]} 7... b5 {[%clk 0:05:21.3]} 8. Nbd2 {[%clk 0:05:22]} 8... Bb7 {[%clk 0:05:23.8]} 9. O-O {[%clk 0:05:24.8]} 9... Be7 {[%clk 0:05:17.5]} 10. Re1 {[%clk 0:05:25.4]} 10... Ne4 {[%clk 0:05:20.5]} 11. Qc2 {[%clk 0:05:24.7]} 11... Ndf6 {[%clk 0:05:16.2]} 12. Be5 {[%clk 0:05:22.3]} 12... O-O {[%clk 0:05:14.8]} 13. Bxf6 {[%clk 0:05:20.8]} 13... Nxf6 {[%clk 0:05:17.9]} 14. e4 {[%clk 0:05:18.9]} 14... c4 {[%clk 0:05:14.6]} 15. Bf1 {[%clk 0:05:15.6]} 15... dxe4 {[%clk 0:05:17.8]} 16. Ng5 {[%clk 0:04:55.6]} 16... Qd5 {[%clk 0:04:48.3]} 17. Ngxe4 {[%clk 0:04:06.7]} 17... Qh5 {[%clk 0:04:39.8]} 18. Be2 {[%clk 0:03:51.7]} 18... Qh4 {[%clk 0:04:37.3]} 19. Nxf6+ {[%clk 0:03:46.4]} 19... Bxf6 {[%clk 0:04:38.6]} 20. Bf3 {[%clk 0:03:49.7]} 20... Bxf3 {[%clk 0:03:58.1]} 21. Nxf3 {[%clk 0:03:52.7]} 21... Qh5 {[%clk 0:03:56.4]} 22. Re2 {[%clk 0:03:44.3]} 22... Rfe8 {[%clk 0:03:54.2]} 23. Rae1 {[%clk 0:03:47.3]} 23... Rad8 {[%clk 0:03:56.6]} 24. Ne5 {[%clk 0:03:42.9]} 24... Rd5 {[%clk 0:03:01.9]} 25. Re4 {[%clk 0:02:37.1]} 25... Bh4 {[%clk 0:02:49.5]} 26. Qe2 {[%clk 0:02:17.3]} 26... Qh6 {[%clk 0:02:09.5]} 27. Qg4 {[%clk 0:01:28.3]} 27... Be7 {[%clk 0:02:03.9]} 28. R4e3 {[%clk 0:01:15.9]} 28... Bd6 {[%clk 0:01:46.4]} 29. Qf3 {[%clk 0:01:00.2]} 29... f6 {[%clk 0:00:58.2]} 30. Ng4 {[%clk 0:00:56.3]} 30... Qg5 {[%clk 0:00:45]} 31. R3e2 {[%clk 0:00:14]} 31... Rf5 {[%clk 0:00:41.5]} 32. Qe3 {[%clk 0:00:17.9]} 32... h5 {[%clk 0:00:23]} 33. Qxg5 {[%clk 0:00:12.8]} 33... Rxg5 {[%clk 0:00:24.8]} 34. Nh2 {[%clk 0:00:10.6]} 34... e5 {[%clk 0:00:26.8]} 35. dxe5 {[%clk 0:00:13.7]} 35... Bxe5 {[%clk 0:00:24.6]} 36. f4 {[%clk 0:00:12.8]} 36... Bxf4 {[%clk 0:00:20.1]} 37. Rxe8+ {[%clk 0:00:16.4]} 37... Kh7 {[%clk 0:00:23]} 38. Nf3 {[%clk 0:00:19.3]} 38... Rd5 {[%clk 0:00:24.9]} 39. R8e2 {[%clk 0:00:13.8]} 39... Bg3 {[%clk 0:00:25.7]} 40... Rf1 {[%clk 0:00:15.4]} 1-0","time_control":"300+5","end_time":1570027537,"rated":true,"fen":"8/6pk/p4p2/1p1r3p/2p5/2P2NbP/PP2R1P1/5RK1 b - -","time_class":"blitz","rules":"chess","white":{"rating":1549,"result":"win","@id":"https://api.chess.com/pub/player/whenimdrunkorhigh","username":"whenImDrunkOrHigh"},"black":{"rating":1586,"result":"resigned","@id":"https://api.chess.com/pub/player/heccap13","username":"heccap13"}}"""
//    
//    pgn match {
//      case r(num, dots) => println(2*num.toInt+dots.length/2)
//      //case r(num) => println(num)
//      case _ => println("???")
//    }
//    for (m <- r.findAllMatchIn(pgn))
//      println(m.group(1))
  }
}