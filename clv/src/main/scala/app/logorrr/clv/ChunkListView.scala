package app.logorrr.clv

import app.logorrr.clv.color.ColorChozzer
import javafx.application.Platform
import javafx.beans.binding.{Bindings, BooleanBinding, IntegerBinding}
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.collections.ObservableList
import javafx.geometry.Orientation
import javafx.scene.control.skin.VirtualFlow
import javafx.scene.control.{ListView, ScrollBar, Skin, SkinBase}
import net.ladstatt.util.log.CanLog

import java.lang
import scala.jdk.CollectionConverters.CollectionHasAsScala


object ChunkListView {

  val DefaultScrollBarWidth = 18

  def lookupVirtualFlow(skin: Skin[_]): Option[VirtualFlow[ChunkListCell[_]]] = {
    Option(skin match {
      case skinBase: SkinBase[_] =>
        skinBase.getChildren.asScala.find(_.getStyleClass.contains("virtual-flow")).orNull.asInstanceOf[VirtualFlow[ChunkListCell[_]]]
      case _ =>
        null
    })
  }

  def lookupScrollBar(flow: VirtualFlow[ChunkListCell[_]], orientation: Orientation): Option[ScrollBar] = {
    Option(flow.getChildrenUnmodifiable.toArray.collectFirst { case sb: ScrollBar if sb.getOrientation == orientation => sb }.orNull)
  }


}


/**
 * Each ListCell contains one or more Logentries - those regions are called 'Chunks'.
 *
 * Those chunks group LogEntries; this grouping serves no other purpose than to optimize painting
 * all entries via a ListView. In this way we get a stable and proven virtual flow implementation
 * under the hood and we don't have to reinvent this again.
 *
 * @param elements                   log entries to display
 * @param selectedLineNumberProperty which line is selected by the user
 * @param blockSizeProperty          size of blocks to display
 */
