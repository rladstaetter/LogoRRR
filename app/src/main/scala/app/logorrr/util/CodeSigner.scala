package app.logorrr.util

import java.nio.file.{Files, Path, Paths}
import scala.jdk.CollectionConverters._

/**
 * Will be called in OSX Installer module
 */
object CodeSigner {

  def main(args: Array[String]): Unit = {
    if (args.length != 4) {
      System.out.println("CodeSigner <developerId> <codesign> <entitlements> <path to config>")
    } else {
      val developerId = args(0)
      val codesign = Paths.get(args(1))
      val entitlements = Paths.get(args(2))
      val file = Paths.get(args(3)).toAbsolutePath
      Files.readAllLines(file).asScala.map(f => sign(codesign, entitlements, developerId, file.getParent.resolve(f)))
    }
  }

  def sign(codesign: Path
           , entitlements: Path
           , developerId: String
           , value: Path): Path = {

    val cmds = Seq(codesign.toAbsolutePath.toString
      , "--timestamp"
      , "--options"
      , "runtime"
      , "--entitlements"
      , entitlements.toAbsolutePath.toString
      , "--deep"
      , "-vvv"
      , "-f"
      , "--sign"
      , developerId
      , value.toAbsolutePath.toString
    )
    Commander.execCmd(value.getParent, cmds)
    value
  }

}
