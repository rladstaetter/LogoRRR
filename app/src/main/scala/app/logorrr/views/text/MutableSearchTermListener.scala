package app.logorrr.views.text

import app.logorrr.model.LogEntry
import app.logorrr.views.search.MutableSearchTerm
import javafx.collections.transformation.FilteredList
import javafx.collections.{FXCollections, ListChangeListener, ObservableList}
import javafx.scene.control.ListView
import javafx.scene.paint.Color

import scala.jdk.CollectionConverters._

class MutableSearchTermListener(mutableSearchTerms: FilteredList[MutableSearchTerm]
                                , targetPairs: ObservableList[(String, Color)]
                                , listview: ListView[LogEntry]
                               ) extends ListChangeListener[MutableSearchTerm]:
  override def onChanged(c: ListChangeListener.Change[? <: MutableSearchTerm]): Unit = {
    while (c.next()) {
      val list = FXCollections.observableArrayList[(String, Color)]()
      if (c.wasPermutated()) {
      } else if (c.wasUpdated()) {
        c.getList.forEach(st => {
          if (st.isActive)
            list.add((st.getValue, st.getColor))
        })
        targetPairs.setAll(list)
      } else if (c.wasRemoved()) {
        c.getRemoved.forEach(st => list.add((st.getValue, st.getColor)))
        targetPairs.removeAll(list)
      } else if (c.wasAdded()) {
        c.getAddedSubList.forEach(st => list.add((st.getValue, st.getColor)))
        targetPairs.addAll(list)
      }
    }
    listview.refresh()
  }


