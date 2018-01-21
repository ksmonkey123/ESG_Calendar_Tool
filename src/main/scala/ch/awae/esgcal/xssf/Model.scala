package ch.awae.esgcal.xssf

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFCell
import scala.reflect.ClassTag
import java.io.OutputStream
import ch.awae.esgcal.Implicit.UseCloseable
import java.io.FileOutputStream
import org.apache.poi.openxml4j.opc.OPCPackage
import java.io.File
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator
import ch.awae.esgcal.export.Entry

class Decorated[T](val raw: T)

class Workbook private (private val workbook: XSSFWorkbook) extends Decorated(workbook) {
  // get sheet by index
  def sheet(index: Int) = apply(index)
  def apply(index: Int) = {
    try {
      Some(new Sheet(workbook.getSheetAt(index)))
    } catch {
      case _: IllegalArgumentException => None
    }
  }
  // get or create sheet by title
  def sheet(title: String) = apply(title)
  def apply(title: String) = new Sheet(Option(workbook.getSheet(title)).getOrElse(workbook.createSheet(title)))
  // write the workbook
  def write(file: String): Unit = write(new FileOutputStream(file))
  def write(stream: OutputStream): Unit = stream use workbook.write
  // evaluate all formulas
  def evaluateAll = XSSFFormulaEvaluator.evaluateAllFormulaCells(workbook)
}

object Workbook {
  def from(workbook: XSSFWorkbook) = new Workbook(workbook)
  def empty = new Workbook(new XSSFWorkbook)
  def from(file: String) = {
    val wb = new XSSFWorkbook(new File(file))
    new Workbook(wb)
  }
  def fromResource(file: String) = new Workbook(new XSSFWorkbook(classOf[Workbook].getClassLoader.getResourceAsStream(file)))
}

class Sheet(private val sheet: XSSFSheet) extends Decorated(sheet) {
  def cell(row: Int, col: Int) = apply(row, col)
  def apply(row: Int, col: Int) = {
    val _row = Option(sheet.getRow(row)).getOrElse(sheet.createRow(row))
    new Cell(Option(_row.getCell(col)).getOrElse(_row.createCell(col)))
  }
  def update[T: CellWriteMagnet](row: Int, col: Int, value: T) = apply(row, col).set(value)
  def process(entry: Entry[_]) = entry.process(this)
}

class Cell(private val cell: XSSFCell) extends Decorated(cell) {
  def apply[T: CellReadMagnet] = get
  def get[T: CellReadMagnet] = implicitly[CellReadMagnet[T]].read(cell)
  def set[T: CellWriteMagnet](value: T) = {
    val row = cell.getRowIndex
    val col = cell.getColumnIndex
    val sheet = cell.getSheet.getSheetName
    println(s"$sheet($col,$row) = $value")
    implicitly[CellWriteMagnet[T]].write(cell, value)
  }

}