package app.logorrr.conf

import app.logorrr.conf.SearchTerm
import upickle.default.*

case class SearchTermGroup(name: String, terms: Seq[SearchTerm]) derives ReadWriter
