package ch.awae.esgcal

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileOutputStream
import ch.awae.esgcal.xssf.Workbook

object Test extends App {

  val book = new Workbook(new XSSFWorkbook)

  val sheet = book("sheet")

  sheet(0, 0) = "This text"
  sheet(1, 2) = true
  sheet(3, 4) = 12
  sheet(2, 3) = 1.2

  val fileOut = new FileOutputStream("workbook.xlsx")
  book.raw.write(fileOut)
  fileOut.close()

}