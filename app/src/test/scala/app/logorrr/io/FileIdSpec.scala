package app.logorrr.io

import app.logorrr.conf.FileId
import org.scalatest.wordspec.AnyWordSpec

class FileIdSpec extends AnyWordSpec {

  "reduceZipFiles" in {
    val fileIds = Seq(FileId("a.zip@1"), FileId("a.zip@2"), FileId("a.zip@3"), FileId("b.zip@1"))
    val res = FileId.reduceZipFiles(fileIds)
    assert(res.size == 2)
    assert(res(FileId("a.zip")) == Seq(FileId("a.zip@1"), FileId("a.zip@2"), FileId("a.zip@3")))
    assert(res(FileId("b.zip")) == Seq(FileId("b.zip@1")))
  }
  "zipentrypath" in {
    val id = FileId("real/path/a.zip@an/entry/file.log")
    assert(id.isZipEntry)
    assert(id.zipEntryPath == "a.zip@an/entry/file.log")
  }
  "zippath" in {
    val id = FileId("real/path/a.zip@an/entry/file.log")
    assert(id.extractZipFileId.fileName == "a.zip")
  }
}
