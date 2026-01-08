package app.logorrr.views.search.stg

import app.logorrr.conf.SearchTerm


object SearchTermGroup {


}

case class SearchTermGroup(name: String, terms: Seq[SearchTerm])
