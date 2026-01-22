package org.arnoldc

import org.arnoldc.ast._

// Simplified Declaimer without speech dependencies
// Original uses FreeTTS for audio output
object Declaimer {
  
  def declaim(root: RootNode, outputFile: String): Unit = {
    println("Audio declamation requires FreeTTS library.")
    println("Use the original ArnoldC for -declaim support.")
    println("This fork focuses on native code generation.")
  }
}
