package app.logorrr.conf

import upickle.default.*

import java.io.File

object SettingsMigrator {

  def migrate(jsonString: String): String = {
    write(migrate(ujson.read(jsonString)), indent = 2)
  }

  // Recursive function to turn all kebab-keys into camelCase keys
  private def migrate(js: ujson.Value): ujson.Value = js match {
    case obj: ujson.Obj =>
      val newObj = ujson.Obj()
      for ((k, v) <- obj.value) {
        // Convert "file-settings" to "fileSettings"
        // but spare keys which are in fact file paths
        // hacky solution ...
        if (k.contains("-") && !k.contains(File.separator)) {
          val newKey = k.split("-").toList match {
            case head :: tail => head + tail.map(_.capitalize).mkString
            case Nil => k
          }
          newObj(newKey) = migrate(v)
        } else {
          newObj(k) = migrate(v)
        }
      }
      newObj
    case arr: ujson.Arr => ujson.Arr
      (arr.value.map(migrate))
    case _ => js
  }

}
