package app.logorrr.conf

import app.logorrr.conf.{SearchTerm, SearchTermGroup}
import upickle.default.*

case class SearchTermGroup(terms: Seq[SearchTerm], selected: Boolean) derives ReadWriter
