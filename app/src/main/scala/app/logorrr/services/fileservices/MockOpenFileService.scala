package app.logorrr.services.fileservices

import java.nio.file.Path

class MockOpenFileService(somePath: Option[Path]) extends LogoRRRFileOpenService {
  override def openFile: Option[Path] = somePath
}
