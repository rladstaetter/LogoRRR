package app.logorrr.model

import app.logorrr.conf.{FileId, SearchTerm}

case class FileIdDividerSearchTerm(fileId: FileId, terms: Seq[SearchTerm], dividerPosition: Double)
