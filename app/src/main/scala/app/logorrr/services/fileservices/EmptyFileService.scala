package app.logorrr.services.fileservices

import java.nio.file.Path

class EmptyFileService extends LogoRRRFileOpenService {
  override def openFile: Option[Path] = None
}
