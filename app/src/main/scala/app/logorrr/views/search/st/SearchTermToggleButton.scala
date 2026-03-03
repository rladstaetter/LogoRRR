package app.logorrr.views.search.st

import app.logorrr.conf.FileId
import app.logorrr.model.{LogEntry, ReoderSearchTermButtonEvent}
import app.logorrr.views.search.MutableSearchTerm
import javafx.animation.FadeTransition
import javafx.beans.binding.{Bindings, BooleanBinding}
import javafx.beans.property.{BooleanProperty, ObjectPropertyBase, SimpleLongProperty, SimpleObjectProperty}
import javafx.collections.ObservableList
import javafx.scene.Cursor
import javafx.scene.input.DragEvent
import javafx.scene.layout.{HBox, Priority, Region, VBox}
import javafx.scene.paint.Color
import javafx.util.{Duration, Subscription}

import java.util.function.Predicate
import scala.compiletime.uninitialized


class SearchTermToggleButton(entries: ObservableList[LogEntry]) extends ASearchTermToggleButton:

  val mutableSearchTermProperty = new SimpleObjectProperty[MutableSearchTerm]()

  private val hitsProperty = new SimpleLongProperty()

  private val hitsLabel: SearchTermHitsLabel = new SearchTermHitsLabel

  private val removeSearchTermButton: RemoveSearchTermButton = new RemoveSearchTermButton

  val dragHandle = new DragToReorderButton()

  val colorPicker = new SearchTermToggleButtonColorPicker()

  private var contrastColorSubscription: Subscription = uninitialized

  private val upperHBox: HBox =
    val spacer: Region = new Region
    spacer.setMinWidth(30)
    HBox.setHgrow(spacer, Priority.ALWAYS)
    val hb = new HBox(dragHandle, searchTermLabel, spacer, colorPicker, removeSearchTermButton)
    hb.setMaxWidth(Double.MaxValue)
    hb

  private val lowerBox: HBox =
    val spacer: Region = new Region
    spacer.setMinWidth(30)
    HBox.setHgrow(spacer, Priority.ALWAYS)
    val hb = new HBox(hitsLabel, spacer)
    hb.setMaxWidth(Double.MaxValue)
    hb

  setGraphic(new VBox(upperHBox, lowerBox))


  def initDnD(): Unit = {
    setOnDragEntered((event: DragEvent) => {
      val source = event.getGestureSource
      if (source != this && source.isInstanceOf[SearchTermToggleButton]) {
        val sourceToggleButton = source.asInstanceOf[SearchTermToggleButton]
        if (sourceToggleButton.idProperty().get() != idProperty().get()) {
          fireEvent(ReoderSearchTermButtonEvent(sourceToggleButton.mutableSearchTermProperty.get(), mutableSearchTermProperty.get()))
        } 
      } 
      event.consume()
    })

    setOnDragDone((event: DragEvent) => {
      val ft = new FadeTransition(Duration.millis(200), this)
      ft.setFromValue(0.2)
      ft.setToValue(1.0)
      ft.play()
      event.consume()
    })
  }

  def init(fileIdProperty: ObjectPropertyBase[FileId]
           , visibleBinding: BooleanBinding
           , mutSearchTerm: MutableSearchTerm): Unit = {
    super.init(fileIdProperty, visibleBinding, mutSearchTerm.valueProperty, mutSearchTerm.colorProperty, mutSearchTerm.activeProperty)
    mutableSearchTermProperty.set(mutSearchTerm)
    dragHandle.init(this, mutSearchTerm)
    colorPicker.init(mutSearchTerm.colorProperty)

    hitsProperty.bind(Bindings.createLongBinding(() => entries.stream().filter(t => t.value.contains(getValue)).count, entries))
    hitsLabel.init(contrastColorProperty, hitsProperty)
    removeSearchTermButton.init(fileIdProperty, visibleBinding, mutSearchTerm)
    // can't bind directly due to ikonli warnings in log
    contrastColorSubscription = contrastColorProperty.subscribe(c =>
      removeSearchTermButton.icon.setStyle(AnIkonliButton.buttonCssStyle(removeSearchTermButton.icon, c))
      dragHandle.icon.setStyle(AnIkonliButton.buttonCssStyle(dragHandle.icon, c))
    )
    initDnD()
  }

  def shutdown(activeProperty: BooleanProperty
               , colorProperty: ObjectPropertyBase[Color]): Unit = {
    super.shutdown(activeProperty)
    colorPicker.shutdown(colorProperty)
    contrastColorSubscription.unsubscribe()
    removeSearchTermButton.shutdown()
    hitsProperty.unbind()
    hitsLabel.shutdown()
  }




