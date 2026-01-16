package app.logorrr.views.search.st

import app.logorrr.conf.FileId
import app.logorrr.util.{HashUtil, JfxUtils}
import app.logorrr.views.a11y.{UiNode, UiNodeSearchTermAware}
import app.logorrr.views.search.*
import app.logorrr.views.util.CssBindingUtil
import javafx.beans.binding.{Bindings, StringBinding}
import javafx.beans.property.{SimpleIntegerProperty, SimpleObjectProperty}
import javafx.beans.{InvalidationListener, Observable}
import javafx.event.ActionEvent
import javafx.scene.control.{Label, ToggleButton}
import javafx.scene.layout.{HBox, Priority, Region, VBox}
import javafx.scene.paint.Color


object SearchTermToggleButton extends UiNodeSearchTermAware:
  override def uiNode(fileId: FileId, searchTerm: String): UiNode = UiNode(classOf[SearchTermToggleButton].getSimpleName + "-" + HashUtil.md5Sum(fileId.absolutePathAsString + ":" + searchTerm))

  def apply(fileId: FileId
            , searchTerm: MutableSearchTerm
            , hits: Int
            , updateSearchTerm: => Unit
            , removeSearchTerm: MutableSearchTerm => Unit): SearchTermToggleButton =
    val b = new SearchTermToggleButton(updateSearchTerm)
    if searchTerm.isUnclassified then
      b.removeSearchTermButton.setVisible(false)
      // b.removeSearchTermButton.setManaged(false)
      b.removeFnProperty.set(() => {
        ()
      })
    else
      b.removeFnProperty.set(() => {
        removeSearchTerm(searchTerm)
      })

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

  def setMutableSearchTerm(searchTerm: MutableSearchTerm): Unit = mutableSearchTermProperty.set(searchTerm)

  def getMutableSearchTerm(): MutableSearchTerm = mutableSearchTermProperty.get()

  val origColorProperty = new SimpleObjectProperty[Color]()

  def setOrigColor(color: Color): Unit = origColorProperty.set(color)

  def getOrigColor(): Color = origColorProperty.get()

  val hitsProperty = new SimpleIntegerProperty()

  def setHits(i: Int): Unit = hitsProperty.set(i)

  idProperty.bind:
    Bindings.createStringBinding(
      () => SearchTermToggleButton.uiNode
        (getFileId, getValue).value
      , fileIdProperty
      , valueProperty
    )

  val contrastColorProperty: SimpleObjectProperty[Color] =
    val p = new SimpleObjectProperty[Color]()
    p.bind(CssBindingUtil.mkContrastPropertyBinding(colorProperty))
    p

  private lazy val textStyleBinding: StringBinding = CssBindingUtil.mkTextStyleBinding(contrastColorProperty)

  val vBox = new VBox()

  val label: Label =
    val l = new Label
    l.textProperty().bind(valueProperty)
    l.styleProperty().bind(textStyleBinding)
    l

  val removeFnProperty = new SimpleObjectProperty[() => Unit]()

  val removeSearchTermButton: RemoveSearchTermButton =
    val rstb = new RemoveSearchTermButton
    rstb.onActionProperty().set((_: ActionEvent) => {
      val funToPerform: () => Unit = removeFnProperty.get()
      funToPerform()
    })
    rstb.idProperty.bind(Bindings.createStringBinding(
      () =>
        (for fileId <- Option(getFileId)
             searchTerm <- Option(getValue)
        yield RemoveSearchTermButton.uiNode(fileId, searchTerm).value).getOrElse(""),
      fileIdProperty, valueProperty))
    rstb.setStyle(
      """
        |-fx-padding: 0;
        |-fx-background-color: inherit;""".stripMargin
    )
    rstb


  protected val hbox: HBox =
    val spacer: Region = new Region
    spacer.setMinWidth(30)
    HBox.setHgrow(spacer, Priority.ALWAYS)
    val hb = new HBox(label, spacer)
    hb.setMaxWidth(Double.MaxValue)
    hb

  protected val hitsLabel: SearchTermHitsLabel =
    val l = new SearchTermHitsLabel
    l.textProperty().bind(Bindings.createStringBinding(() => s"Hits: ${hitsProperty.get()}", hitsProperty))
    l.styleProperty().bind(textStyleBinding)
    l

  hbox.getChildren.add(removeSearchTermButton)
  vBox.getChildren.addAll(hbox, hitsLabel)

  setGraphic(vBox)

  // private val vis = SearchTermUiComponent(fileId, hits, searchTerm, removeSearchTerm)

  // depending on the toggle status update search results in textview and chunklistview

  /*
  selectedProperty().addListener:
    new InvalidationListener:
      override def invalidated(observable: Observable): Unit =
        println("Update?")
        updateSearchTerm
*/

  /** contrast color is used for label text */

  selectedProperty().addListener:
    JfxUtils.onNew[java.lang.Boolean]: selected =>
      if selected then {
        setColor(getOrigColor())
        getMutableSearchTerm().setActive(true)
        updateSearchTerm
      } else {
        setColor(Color.WHITESMOKE)
        getMutableSearchTerm().setActive(false)
        updateSearchTerm
      }

  // styleProperty.bind(CssBindingUtil.mkGradientStyleBinding(colorProperty))
  // update visual appearance depending if selected or not
  /*

  */

  styleProperty().bind(CssBindingUtil.mkGradientStyleBinding(colorProperty))

//setGraphic(vis)

//selectedProperty.bind(searchTerm.activeProperty)
// setSelected(searchTerm.isSelected) // fire selected property if active (see above)

