package app.logorrr.model

import app.logorrr.conf.FileId
import javafx.beans.property.{ObjectPropertyBase, SimpleObjectProperty}

trait FileIdPropertyHolder:
  val fileIdProperty: SimpleObjectProperty[FileId] = new SimpleObjectProperty[FileId]()

  def getFileId: FileId = fileIdProperty.get()

  def setFileId(fileId: FileId): Unit = fileIdProperty.set(fileId)

  def bindFileIdProperty(fileIdProperty: ObjectPropertyBase[FileId]): Unit = this.fileIdProperty.bind(fileIdProperty)

  def unbindFileIdProperty(): Unit = fileIdProperty.unbind()
