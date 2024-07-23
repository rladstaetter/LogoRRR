package app.logorrr.services.file

import app.logorrr.io.FileId

/**
 * Given a list of files, it returns each file in order, and if the last file is reached, a
 * `None`
 *
 * @param files which this service is returning
 */
class MockFileService(files: Seq[FileId]) extends FileService {

  private val it = files.iterator

  override def openFile: Option[FileId] = {
    if (it.hasNext) {
      Option(it.next())
    } else None
  }

}
