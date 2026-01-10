package app.logorrr.conf

import app.logorrr.conf.SearchTerm


object SearchTermGroup {


}

case class SearchTermGroup(name: String, terms: Seq[SearchTerm])
