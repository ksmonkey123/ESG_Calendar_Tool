package ch.awae.esgcal

import scala.language.postfixOps

import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.util.Calendar

import Implicit._

import javax.swing.DefaultComboBoxModel
import javax.swing.JComboBox

import scene._

class DateSelection(day: Int, month: Int, year: Int, λ: () => Unit = () => {}) {

  val cb_days = new JComboBox(1 to 31 map { _.toString } toArray)
  val cb_months = new JComboBox(Month.months.toArray)
  val cb_years = new JComboBox(
    (-2 to 10 map { _ + (Calendar.getInstance get Calendar.YEAR) }) map { _.toString } toArray)

  val updateDayCount = new ActionListener {
    override def actionPerformed(e: ActionEvent) = {
      val last = cb_days.getSelectedIndex + 1
      val size = cb_months.getSelectedItem.asInstanceOf[Month].dayCount(cb_years.getSelectedItem.asInstanceOf[String].toInt)
      cb_days setModel new DefaultComboBoxModel(1 to size map { _.toString } toArray)
      cb_days.setSelectedIndex(if (last > size) size - 1 else last - 1)
    }
  }

  val listener = new ActionListener {
    override def actionPerformed(e: ActionEvent) = λ()

  }

  cb_days.setSelectedIndex(day)
  cb_months.setSelectedIndex(month)
  cb_years.setSelectedIndex(year)

  def date = new Calendar.Builder().setDate(
    cb_years.getSelectedItem.asInstanceOf[String].toInt,
    cb_months.getSelectedItem.asInstanceOf[Month].index,
    cb_days.getSelectedIndex + 1)
    .build.getTime

  def build = horizontal(cb_days, cb_months, cb_years) #> vlock #> hlock

  List(cb_months, cb_years) foreach (_ addActionListener updateDayCount)
  List(cb_days, cb_months, cb_years) foreach (_ addActionListener listener)

}