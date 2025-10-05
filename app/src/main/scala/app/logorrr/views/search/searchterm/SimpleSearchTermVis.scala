package app.logorrr.views.search.searchterm

import app.logorrr.clv.color.ColorUtil
import app.logorrr.views.search.SearchTerm
import javafx.beans.property.{SimpleIntegerProperty, SimpleObjectProperty, SimpleStringProperty}
import javafx.beans.{InvalidationListener, Observable}
import javafx.scene.control.Label
import javafx.scene.layout.{HBox, Priority, Region, VBox}
import javafx.scene.paint.Color

object SimpleSearchTermVis {

  def apply(searchTerm: SearchTerm): SimpleSearchTermVis = {
    val sstv = new SimpleSearchTermVis
    sstv.textProperty.set(searchTerm.value)
    sstv.colorProperty.set(searchTerm.color)
    sstv
  }
}

class SimpleSearchTermVis extends VBox {

  val hitsProperty = new SimpleIntegerProperty()
  val textProperty = new SimpleStringProperty()
  val colorProperty = new SimpleObjectProperty[Color]()

  colorProperty.addListener(new InvalidationListener {
    override def invalidated(observable: Observable): Unit = {
      setStyle(ColorUtil.mkCssBackgroundString(colorProperty.get()))
    }
  })

  private val label = new Label()
  label.setStyle("-fx-font-weight: bold;")
  label.textProperty().bind(textProperty)


  protected val hbox: HBox = {
    val spacer: Region = new Region
    spacer.setMinWidth(30)
    HBox.setHgrow(spacer, Priority.ALWAYS)
    val hb = new HBox(label, spacer)
    hb.setMaxWidth(Double.MaxValue)
    hb
  }

  protected val hitsLabel = new SearchTermHitsLabel


  getChildren.addAll(hbox, hitsLabel)
}
