package app.logorrr.services.file

import app.logorrr.conf.FileId

/**
 * Trait to inject either Test FileId Services or query the native dialog
 */
trait FileIdService:

  /** returns either None or a valid FileId */
  def provideFileId: Option[FileId]

