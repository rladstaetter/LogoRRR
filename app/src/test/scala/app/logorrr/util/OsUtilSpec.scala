package app.logorrr.util

import net.ladstatt.util.os.OsUtil
import org.scalatest.wordspec.AnyWordSpec

class OsUtilSpec extends AnyWordSpec:

  "OsUtil" should:
    /** make sure security bookmarks are activated for build */
    "enable mac file" in:
      if OsUtil.isMac then
        assert(OsUtil.enableSecurityBookmarks)
