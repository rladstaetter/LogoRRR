package app.logorrr.services.file

import app.logorrr.io.FileId

/**
 * Trait to inject either Test File Services or the service which calls the native dialog
 */
trait FileService {

  /** returns either None or a valid FileId */
  def openFile: Option[FileId]

}
