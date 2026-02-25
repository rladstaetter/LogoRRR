package app.logorrr.views.search.st

import app.logorrr.conf.mut.MutSearchTermGroup
import app.logorrr.views.search.MutableSearchTerm
import javafx.beans.property.SimpleListProperty
import javafx.collections.ObservableList
import javafx.scene.control.{ComboBox, ListCell, ListView}
import javafx.util.{Callback, StringConverter, Subscription}

import scala.compiletime.uninitialized

class FavoritesComboBox extends ComboBox[MutSearchTermGroup]:
  setPrefWidth(200)
  setMaxWidth(200)

  var subscription: Subscription = uninitialized

  def init(searchTermGroupEntries: SimpleListProperty[MutSearchTermGroup]
           , mutableSearchTerms: ObservableList[MutableSearchTerm]): Unit = {
    itemsProperty.bind(searchTermGroupEntries)
    // disabled init combobox with selected element on
    // startup - local search terms win
    // searchTermGroupEntries.stream.filter(_.isSelected).forEach(s => getSelectionModel.select(s))

    setConverter(new StringConverter[MutSearchTermGroup] {
      override def toString(stg: MutSearchTermGroup): String =
        Option(stg).map(s => s.nameProperty.get()).getOrElse("")

      override def fromString(string: String): MutSearchTermGroup = {
        new MutSearchTermGroup
      }
    })

    // if element is selected, update toolbar
    subscription = getSelectionModel
      .selectedItemProperty()
      .subscribe(sNull =>
        Option(sNull).foreach(s => {
          mutableSearchTerms.clear()
          mutableSearchTerms.addAll(s.termsProperty.stream.map(s => MutableSearchTerm(s)).toList)
        }))

    setCellFactory(new Callback[ListView[MutSearchTermGroup], ListCell[MutSearchTermGroup]] {
      override def call(p: ListView[MutSearchTermGroup]): ListCell[MutSearchTermGroup] = new ListCell[MutSearchTermGroup] {
        override def updateItem(item: MutSearchTermGroup, empty: Boolean): Unit = {
          super.updateItem(item, empty)
          textProperty().unbind() // Important!
          if (empty || item == null) {
            setText(null)
          } else {
            textProperty().bind(item.nameProperty)
          }
        }
      }
    })

  }

  def shutdown(): Unit =
    subscription.unsubscribe()
