package app.logorrr.views.search

import app.logorrr.conf.FileId
import app.logorrr.model.BoundFileId
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import javafx.beans.property.ObjectPropertyBase
import javafx.scene.control.{TextField, Tooltip}
import net.ladstatt.util.os.OsUtil


object SearchTextField extends UiNodeFileIdAware:

  private val shortCut = s"${OsUtil.osFun("CTRL-F", "COMMAND-F", "CTRL-F")}"

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[SearchTextField])


class SearchTextField extends TextField with BoundFileId(SearchTextField.uiNode(_).value):

  setPrefWidth(200)
  setMaxWidth(200)
  setTooltip(new Tooltip(s"enter search pattern\n\nshortcut: ${SearchTextField.shortCut}"))
  setPromptText(s"search (${SearchTextField.shortCut})")

  def init(fileIdProperty : ObjectPropertyBase[FileId]) : Unit = bindIdProperty(fileIdProperty)
  def shutdown() : Unit = unbindIdProperty()