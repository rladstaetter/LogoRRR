package app.logorrr.conf.mut

import app.logorrr.views.SearchTerm
import javafx.beans.property.SimpleMapProperty
import javafx.collections.{FXCollections, MapChangeListener, ObservableList}

import java.util
import scala.jdk.CollectionConverters._

class MutSearchTermSettings {

  private val searchTermMapping = new SimpleMapProperty[String, Seq[SearchTerm]](FXCollections.observableMap(new util.HashMap()))

  val searchTermGroupNames: ObservableList[String] = FXCollections.observableArrayList(searchTermMapping.keySet().asScala.toSeq: _*)

  searchTermMapping.addListener(new MapChangeListener[String, Seq[SearchTerm]] {
    override def onChanged(change: MapChangeListener.Change[_ <: String, _ <: Seq[SearchTerm]]): Unit = {
      searchTermGroupNames.setAll(searchTermMapping.keySet().asScala.toSeq: _*)
    }
  })

  def put(groupName: String, searchTerms: Seq[SearchTerm]): Unit = searchTermMapping.put(groupName, searchTerms)

  def get(groupName: String): Option[Seq[SearchTerm]] = Option(searchTermMapping.get(groupName))

  def mkImmutable(): Map[String, Seq[SearchTerm]] = searchTermMapping.asScala.toMap

  def remove(groupName: String): Unit = searchTermMapping.remove(groupName)
}
