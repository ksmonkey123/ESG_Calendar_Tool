package ch.awae.esgcal.xssf

import org.apache.poi.xssf.usermodel.XSSFCell
import scala.util.Try

trait CellMagnet[T] {
  def read(cell: XSSFCell): Option[T]
  def write(cell: XSSFCell, value: T): Unit
}

object CellMagnet {
  implicit object StringMagnet extends CellMagnet[String] {
    def read(cell: XSSFCell): Option[String] = Try { cell.getStringCellValue }.toOption
    def write(cell: XSSFCell, value: String) = cell.setCellValue(value)
  }

  implicit object BooleanMagnet extends CellMagnet[Boolean] {
    def read(cell: XSSFCell): Option[Boolean] = Try { cell.getBooleanCellValue }.toOption
    def write(cell: XSSFCell, value: Boolean) = cell.setCellValue(value)
  }

  implicit object DoubleMagnet extends CellMagnet[Double] {
    def read(cell: XSSFCell): Option[Double] = Try { cell.getNumericCellValue }.toOption
    def write(cell: XSSFCell, value: Double) = cell.setCellValue(value)
  }

  implicit object IntMagnet extends CellMagnet[Int] {
    def read(cell: XSSFCell): Option[Int] = Try { cell.getNumericCellValue.toInt }.toOption
    def write(cell: XSSFCell, value: Int) = cell.setCellValue(value)
  }

}