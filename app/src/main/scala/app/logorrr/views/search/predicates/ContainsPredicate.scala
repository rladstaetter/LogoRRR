package app.logorrr.views.search.predicates

case class ContainsPredicate(pattern: String) extends LabelledFunction(pattern) {
  override def apply(searchTerm: String): Boolean = searchTerm.contains(pattern)
}
