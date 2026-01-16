package app.logorrr.views.search

import app.logorrr.conf.{FileId, SearchTerm}
import javafx.beans.property.*
import javafx.scene.paint.Color

trait UnclassifiedPropertyHolder:
  private val unclassifiedProperty = new SimpleBooleanProperty()

  def setUnclassified(isUnclassified: Boolean): Unit = unclassifiedProperty.set(isUnclassified)

  def isUnclassified: Boolean = unclassifiedProperty.get()

trait ValuePropertyHolder:
  val valueProperty: SimpleStringProperty = new SimpleStringProperty()

  def getValue: String = valueProperty.get()

  def setValue(value: String): Unit = valueProperty.set(value)

  def bindValueProperty(valueProperty: SimpleStringProperty): Unit = valueProperty.bind(valueProperty)

  def unbindValueProperty(): Unit = valueProperty.unbind()

trait ColorPropertyHolder:
  val colorProperty: SimpleObjectProperty[Color] = new SimpleObjectProperty[Color]()

  def getColor: Color = colorProperty.get()

  def setColor(color: Color): Unit = colorProperty.set(color)

  def bindColorProperty(colorProperty: ObjectPropertyBase[Color]): Unit = colorProperty.bind(colorProperty)

  def unbindColorProperty(): Unit = colorProperty.unbind()

trait ActivePropertyHolder:
  val activeProperty: SimpleBooleanProperty = new SimpleBooleanProperty()

  def setActive(active: Boolean): Unit = activeProperty.set(active)

  def isActive: Boolean = activeProperty.get()

  def bindActiveProperty(activeProperty: BooleanProperty): Unit = activeProperty.bind(activeProperty)

  def unbindActiveProperty(): Unit = activeProperty.unbind()


trait FileIdPropertyHolder:
  val fileIdProperty: SimpleObjectProperty[FileId] = new SimpleObjectProperty[FileId]()

  def getFileId: FileId = fileIdProperty.get()

  def setFileId(fileId: FileId): Unit = fileIdProperty.set(fileId)

  def bindFileIdProperty(fileIdProperty: ObjectPropertyBase[FileId]): Unit = fileIdProperty.bind(fileIdProperty)

  def unbindFileIdProperty(): Unit = fileIdProperty.unbind()


/**
 * Groups important attributes for search terms together
 */
trait BaseSearchTermModel
  extends ValuePropertyHolder
    with ColorPropertyHolder
    with ActivePropertyHolder
    with UnclassifiedPropertyHolder:

  def bindSearchTerm(mutableSearchTerm: MutableSearchTerm): Unit = {
    bindValueProperty(mutableSearchTerm.valueProperty)
    bindColorProperty(mutableSearchTerm.colorProperty)
    bindActiveProperty(mutableSearchTerm.activeProperty)
  }

  def unbindSearchTerm(): Unit = {
    unbindValueProperty()
    unbindColorProperty()
    unbindActiveProperty()
  }

  def asSearchTerm: SearchTerm = SearchTerm(getValue, getColor, isActive)
