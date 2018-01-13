package ch.awae.esgcal.xssf

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFCell
import scala.reflect.ClassTag

class Decorated[T](val raw: T)

class Workbook(private val workbook: XSSFWorkbook) extends Decorated(workbook) {
  def apply(index: Int) = new Sheet(workbook.getSheetAt(index))
  def apply(title: String) = new Sheet(Option(workbook.getSheet(title)).getOrElse(workbook.createSheet(title)))
}

class Sheet(private val sheet: XSSFSheet) extends Decorated(sheet) {
  def apply(row: Int, col: Int) = {
    val _row = Option(sheet.getRow(row)).getOrElse(sheet.createRow(row))
    new Cell(Option(_row.getCell(col)).getOrElse(_row.createCell(col)))
  }
  def update[T: CellMagnet](row: Int, col: Int, value: T) = apply(row, col).set(value)
}

class Cell(private val cell: XSSFCell) extends Decorated(cell) {
  def apply[T: CellMagnet] = get
  def get[T: CellMagnet] = implicitly[CellMagnet[T]].read(cell)
  def set[T: CellMagnet](value: T) = implicitly[CellMagnet[T]].write(cell, value)
}