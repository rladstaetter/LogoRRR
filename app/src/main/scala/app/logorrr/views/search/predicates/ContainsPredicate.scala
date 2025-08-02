package app.logorrr.views.search.predicates

case class ContainsPredicate(pattern: String) extends DescriptivePredicate(pattern) {
  override def apply(searchTerm: String): Boolean = searchTerm.contains(pattern)
}
