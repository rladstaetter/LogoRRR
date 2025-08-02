package app.logorrr.views.search.predicates

import java.util.regex.Pattern

case class RegexPredicate(pattern: String) extends DescriptivePredicate(pattern) {
  val compiledPattern: Pattern = Pattern.compile(pattern)

  override def apply(searchTerm: String): Boolean = compiledPattern.matcher(searchTerm).find()

}
