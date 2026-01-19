package app.logorrr.views.search.st

import app.logorrr.conf.FileId
import app.logorrr.util.{HashUtil, JfxUtils}
import app.logorrr.views.a11y.{UiNode, UiNodeSearchTermAware}
import app.logorrr.views.search.*
import app.logorrr.views.util.CssBindingUtil
import javafx.beans.binding.{Bindings, StringBinding}
import javafx.beans.property.{SimpleIntegerProperty, SimpleObjectProperty}
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.event.ActionEvent
import javafx.scene.control.{Label, ToggleButton}
import javafx.scene.layout.{HBox, Priority, Region, VBox}
import javafx.scene.paint.Color

import java.lang


object SearchTermToggleButton extends UiNodeSearchTermAware:

  override def uiNode(fileId: FileId, searchTerm: String): UiNode =
    Option(fileId) match
      case Some(value) => UiNode(classOf[SearchTermToggleButton].getSimpleName + "-" + HashUtil.md5Sum(value.absolutePathAsString + ":" + searchTerm))
      case None => UiNode(classOf[SearchTermToggleButton].getSimpleName + "-" + HashUtil.md5Sum(":" + searchTerm))


  def apply(fileId: FileId
            , searchTerm: MutableSearchTerm
            , hits: Int
            , updateSearchTerm: => Unit
            , removeSearchTerm: MutableSearchTerm => Unit): SearchTermToggleButton =
    val b = new SearchTermToggleButton(updateSearchTerm)
    if searchTerm.isUnclassified then
      b.removeSearchTermButton.setVisible(false)
      b.removeFnProperty.set:
        () => ()
    else
      b.removeFnProperty.set:
        () => removeSearchTerm(searchTerm)


    b.setUnclassified(searchTerm.isUnclassified)
    b.setMutableSearchTerm(searchTerm)
    b.setFileId(fileId)
    b.setHits(hits)

    b.setValue(searchTerm.getValue)
    b.setOrigColor(searchTerm.getColor)
    b.setColor(searchTerm.getColor)
    b.setActive(searchTerm.isActive)
    b.setSelected(searchTerm.isActive)
    b

/**
 * Displays a search term and triggers displaying the results.
 */
class SearchTermToggleButton(updateSearchTerm: => Unit) extends ToggleButton
  with BaseSearchTermModel
  with FileIdPropertyHolder:

  val mutableSearchTermProperty = new SimpleObjectProperty[MutableSearchTerm]()
  val origColorProperty = new SimpleObjectProperty[Color]()
  val hitsProperty = new SimpleIntegerProperty()

  // for tests
  idProperty.bind:
    Bindings.createStringBinding(
      () => SearchTermToggleButton.uiNode
        (getFileId, getValue).value
      , fileIdProperty
      , valueProperty
    )

  // --- listeners START

  private val selectedListener = JfxUtils.onNew[lang.Boolean]: selected =>
    if selected then {
      setColor(getOrigColor)
      getMutableSearchTerm.setActive(true)
      updateSearchTerm
    } else {
      setColor(Color.WHITESMOKE)
      getMutableSearchTerm.setActive(false)
      updateSearchTerm
    }
  // --- listeners END (see also unbind function)


  val label: Label =
    val l = new Label
    l.textProperty().bind(valueProperty)
    l.styleProperty().bind(textStyleBinding)
    l

  // holds function which is triggered if remove button is clicked
  val removeFnProperty = new SimpleObjectProperty[() => Unit]()

  val removeSearchTermButton: RemoveSearchTermButton =
    val rstb = new RemoveSearchTermButton
    rstb.onActionProperty().set((_: ActionEvent) => {
      // interesting pattern to call a function 'on demand'
      val funToPerform: () => Unit = removeFnProperty.get()
      funToPerform()
    })
    rstb.idProperty.bind(Bindings.createStringBinding(
      () =>
        (for fileId <- Option(getFileId)
             searchTerm <- Option(getValue)
        yield RemoveSearchTermButton.uiNode(fileId, searchTerm).value).getOrElse(""),
      fileIdProperty, valueProperty))
    rstb.icon.setStyle(RemoveSearchTermButton.buttonCssStyle(contrastColorProperty.get()))
    rstb

  private lazy val colorListener = new ChangeListener[Color] {
    override def changed(observable: ObservableValue[? <: Color], oldValue: Color, newValue: Color): Unit = {
      removeSearchTermButton.icon.setStyle(RemoveSearchTermButton.buttonCssStyle(newValue))
    }
  }

  lazy val contrastColorProperty: SimpleObjectProperty[Color] =
    val p = new SimpleObjectProperty[Color]()
    p.bind(CssBindingUtil.mkContrastPropertyBinding(colorProperty))
    p.addListener(colorListener)
    p

  private lazy val textStyleBinding: StringBinding = CssBindingUtil.mkTextStyleBinding(contrastColorProperty)


  lazy val hitsLabel: SearchTermHitsLabel =
    val l = new SearchTermHitsLabel
    l.textProperty().bind(Bindings.createStringBinding(() => s"Hits: ${hitsProperty.get()}", hitsProperty))
    l.styleProperty().bind(textStyleBinding)
    l

  selectedProperty().addListener(selectedListener)
  styleProperty().bind(CssBindingUtil.mkGradientStyleBinding(colorProperty))

  // --- layout

  private val hbox: HBox =
    val spacer: Region = new Region
    spacer.setMinWidth(30)
    HBox.setHgrow(spacer, Priority.ALWAYS)
    val hb = new HBox(label, spacer, removeSearchTermButton)
    hb.setMaxWidth(Double.MaxValue)
    hb

  setGraphic(new VBox(hbox, hitsLabel))

  // -- functions -----------------------------------------------------------
  def setMutableSearchTerm(searchTerm: MutableSearchTerm): Unit = mutableSearchTermProperty.set(searchTerm)

  def getMutableSearchTerm: MutableSearchTerm = mutableSearchTermProperty.get()

  def setOrigColor(color: Color): Unit = origColorProperty.set(color)

  def getOrigColor: Color = origColorProperty.get()

  def setHits(i: Int): Unit = hitsProperty.set(i)

  def unbind(): Unit = {
    idProperty().unbind()
    label.textProperty().unbind()
    label.styleProperty().unbind()
    hitsLabel.textProperty().unbind()
    hitsLabel.styleProperty().unbind()
    contrastColorProperty.removeListener(colorListener)
    removeSearchTermButton.idProperty().unbind()

    styleProperty().unbind()
    selectedProperty().removeListener(selectedListener)
  }
