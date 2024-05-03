package app.logorrr.services.fileservices

import java.nio.file.Path

trait LogoRRRFileOpenService {

  def openFile: Option[Path]

}
