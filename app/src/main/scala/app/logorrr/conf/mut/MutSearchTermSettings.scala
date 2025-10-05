package app.logorrr.conf.mut

import app.logorrr.views.search.SearchTerm
import app.logorrr.views.search.stg.StgEntry
import javafx.beans.property.SimpleMapProperty
import javafx.collections.{FXCollections, MapChangeListener, ObservableList}

import java.util
import scala.jdk.CollectionConverters._

class MutSearchTermSettings {

  private val searchTermMapping: SimpleMapProperty[String, Seq[SearchTerm]] = new SimpleMapProperty[String, Seq[SearchTerm]](FXCollections.observableMap(new util.HashMap()))

  val searchTermGroupNames: ObservableList[String] = FXCollections.observableArrayList(searchTermMapping.keySet().asScala.toSeq.sorted: _*)

  def toObservableList(mapping: SimpleMapProperty[String, Seq[SearchTerm]]): util.List[StgEntry] = mapping.entrySet().asScala.map(e => StgEntry(e.getKey, e.getValue)).toSeq.sortBy(_.name).asJava

  val searchTermGroupEntries: ObservableList[StgEntry] = FXCollections.observableArrayList(toObservableList(searchTermMapping))

  searchTermMapping.addListener(new MapChangeListener[String, Seq[SearchTerm]] {
    override def onChanged(change: MapChangeListener.Change[_ <: String, _ <: Seq[SearchTerm]]): Unit = {
      searchTermGroupNames.setAll(searchTermMapping.keySet().asScala.toSeq.sorted: _*)
      searchTermGroupEntries.setAll(toObservableList(searchTermMapping))
    }
  })

  def put(groupName: String, searchTerms: Seq[SearchTerm]): Unit = searchTermMapping.put(groupName, searchTerms)

  def get(groupName: String): Option[Seq[SearchTerm]] = Option(searchTermMapping.get(groupName))

  def mkImmutable(): Map[String, Seq[SearchTerm]] = searchTermMapping.asScala.toMap

  def remove(groupName: String): Unit = searchTermMapping.remove(groupName)
}
