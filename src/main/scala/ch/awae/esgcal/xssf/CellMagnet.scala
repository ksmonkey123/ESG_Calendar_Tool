package ch.awae.esgcal.xssf

import org.apache.poi.xssf.usermodel.XSSFCell
import scala.util.Try
import java.util.Date
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.CellStyle
import java.time.LocalDate
import org.apache.poi.ss.usermodel.DateUtil
import java.util.Calendar

trait CellReadMagnet[T] {
  def read(cell: XSSFCell): Option[T]
}
trait CellWriteMagnet[T] {
  def write(cell: XSSFCell, value: T): Unit
}
trait CellMagnet[T] extends CellReadMagnet[T] with CellWriteMagnet[T]

object CellMagnet {
  implicit object StringMagnet extends CellMagnet[String] {
    def read(cell: XSSFCell): Option[String] = Try { cell.getStringCellValue }.toOption
    def write(cell: XSSFCell, value: String) = {
      cell.setCellValue(value)
    }
  }

  implicit object BooleanMagnet extends CellMagnet[Boolean] {
    def read(cell: XSSFCell): Option[Boolean] = Try { cell.getBooleanCellValue }.toOption
    def write(cell: XSSFCell, value: Boolean) = cell.setCellValue(value)
  }

  implicit object DoubleMagnet extends CellMagnet[Double] {
    def read(cell: XSSFCell): Option[Double] = Try { cell.getNumericCellValue }.toOption
    def write(cell: XSSFCell, value: Double) = cell.setCellValue(value)
  }

  implicit object IntMagnet extends CellWriteMagnet[Int] {
    def write(cell: XSSFCell, value: Int) = cell.setCellValue(value)
  }

  implicit object LocalDateMagnet extends CellWriteMagnet[LocalDate] {
    def write(cell: XSSFCell, value: LocalDate) = {
      val offset =
        if (cell.getSheet.getWorkbook.isDate1904)
          LocalDate.of(1903, 12, 31)
        else
          LocalDate.of(1899, 12, 31)
      val day = value.toEpochDay - offset.toEpochDay + 1
      cell.setCellValue(day)
    }
  }

}