package app.logorrr.views.search.st

import app.logorrr.conf.FileId
import app.logorrr.model.LogEntry
import app.logorrr.views.search.MutableSearchTerm
import app.logorrr.views.search.st.RemoveSearchTermButton.buttonCssStyle
import javafx.beans.binding.{Bindings, BooleanBinding}
import javafx.beans.property.{BooleanProperty, ObjectPropertyBase, SimpleLongProperty, StringProperty}
import javafx.collections.ObservableList
import javafx.scene.layout.{HBox, Priority, Region, VBox}
import javafx.scene.paint.Color
import javafx.util.Subscription

import java.util.function.Predicate
import scala.compiletime.uninitialized

class SearchTermToggleButton(entries: ObservableList[LogEntry]) extends ASearchTermToggleButton:

  private val hitsProperty = new SimpleLongProperty()

  private val hitsLabel: SearchTermHitsLabel = new SearchTermHitsLabel

  private val removeSearchTermButton: RemoveSearchTermButton = new RemoveSearchTermButton

  private var contrastColorSubscription: Subscription = uninitialized

  private val hbox: HBox =
    val spacer: Region = new Region
    spacer.setMinWidth(30)
    HBox.setHgrow(spacer, Priority.ALWAYS)
    val hb = new HBox(searchTermLabel, spacer, removeSearchTermButton)
    hb.setMaxWidth(Double.MaxValue)
    hb

  setGraphic(new VBox(hbox, hitsLabel))

  override def init(fileIdProperty: ObjectPropertyBase[FileId]
                    , visibleBinding: BooleanBinding
                    , mutSearchTerm: MutableSearchTerm
                    , valProperty: StringProperty
                    , colorProperty: ObjectPropertyBase[Color]
                    , activeProperty: BooleanProperty): Unit = {
    super.init(fileIdProperty, visibleBinding, mutSearchTerm, valProperty, mutSearchTerm.colorProperty, activeProperty)
    hitsProperty.bind(Bindings.createLongBinding(() => entries.stream().filter(t => t.value.contains(getValue)).count, entries))
    hitsLabel.init(contrastColorProperty, hitsProperty)
    removeSearchTermButton.init(fileIdProperty, visibleBinding, mutSearchTerm)
    // can't bind directly due to ikonli warnings in log
    contrastColorSubscription = contrastColorProperty.subscribe(c => removeSearchTermButton.icon.setStyle(buttonCssStyle(c)))

  }

  override def shutdown(activeProperty: BooleanProperty): Unit = {
    super.shutdown(activeProperty)
    contrastColorSubscription.unsubscribe()
    removeSearchTermButton.shutdown()
    hitsProperty.unbind()
    hitsLabel.shutdown()
  }




