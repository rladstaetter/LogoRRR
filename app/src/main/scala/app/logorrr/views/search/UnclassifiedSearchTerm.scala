package app.logorrr.views.search

import app.logorrr.conf.SearchTerm
import javafx.scene.paint.Color

import java.util.function.Predicate

/**
 * Match everything except given filters
 *
 * @param otherPredicates a set of filters to build the complement
 */
case class UnclassifiedSearchTerm(otherPredicates: Set[Predicate[String]]) extends MutableSearchTerm:

  setActive(true)

  override def getColor = SearchTerm.Unclassified

  // is true if none of the other match
  override def test(logLine: String): Boolean = !otherPredicates.foldRight(false)((p, acc) => acc || p.test(logLine))

  override def getSearchTermAsString: String = "Unclassfied"
