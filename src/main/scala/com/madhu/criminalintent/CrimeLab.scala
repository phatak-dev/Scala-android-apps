package com.madhu.criminalintent

import java.util.UUID

import android.util.Log.d

import android.content.Context

  class CrimeLab(context:Context) {
  var crimes:List[Crime] = List()
  val fileName = "crimes.json"
    def getCrimes(): List[Crime] = crimes
    def addCrime(crime:Crime) = crimes = crime :: crimes
    def getCrime(id: UUID): Option[Crime] = crimes.find(value => value.uuid == id)
    def saveCrimes(implicit context:Context) = {
      new CrimeSerializer(fileName,context).saveCrimes(getCrimes)
      d("#####","saved to file")
    }
}


object CrimeLab {
   var crimeLab:CrimeLab = null
  def apply(implicit con:Context) = {
    if(crimeLab==null) crimeLab = new CrimeLab(con)
    crimeLab
  }
}