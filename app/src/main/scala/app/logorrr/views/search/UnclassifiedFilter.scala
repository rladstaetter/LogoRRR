package app.logorrr.views.search

/**
 * Match everything except given filters
 *
 * @param filters a set of filters to build the complement
 */
case class UnclassifiedFilter(filters: Set[Filter]) extends
  Filter("Unclassified", Filter.unClassifiedFilterColor, true) {

  override def matches(searchTerm: String): Boolean = !filters.exists(_.matches(searchTerm))

}
