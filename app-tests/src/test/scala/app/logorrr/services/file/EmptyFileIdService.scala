package app.logorrr.services.file

/**
 * Don't provide any FileId, for tests which don't need any file operations
 */
class EmptyFileIdService extends MockFileIdService(Seq())
