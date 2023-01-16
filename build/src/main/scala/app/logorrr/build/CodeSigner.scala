package app.logorrr.build

import java.nio.file.{Files, Path, Paths}
import scala.jdk.CollectionConverters._

/**
 * Will be called in OSX Installer module
 */
object CodeSigner {

  def main(args: Array[String]): Unit = {
    if (args.length != 4) {
      Console.println("CodeSigner <developerId> <codesign> <entitlements> <path to config>")
    } else {
      val developerId = args(0)
      val codesign = Paths.get(args(1))
      val entitlements = Paths.get(args(2))
      val file = Paths.get(args(3)).toAbsolutePath
      if (Files.exists(file)) {
        Files.readAllLines(file).asScala.map(f => sign(codesign, entitlements, developerId, file.getParent.resolve(f)))
      } else {
        Console.println(s"${file.toAbsolutePath} does not exist, could not execute signing operation ...")
      }
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
