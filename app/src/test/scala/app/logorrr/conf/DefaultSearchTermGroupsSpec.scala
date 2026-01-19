package app.logorrr.conf

import org.scalatest.wordspec.AnyWordSpec
import upickle.default.*

class DefaultSearchTermGroupsSpec extends AnyWordSpec:

  "load from cp" in :
    val d = DefaultSearchTermGroups()
    assert(d != null)

  "roundTrip" in :
    val orig = DefaultSearchTermGroups()
    val read = DefaultSearchTermGroups(write(orig))
    assert(orig == read)

