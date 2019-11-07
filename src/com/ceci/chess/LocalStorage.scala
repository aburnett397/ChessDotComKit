package com.ceci.chess

import java.io.File

import scala.collection.JavaConverters._
import scalafx.Includes._
import scalafx.beans.property.MapProperty
import scalafx.beans.property.SetProperty
import scalafx.collections.ObservableSet
import scalafx.collections.ObservableHashSet
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import java.io.ObjectInputStream
import java.io.FileInputStream
import java.io.ObjectOutputStream
import java.io.FileOutputStream
import scalafx.collections.ObservableBuffer
import scalafx.application.Platform

object LocalStorage {
  val folder = new File(System.getProperty("user.home")+"/.chessdotcomkit")
  if (!folder.exists)
    folder.mkdir
  
  val archives = new ObservableBuffer[String]
  folder.list().filter(name => !name.startsWith(".")).map(name => archives += name)
  archives.sort
  
  def read(name: String): GameArchive = {
    val in = new ObjectInputStream(new FileInputStream(new File(folder, name)))
    val archive = in.readObject.asInstanceOf[GameArchive]
    in.close
    archive
  }
  
  def write(archive: GameArchive): Unit = {
    val out = new ObjectOutputStream(new FileOutputStream(new File(folder, archive.player)))
    out.writeObject(archive)
    out.close
    if (!archives.contains(archive.player)) {
      Platform.runLater({
        archives += archive.player
        archives.sort
      })
    }
  }
}