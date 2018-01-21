package ch.awae.esgcal

import javax.swing.JFrame
import java.awt.FileDialog
import javax.swing.JFileChooser
import java.io.File

object Test extends App {

  val frame = new JFrame("test")
  frame.setVisible(true)

  val fd = new FileDialog(frame, "Choose a file", FileDialog.SAVE);
  fd.setFile("test.xml");
  fd.setVisible(true);
  val filename = fd.getFile();
  if (filename == null)
    System.out.println("You cancelled the choice");
  else
    System.out.println("You chose " + filename);
}

