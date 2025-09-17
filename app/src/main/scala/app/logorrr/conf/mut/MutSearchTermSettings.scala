package app.logorrr.conf.mut

import app.logorrr.views.SearchTerm
import javafx.beans.property.SimpleMapProperty
import javafx.collections.FXCollections

import java.util
import scala.jdk.CollectionConverters._

class MutSearchTermSettings {

  private val searchTermMapping = new SimpleMapProperty[String, Seq[SearchTerm]](FXCollections.observableMap(new util.HashMap()))

  def keySet(): Set[String] = searchTermMapping.keySet().asScala.toSet

  def put(key: String, searchTerms: Seq[SearchTerm]): Unit = searchTermMapping.put(key, searchTerms)

  def mkImmutable(): Map[String, Seq[SearchTerm]] = searchTermMapping.asScala.toMap
}
