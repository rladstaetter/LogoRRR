package app.logorrr.views.visual.sivr


import app.logorrr.util.{CanLog, JfxUtils}
import app.logorrr.views.visual.SquareImageView
import javafx.beans.property.{SimpleDoubleProperty, SimpleListProperty}
import javafx.beans.{InvalidationListener, Observable}
import javafx.collections.{FXCollections, ListChangeListener}
import javafx.scene.control._
import javafx.scene.layout.VBox

import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters._


object SquareImageViz {


}

class SquareImageViz extends ScrollPane with CanLog {

  /** vertical box which holds single SQViews and serves as a canvas for painting */
  val vbox = new VBox()

  private val listChangeListener: ListChangeListener[SQView.E] = (c: ListChangeListener.Change[_ <: SQView.E]) => calcSquareViews()

  val entries = {
    val es = new SimpleListProperty[SQView.E](FXCollections.observableArrayList())
    es.addListener(listChangeListener)
    es
  }

  def setEntries(es: Seq[SQView.E]): Unit = {
    entries.setAll(es.asJava)
  }

  private val recalcSqViewsListener: InvalidationListener = (observable: Observable) => {
    calcSquareViews()
  }

  val blockSizeProperty = {
    val p = new SimpleDoubleProperty(5)
    p.addListener(recalcSqViewsListener)
    p
  }

  widthProperty().addListener(recalcSqViewsListener)

  def setBlockSize(blockSize: Int): Unit = blockSizeProperty.set(blockSize)

  def getBlockSize(): Int = blockSizeProperty.get().toInt




  /**
   * one (most of the time) or more squareviews which visualize entries
   *
   * there has to be a list of squareviews since there is a limitation on how big a squareview (its backing
   * image) can get (4096 * 4096 is the maximum). This is the reason why width of SquareImageViz is constrained
   * to 4096.
   * */
  // val squareViews = new SimpleListProperty[SQView](FXCollections.observableArrayList())

  def mkSQView(): SQView = {
    val squareView = new SQView
    squareView.bind(blockSizeProperty, widthProperty)
    squareView
  }


  private def calcSquareViews(): Unit = {
    // unbind old listeners or we have a memory problem
    vbox.getChildren.forEach {
      case c: SQView => c.unbind()
      case _ => ???
    }

    val virtualCanvasHeight: Int =
      SQView.calcVirtualCanvasHeight(
        blockSizeProperty.get().toInt
        , blockSizeProperty.get().toInt
        , getWidth.toInt
        , entries.size)

    val sqViews: Seq[SQView] = {
      // if virtual canvas height is lower than maxheight, just create one sqView and be done with it
      if (virtualCanvasHeight <= SQImage.MaxHeight) {
        val sqView = mkSQView()
        sqView.setHeight(virtualCanvasHeight)
        sqView.setWidth(getWidth().toInt)
        Seq(sqView)
      } else {
        // if the virtual canvas height exceeds SQImage.MaxHeight, iterate and create new SQViews
        val nrOfElemsInRow = (getWidth.toInt / blockSizeProperty.get()).toInt
        val nrOfRowsPerSquareView = (SQImage.MaxHeight / blockSizeProperty.get()).toInt
        val nrElemsInSqView = nrOfRowsPerSquareView * nrOfElemsInRow
        var curIndex = 0
        val lb = new ListBuffer[SQView]

        while (curIndex < entries.size) {
          val v = mkSQView()
          v.setWidth(getWidth().toInt)
          val end = if (curIndex + nrElemsInSqView < entries.size) {
            curIndex + nrElemsInSqView
          } else {
            entries.size
          }
          val entriesInSquareView = entries.subList(curIndex, end)
          v.setEntries(entriesInSquareView)
          v.setHeight(SQView.calcVirtualCanvasHeight(getBlockSize(), getBlockSize(), getWidth.toInt, entriesInSquareView.size))
          lb.addOne(v)
          curIndex = curIndex + nrElemsInSqView
        }
        lb.toSeq
      }
    }
    vbox.getChildren.setAll(sqViews: _*)
    logTrace(s"Redraw ${sqViews.size} SquareViews")
    sqViews.foreach(_.redraw())
    ()
  }


  setContent(vbox)
}