package app.logorrr.conf

import app.logorrr.cp.TxtCp
import net.ladstatt.util.log.TinyLog
import upickle.default.*

import scala.util.{Failure, Success}

object DefaultSearchTermGroups extends TinyLog:

  var cpResource = TxtCp("/app/logorrr/conf/default-search-term-groups.json")

  def apply(): DefaultSearchTermGroups = apply(cpResource)

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
 * @param searchTermGroups search term groups
 */
case class DefaultSearchTermGroups(searchTermGroups: Seq[SearchTermGroup]) derives ReadWriter:
  val empty: SearchTermGroup = searchTermGroups.head
