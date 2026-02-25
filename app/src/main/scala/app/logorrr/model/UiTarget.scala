package app.logorrr.model

import app.logorrr.conf.{FileId, TimeSettings}
import javafx.beans.property.ObjectPropertyBase
import javafx.scene.{Node, Scene}
import javafx.stage.Window

trait UiTarget extends Node:

  def contains(fileId: FileId): Boolean

  def selectFile(fileId: FileId): Unit

  def addData(model: LogorrrModel): Unit

  def selectLastLogFile(): Unit

  def getInfos: Seq[FileIdDividerSearchTerm]

  def applyTimeSettings(timesettings: TimeSettings): Unit 

  def shutdown(): Unit

