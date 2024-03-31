package app.logorrr.util

import app.logorrr.io.FileId

import java.security.MessageDigest

object HashUtil {

  def md5Sum(input: String): String = {
    // Create MessageDigest instance for MD5
    val md = MessageDigest.getInstance("MD5")
    // Add input string bytes to digest
    md.update(input.getBytes)
    // Get the MD5 hash
    val hashBytes = md.digest
    // Convert byte array to hex string
    val sb = new StringBuilder
    for (b <- hashBytes) {
      sb.append(String.format("%02x", b))
    }
    sb.toString
  }

  def md5Sum(fileId: FileId): String = HashUtil.md5Sum(fileId.value)

}