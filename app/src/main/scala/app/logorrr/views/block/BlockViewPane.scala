package app.logorrr.views.block

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.model.LogEntry
import app.logorrr.util.{CanLog, JfxUtils}
import app.logorrr.views.search.Filter
import javafx.beans.property.{SimpleIntegerProperty, SimpleListProperty, SimpleObjectProperty}
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.collections.{FXCollections, ListChangeListener, ObservableList}
import javafx.scene.control._
import javafx.scene.layout.VBox

import scala.collection.mutable.ListBuffer


class BlockViewPane(pathAsString: String)
  extends ScrollPane
    with HasBlockSizeProperty
    with CanLog {

  /** vertical box which holds single SQViews and serves as a canvas for painting */
  private val vbox = new VBox()
  private val repaintListener: ListChangeListener[LogEntry] = (_: ListChangeListener.Change[_ <: LogEntry]) => repaint()

  val selectedElemProperty = {
    val sp = new SimpleObjectProperty[LogEntry]()
    sp
  }

  private val entriesProperty = {
    val es = new SimpleListProperty[LogEntry](FXCollections.observableArrayList())
    es.addListener(repaintListener)
    es
  }

  val filtersProperty = new SimpleListProperty[Filter]()

  override val blockSizeProperty: SimpleIntegerProperty = {
    val p = new SimpleIntegerProperty()
    p.addListener(_ => repaint())
    p
  }

  init()

  private def init(): Unit = {
    selectedElemProperty.addListener(new ChangeListener[LogEntry] {
      override def changed(observable: ObservableValue[_ <: LogEntry], oldValue: LogEntry, newLogEntry: LogEntry): Unit = {
        LogoRRRGlobals.getLogFileSettings(pathAsString).selectedLineNumber(newLogEntry.lineNumber)
      }
    })

    widthProperty().addListener(JfxUtils.onNew[Number](_ => repaint()))

    setContent(vbox)
  }


  def setCanvasWidth(value: Double): Unit = {
    super.setWidth(value)
  }

  def getEntriesSize(): Int = entriesProperty.size()

  def setEntries(es: ObservableList[LogEntry]): Unit = {
    entriesProperty.setValue(es)
  }


  def setSelectedElem(elem: LogEntry): Unit = selectedElemProperty.set(elem)

  private def mkBlockView(): BlockView = {
    val blockView = new BlockView
    blockView.bind(filtersProperty,blockSizeProperty, widthProperty, setSelectedElem)
    blockView
  }

  def repaint(): Unit = {
    if (isVisible) {
      // unbind old listeners or we have a memory problem
      vbox.getChildren.forEach {
        case c: BlockView => c.unbind()
        case _ =>
      }

      val virtualHeight: Int =
        BlockView.calcVirtualHeight(
          getBlockSize()
          , getBlockSize()
          , getWidth.toInt
          , getEntriesSize())
      /*
        assert(getBlockSize() > 0)
        assert(getWidth.toInt > 0, getWidth)
        */
      if (getBlockSize() > 0 && getWidth.toInt > 0) {
        val blockViews: Seq[BlockView] = {
          // if virtual canvas height is lower than maxheight, just create one sqView and be done with it
          if (virtualHeight <= BlockImage.MaxHeight) {
            val blockView = mkBlockView()
            blockView.setWidth(getWidth.toInt)
            blockView.setHeight(virtualHeight)
            blockView.setEntries(entriesProperty)
            Seq(blockView)
          } else {
            // if the virtual canvas height exceeds SQImage.MaxHeight, iterate and create new SQViews
            val nrOfElemsInRow = (getWidth.toInt / blockSizeProperty.get()).toInt
            val nrOfRowsPerSquareView = (BlockImage.MaxHeight / blockSizeProperty.get()).toInt
            val nrElemsInSqView = nrOfRowsPerSquareView * nrOfElemsInRow
            var curIndex = 0
            val lb = new ListBuffer[BlockView]

            while (curIndex < getEntriesSize()) {
              val v = mkBlockView()
              v.setWidth(getWidth.toInt)
              val end = if (curIndex + nrElemsInSqView < getEntriesSize()) {
                curIndex + nrElemsInSqView
              } else {
                getEntriesSize()
              }
              val blockViewEntries = entriesProperty.subList(curIndex, end)
              v.setEntries(blockViewEntries)
              v.setHeight(BlockView.calcVirtualHeight(getBlockSize(), getBlockSize(), getWidth.toInt, blockViewEntries.size))
              lb.addOne(v)
              curIndex = curIndex + nrElemsInSqView
            }
            lb.toSeq
          }
        }

        vbox.getChildren.setAll(blockViews: _*)
        // logTrace(s"Redraw ${blockViews.size} BlockViews")
        blockViews.foreach(_.repaint())
      } else {
        logWarn(s"Blocksize: ${getBlockSize()}, getWidth: ${getWidth} ")
      }
    } else {
      // logTrace("invisible ...")
    }
    ()
  }


  def scrollToEnd(): Unit = setVvalue(getVmax)


}