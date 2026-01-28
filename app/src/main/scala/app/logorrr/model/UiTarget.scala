package app.logorrr.model

import app.logorrr.conf.FileId
import javafx.scene.Node

trait UiTarget extends Node:

  def contains(fileId: FileId): Boolean

  def selectFile(fileId: FileId): Unit

  def addData(model: LogorrrModel): Unit

  def selectLastLogFile(): Unit

  def getInfos: Seq[FileIdDividerSearchTerm]

  def shutdown(): Unit

