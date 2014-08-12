package com.madhu.criminalintent

import java.util.UUID

import android.util.Log.d

import macroid.FullDsl._
import macroid._
import macroid.contrib.LpTweaks._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
object CrimeLab {
 var crimes:List[Crime] = List() /*(1 until 100) map (i => { val crime = new Crime()
    crime.mTitle = "Set mTitle" + i    
    crime.solved = (i % 2) == 0 
     crime }) toList */
 
   def getCrimes(): List[Crime] = crimes
   def addCrime(crime:Crime) = crimes = crime :: crimes
   def getCrime(id: UUID): Option[Crime] = crimes.find(value => value.uuid == id)	 
  
}