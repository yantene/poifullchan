package net.yantene.poifullchan

import java.io.File
import java.io.FileWriter
import scala.collection.mutable.HashMap
import scala.io.Source

object Account{
	def getAccountMap: HashMap[Long, Account] = {
		var accountMap = new HashMap[Long, Account]
		new File("./Accounts/").list.foreach(id => {
			accountMap += id.toLong -> new Account(id.toLong)
		})
		accountMap
	}
}

class Account(val id: Long) {
	var level: Int = 1
	var points: Long = 100
	var yesPoints: Long = 0
	var replyCnt = 0
	var hinahoContCnt = 0
	var hinahoSuccCnt = 0
	var hinahoFailCnt = 0
	var srcFlg = false
	if(new File("./Accounts/").list.contains(id.toString)){
		val loadData = Source.fromFile("./Accounts/" + id).getLines.toArray
		points = loadData(1).toLong
		level = Math.max(Math.round(Math.log((points + 7)/100) / Math.log(1.072)).toInt, loadData(0).toInt)
		yesPoints = loadData(2).toLong
		replyCnt = loadData(3).toInt
		hinahoContCnt = loadData(4).toInt
		hinahoSuccCnt = loadData(5).toInt
		hinahoFailCnt = loadData(6).toInt
		srcFlg = loadData(7) == "true"
	}
	
	def diffPoints = points - yesPoints
	
	def changeDay{
		yesPoints = points
		update
	}
	
	def succeedInHinaho{
		hinahoContCnt += 1
		hinahoSuccCnt += 1
		update
	}
	
	def failedInHinaho{
		hinahoContCnt = 0
		hinahoFailCnt += 1
		update
	}
	
	def addPoints(addition: Int) = {
		points += addition
		val latestLevel = Math.round(Math.log((points + 7)/100) / Math.log(1.072)).toInt
		val levelUp = latestLevel > level
		level = Math.max(latestLevel, level)
		update
		levelUp
	}
	
	def update{
		val writer = new FileWriter("./Accounts/" + id, false)
		writer.write(level + "\n")
		writer.write(points + "\n")
		writer.write(yesPoints + "\n")
		writer.write(replyCnt + "\n")
		writer.write(hinahoContCnt + "\n")
		writer.write(hinahoSuccCnt + "\n")
		writer.write(hinahoFailCnt + "\n")
		writer.write(srcFlg + "\n")
		writer.close
	}
}