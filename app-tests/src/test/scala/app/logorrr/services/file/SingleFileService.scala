package app.logorrr.services.file

import app.logorrr.io.FileId

/**
 * Service which always opens given file
 *
 * @param fileId file reference to open
 */
class SingleFileService(fileId: FileId) extends MockFileService(Seq(fileId))
