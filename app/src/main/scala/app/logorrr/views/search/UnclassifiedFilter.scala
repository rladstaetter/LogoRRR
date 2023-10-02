package app.logorrr.views.search

class UnclassifiedFilter(filters: Set[Filter]) extends
  Filter("Unclassified", Filter.unClassifiedFilterColor, true) {

  override def matches(searchTerm: String): Boolean = !filters.exists(_.matches(searchTerm))

}
