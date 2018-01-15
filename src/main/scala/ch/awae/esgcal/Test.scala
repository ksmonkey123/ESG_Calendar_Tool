package ch.awae.esgcal

import java.io.FileOutputStream

import ch.awae.esgcal.xssf.CellMagnet._
import ch.awae.esgcal.xssf.Workbook

import org.apache.poi.xssf.usermodel.XSSFWorkbook

object Test extends App {

  val book = Workbook.empty
  val sheet = book("sheet")

  sheet(0, 0) = "This text"
  sheet(1, 2) = true
  sheet(3, 4) = 1.2
  sheet(2, 3) = 12

  book.write("workbook.xlsx")

}