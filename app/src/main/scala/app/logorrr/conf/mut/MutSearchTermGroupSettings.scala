package app.logorrr.conf.mut

import app.logorrr.conf.SearchTerm
import app.logorrr.views.search.stg.SearchTermGroup
import javafx.beans.property.SimpleMapProperty
import javafx.collections.{FXCollections, MapChangeListener, ObservableList}

import java.util
import scala.jdk.CollectionConverters._

object MutSearchTermGroupSettings:

  def toObservableList(mapping: SimpleMapProperty[String, Seq[SearchTerm]]): util.List[SearchTermGroup] = mapping.entrySet().asScala.map(e => SearchTermGroup(e.getKey, e.getValue)).toSeq.sortBy(_.name).asJava


class MutSearchTermGroupSettings:

  private val searchTermMapping: SimpleMapProperty[String, Seq[SearchTerm]] = new SimpleMapProperty[String, Seq[SearchTerm]](FXCollections.observableMap(new util.HashMap()))

  val searchTermGroupNames: ObservableList[String] = FXCollections.observableArrayList(searchTermMapping.keySet().asScala.toSeq.sorted*)

  val searchTermGroupEntries: ObservableList[SearchTermGroup] = FXCollections.observableArrayList(MutSearchTermGroupSettings.toObservableList(searchTermMapping))

  searchTermMapping.addListener(new MapChangeListener[String, Seq[SearchTerm]] {
    override def onChanged(change: MapChangeListener.Change[? <: String, ? <: Seq[SearchTerm]]): Unit = {
      searchTermGroupNames.setAll(searchTermMapping.keySet().asScala.toSeq.sorted*)
      searchTermGroupEntries.setAll(MutSearchTermGroupSettings.toObservableList(searchTermMapping))
    }
  })

  def put(groupName: String, searchTerms: Seq[SearchTerm]): Unit = searchTermMapping.put(groupName, searchTerms)

  def get(groupName: String): Option[Seq[SearchTerm]] = Option(searchTermMapping.get(groupName))

  def remove(groupName: String): Unit = searchTermMapping.remove(groupName)

  def mkImmutable(): Map[String, Seq[SearchTerm]] = searchTermMapping.asScala.toMap

  def clear(): Unit = searchTermMapping.clear()


