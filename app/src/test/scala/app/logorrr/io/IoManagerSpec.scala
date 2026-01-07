package app.logorrr.io

import app.logorrr.LogoRRRNative
import org.scalatest.wordspec.AnyWordSpec

import java.nio.file.Paths

class IoManagerSpec extends AnyWordSpec {

  LogoRRRNative.loadNativeLibraries()

  "read ziputil-simple.zip" in {
    val res = IoManager.unzip(Paths.get("src/test/resources/app/logorrr/io/ziputil-simple.zip"))
    assert(res.size == 1)
    val (fileId, entries) = res.toSeq.head
    assert(fileId.fileName.endsWith("simple.log"))
    assert(entries.size == 1)
    assert(entries.get(0).value.startsWith("""MSI (c) (94:5C)"""))
  }
  "read ziputil-bit-more-complex.zip" in {
    val res = IoManager.unzip(Paths.get("src/test/resources/app/logorrr/io/ziputil-bit-more-complex.zip"))
    assert(res.size == 3)
  }
}