class ChunkListView[A](val elements: ObservableList[A]
                       , val selectedLineNumberProperty: SimpleIntegerProperty
                       , val blockSizeProperty: SimpleIntegerProperty
                       , firstVisibleTextCellIndexProperty: SimpleIntegerProperty
                       , lastVisibleTextCellIndexProperty: SimpleIntegerProperty
                       , selectInTextView: A => Unit
                       , logEntryVizor: Vizor[A]
                       , logEntryChozzer: ColorChozzer[A]
                       , logEntrySelector: ElementSelector[A])
  extends ListView[Chunk[A]] with CanLog {

  // width of Scrollbars
  val scrollBarWidthProperty = new SimpleIntegerProperty(ChunkListView.DefaultScrollBarWidth)

  def setScrollBarWidth(width: Int): Unit = scrollBarWidthProperty.set(width)

  // returns width - scrollbarwidth if it is > 0, else width
  val chunkListWidthProperty: IntegerBinding = Bindings.createIntegerBinding(
    () =>
      (if (widthProperty.get() - scrollBarWidthProperty.get() >= 0)
        widthProperty.get() - scrollBarWidthProperty.get()
      else
        widthProperty.get()
        ).toInt
    ,
    widthProperty, scrollBarWidthProperty)

  /**
   * What should happen if the scrollbar appears/vanishes
   */
  private val scrollBarVisibilityListener = new ChangeListener[lang.Boolean] {
    override def changed(observableValue: ObservableValue[_ <: lang.Boolean], t: lang.Boolean, isVisible: lang.Boolean): Unit = {
      if (isVisible) {
        setScrollBarWidth(ChunkListView.DefaultScrollBarWidth)
        recalculateAndUpdateItems()
      } else {
        setScrollBarWidth(0)
        recalculateAndUpdateItems()
      }
    }
  }

  // needed to get access to the scrollbar
  private val chunkListViewSkinListener: ChangeListener[Skin[_]] = (_: ObservableValue[_ <: Skin[_]], _: Skin[_], currentSkin: Skin[_]) => {
    for {skin <- Option(currentSkin)
         flow <- ChunkListView.lookupVirtualFlow(skin)
         horizontalScrollbar <- ChunkListView.lookupScrollBar(flow, Orientation.HORIZONTAL)
         verticalScrollbar <- ChunkListView.lookupScrollBar(flow, Orientation.VERTICAL)} {
      horizontalScrollbar.setVisible(false)
      verticalScrollbar.visibleProperty().addListener(scrollBarVisibilityListener)
    }
  }

  /**
   * If the observable list changes in any way, recalculate the items in the listview.
   */
  private val elementInvalidationListener = JfxUtils.mkInvalidationListener(_ => recalculateAndUpdateItems())

  /** if user selects a new active element, recalculate and implicitly repaint */
  //private val selectedRp = mkRecalculateAndUpdateItemListener("selected")
  private val anyRp: ChangeListener[java.lang.Boolean] = (_: ObservableValue[_ <: java.lang.Boolean], _: java.lang.Boolean, _: java.lang.Boolean) => {
    recalculateAndUpdateItems()
  }

  /** performance optimisation to debounce calls to the recalculation / repainting operation */
  var recalculateScheduled = false

  def init(): Unit = {
    getStylesheets.add(getClass.getResource("/app/logorrr/clv/ChunkListView.css").toExternalForm)

    setCellFactory((_: ListView[Chunk[A]]) => {
      new ChunkListCell(
        blockSizeProperty
        , selectInTextView
        , logEntryVizor
        , logEntryChozzer
        , logEntrySelector
        , chunkListWidthProperty)
    })

    addListeners()
  }

  def scrollToActiveChunk(): Unit = {
    if (!getItems.isEmpty) {
      val filteredChunks = getItems.filtered(c => {
        var found = false
        c.entries.forEach(e => if (found) found else {
          found = logEntryVizor.isSelected(e)
        })
        found
      })
      if (!filteredChunks.isEmpty) {
        Option(filteredChunks.get(0)) match {
          case Some(chunk) =>
            getSelectionModel.select(chunk)
            val relativeIndex = getItems.indexOf(chunk)
            getSelectionModel.select(relativeIndex)
            JfxUtils.scrollTo[Chunk[A]](this, chunk.height, relativeIndex)
          case None =>
        }
      }
    } else {
      logTrace("ListView[Chunk] is empty, not scrolling to active chunk.")
    }
  }

  /** invalidation listener has to be disabled when manipulating log entries (needed for setting the timestamp for example) */
  def addInvalidationListener(): Unit = elements.addListener(elementInvalidationListener)

  def removeInvalidationListener(): Unit = elements.removeListener(elementInvalidationListener)

  /** toggle needed such that change listener fires */
  var toggle = false

  // if any of the given properties change, recalculate this binding
  val anyPropProperty: BooleanBinding = Bindings.createBooleanBinding(() => {
    toggle = !toggle
    toggle
  },
    selectedLineNumberProperty
    , firstVisibleTextCellIndexProperty
    , lastVisibleTextCellIndexProperty
    , blockSizeProperty
    , widthProperty
    , heightProperty)

  private def addListeners(): Unit = {
    addInvalidationListener()
    anyPropProperty.addListener(anyRp)
    skinProperty().addListener(chunkListViewSkinListener)
  }


  def removeListeners(): Unit = {
    removeInvalidationListener()
    anyPropProperty.removeListener(anyRp)

    for {skin <- Option(getSkin)
         flow <- ChunkListView.lookupVirtualFlow(skin)
         verticalScrollbar <- ChunkListView.lookupScrollBar(flow, Orientation.VERTICAL)} {
      verticalScrollbar.visibleProperty().removeListener(scrollBarVisibilityListener)
    }

    skinProperty().removeListener(chunkListViewSkinListener)
  }


  /**
   * Recalculates elements of listview depending on width, height and blocksize.
   */
  def recalculateAndUpdateItems(): Unit = {
    if (!recalculateScheduled && widthProperty().get() > 0 && heightProperty.get() > 0 && blockSizeProperty.get() > 0) {
      recalculateScheduled = true
      Platform.runLater(() => {
        // println(s"recalculating ($ctx)> (width: ${widthProperty().get()}, blockSize: ${blockSizeProperty.get()}, height: ${heightProperty().get()})")
        //        println(s"elems.size: ${elements.size()}")
        Chunk.updateChunks[A](getItems, elements, blockSizeProperty.get(), chunkListWidthProperty.get(), heightProperty.get(), Chunk.ChunksPerVisibleViewPort)
        //setItems(chunks)
        recalculateScheduled = false
      })
    } else {
      // println(s"NOT recalculating ($ctx)> (width: ${widthProperty().get()}, blockSize: ${blockSizeProperty.get()}, height: ${heightProperty().get()})")
    }
  }
}


