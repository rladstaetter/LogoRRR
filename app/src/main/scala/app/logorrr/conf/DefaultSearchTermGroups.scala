package app.logorrr.conf

import app.logorrr.cp.TxtCp
import javafx.scene.paint.Color
import net.ladstatt.util.log.TinyLog
import upickle.default.*

import scala.util.{Failure, Success}

object DefaultSearchTermGroups extends TinyLog:
/*
  private val EmptyGroup: SearchTermGroup = SearchTermGroup("empty", Seq())

  val JavaLoggingGroup: SearchTermGroup = SearchTermGroup("default", Seq(
    SearchTerm("FINEST", Color.GREY, active = true)
    , SearchTerm("INFO", Color.GREEN, active = true)
    , SearchTerm("WARNING", Color.ORANGE, active = true)
    , SearchTerm("SEVERE", Color.RED, active = true)
  ))

  val fallback = DefaultSearchTermGroups(Seq(EmptyGroup, JavaLoggingGroup))
*/
  def apply(): DefaultSearchTermGroups =
    apply(TxtCp("/app/logorrr/conf/default-search-term-groups.json"))

  def apply(txtCp: TxtCp): DefaultSearchTermGroups =
    txtCp.asString() match
      case Failure(exception) =>
        logException("Could not read default search term groups from classpath, using fallback", exception)
        DefaultSearchTermGroups(Seq())
      case Success(value) =>
        apply(value)

  def apply(asString: String): DefaultSearchTermGroups =
    read[DefaultSearchTermGroups](asString)

/**
 * Container for default settings which are provided as classpath resource
 *
 * @param searchTermGroups
 */
case class DefaultSearchTermGroups(searchTermGroups: Seq[SearchTermGroup]) derives ReadWriter:
  val empty: SearchTermGroup = searchTermGroups.head