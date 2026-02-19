package app.logorrr.model

import app.logorrr.conf.FileId
import javafx.beans.binding.Bindings
import javafx.beans.property.{ObjectPropertyBase, SimpleObjectProperty}
import javafx.scene.Node

import java.util.concurrent.Callable


trait BoundId(calcId: FileId => String):
  self: Node =>

  def bindIdProperty(fileIdProperty: ObjectPropertyBase[FileId]): Unit = {
    idProperty.bind(Bindings.createStringBinding(() => calcId(fileIdProperty.get()), fileIdProperty))
  }

  def unbindIdProperty(): Unit = {
    idProperty.unbind()
  }

