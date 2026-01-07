package app.logorrr.views.search.st
import app.logorrr.clv.JfxUtils
import app.logorrr.clv.color.ColorUtil
import app.logorrr.conf.FileId
import app.logorrr.util.HashUtil
import app.logorrr.views.a11y.{UiNode, UiNodeSearchTermAware}
import app.logorrr.views.search.{MutableSearchTerm, MutableSearchTermUnclassified}
import javafx.beans.{InvalidationListener, Observable}
import javafx.scene.control.ToggleButton
import javafx.scene.paint.Color
import net.ladstatt.util.log.CanLog


object SearchTermButton extends UiNodeSearchTermAware {

  override def uiNode(fileId: FileId, searchTerm: MutableSearchTerm): UiNode = UiNode(classOf[SearchTermButton].getSimpleName + "-" + HashUtil.md5Sum(fileId.absolutePathAsString + ":" + searchTerm.getPredicate.description))

}


/**
 * Displays a search term and triggers displaying the results.
 */
class SearchTermButton(val fileId: FileId
                       , val searchTerm: MutableSearchTerm
                       , hits: Int
                       , updateActiveSearchTerm: => Unit
                       , removeSearchTerm: MutableSearchTerm => Unit) extends ToggleButton with CanLog {

  val isUnclassified: Boolean = searchTerm.isInstanceOf[MutableSearchTermUnclassified]

  selectedProperty().addListener(new InvalidationListener {
    override def invalidated(observable: Observable): Unit = updateActiveSearchTerm
  })
  selectedProperty().addListener(JfxUtils.onNew[java.lang.Boolean](selected => {
    if (selected) {
      setStyle(ColorUtil.mkCssBackgroundString(searchTerm.getColor))
    } else {
      setStyle(ColorUtil.mkCssBackgroundString(Color.WHITESMOKE))
    }
  }))

  setId(SearchTermButton.uiNode(fileId, searchTerm).value)

  setGraphic(SearchTermVis(fileId, hits, searchTerm, isUnclassified, removeSearchTerm))

  setSelected(searchTerm.isActive)



}
