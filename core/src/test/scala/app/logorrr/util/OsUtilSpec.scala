package app.logorrr.util

import org.scalatest.wordspec.AnyWordSpec

class OsUtilSpec extends AnyWordSpec {

  "OsUtil" should {
    /** make sure security bookmarks are activated for build */
    "enable mac file" in {
      if (OsUtil.isMac) {
        assert(OsUtil.enableSecurityBookmarks)
      }
    }
  }
}
