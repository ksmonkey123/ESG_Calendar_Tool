package ch.awae.esgcal.agent

import java.awt.FileDialog
import javax.swing.JFrame

object SaveFileAgent {

  def getFile(file: String, frame: JFrame): Option[String] = {
    val fd = new FileDialog(frame, "Choose a file", FileDialog.SAVE)
    fd.setFile(file)
    fd.setVisible(true)
    val filename = fd.getFile()
    if (filename == null)
      None
    else
      Some(fd.getDirectory + filename)
  }

}