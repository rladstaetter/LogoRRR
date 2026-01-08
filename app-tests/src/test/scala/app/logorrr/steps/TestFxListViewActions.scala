package app.logorrr.steps

import app.logorrr.clv.ChunkListCell
import app.logorrr.conf.FileId
import app.logorrr.model.LogEntry
import app.logorrr.usecases.TestFxBaseApplicationTest
import app.logorrr.views.logfiletab.LogoRRRChunkListView

trait TestFxListViewActions:
  self: TestFxBaseApplicationTest =>

  def lookupChunkListView(fileId: FileId): LogoRRRChunkListView =
    lookup(LogoRRRChunkListView.uiNode(fileId).ref).query[LogoRRRChunkListView]

  def nthCell(clv: LogoRRRChunkListView, cellIndex: Int): ChunkListCell[LogEntry] =
    from(clv).lookup(".list-cell").nth(cellIndex).query[ChunkListCell[LogEntry]]()



