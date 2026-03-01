package app.logorrr.model

import app.logorrr.conf.{FileId, TimeSettings}
import app.logorrr.views.logfiletab.LogFilePane
import javafx.scene.Node
import javafx.stage.Window

trait UiTarget extends Node:

  def contains(fileId: FileId): Boolean

  def selectFile(fileId: FileId): Unit

  def addData(owner : Window, logFilePane : LogFilePane): Unit

  def selectLastLogFile(): Unit

  def applyTimeSettings(timeSettings: TimeSettings): Unit

  def shutdown(): Unit

