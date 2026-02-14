package app.logorrr.views.search.stg

import app.logorrr.conf.FileId
import app.logorrr.model.BoundId
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import javafx.beans.property.ObjectPropertyBase
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.scene.control.TextField
import javafx.scene.input.{KeyCode, KeyEvent}

object StgNameTextField extends UiNodeFileIdAware:

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[StgNameTextField])



/**
 * Enter a name for the new search term group
 *
 * @param fireEvent a function which fires an event to trigger an onAction callBack
 */
class StgNameTextField(fileId: FileId, fireEvent: () => Unit)
  extends TextField with BoundId(StgNameTextField.uiNode(_).value):

  setPrefWidth(150)
  val maxLength = 200
  setPromptText("Enter a name...")

  setOnKeyPressed:
    (event: KeyEvent) =>
      if event.getCode == KeyCode.ENTER then fireEvent()

  private val restrainLengthListener = new ChangeListener[String] {
    override def changed(observable: ObservableValue[? <: String], oldValue: String, newValue: String): Unit =
      if newValue.length > maxLength then setText(oldValue)
  }

  def init(fileIdProperty: ObjectPropertyBase[FileId]): Unit =
    bindIdProperty(fileIdProperty)
    textProperty().addListener(restrainLengthListener)

  def shutdown(): Unit =
    unbindIdProperty()
    textProperty.removeListener(restrainLengthListener)