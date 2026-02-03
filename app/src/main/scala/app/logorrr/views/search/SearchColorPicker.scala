package app.logorrr.views.search

import app.logorrr.conf.FileId
import app.logorrr.model.BoundFileId
import app.logorrr.util.JfxUtils
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import javafx.beans.property.ObjectPropertyBase
import javafx.scene.control.{ColorPicker, Tooltip}

object SearchColorPicker extends UiNodeFileIdAware:

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[SearchColorPicker])

class SearchColorPicker extends ColorPicker with BoundFileId(SearchColorPicker.uiNode(_).value):

  setValue(JfxUtils.randColor)
  setMaxWidth(46)
  setTooltip(new Tooltip("choose color"))
  // see https://stackoverflow.com/questions/45966844/how-to-change-the-icon-size-of-a-color-picker-in-javafx
  setStyle("""-fx-color-label-visible: false""".stripMargin)

  def init(fileIdProperty: ObjectPropertyBase[FileId]): Unit = bindIdProperty(fileIdProperty)

  def shutdown(): Unit = unbindIdProperty()
