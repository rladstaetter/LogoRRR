package app.logorrr.services.fileservices

import java.nio.file.Path

class OpenSingleFileService(somePath: Option[Path]) extends LogoRRRFileOpenService {
  override def openFile: Option[Path] = somePath
}

/**
 * Given a list of files, it returns each file in order, and if the last file is reached, a
 * `None`
 *
 * @param files which this service is returning
 */
class OpenMultipleFilesService(files: Seq[Path]) extends LogoRRRFileOpenService {

  private val it = files.iterator

  override def openFile: Option[Path] = {
    if (it.hasNext) {
      Option(it.next())
    } else None
  }

}