package com.madhu.criminalintent

import java.util.UUID

import android.util.Log.d

import android.content.Context

class CrimeLab(context: Context) {
  var crimes: List[Crime] = List()
  val fileName = "crimes.json"
  var serializer = new CrimeSerializer(fileName, context)

  def getCrimes(): List[Crime] = crimes

  def addCrime(crime: Crime) = crimes = crime :: crimes

  def getCrime(id: UUID): Option[Crime] = crimes.find(value => value.uuid == id)

  def saveCrimes() = {
    serializer.saveCrimes(getCrimes)
    d("#####", "saved to file")
  }

  def deleteCrime(crime:Crime) = {
    crimes = crimes.filter(_.uuid != crime.uuid)
  }

  def loadCrimes() {
    d("############", "loadCrimes")
    crimes = serializer.readCrimes()
  }
}


object CrimeLab {
  var crimeLab: CrimeLab = null

  def apply(implicit con: Context) = {
    if (crimeLab == null) {
      crimeLab = new CrimeLab(con)
      crimeLab.loadCrimes()
    }
    crimeLab
  }
}