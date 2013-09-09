package net.yantene.poifullchan

import scala.io.Source
import scala.collection.mutable.HashMap
import java.io.FileWriter

class Friends{
	val accounts = Account.getAccountMap
	
	def getPoints(id: Long) = if(accounts.contains(id)) accounts.apply(id).points else 100
	
	def getLevel(id: Long) = if(accounts.contains(id)) accounts.apply(id).level else 1
	
	def getNumOfFriends = accounts.size
	
	def changeDay{
		accounts.foreach(_._2.changeDay)
	}
	
	def hinahoCount(id: Long, success: Boolean){
		if(!accounts.contains(id)) accounts += id -> new Account(id)
		if(success){
			accounts.apply(id).succeedInHinaho
		}else{
			accounts.apply(id).failedInHinaho
		}
	}
	
	def getIncreaseRanking = {
		var ranking: List[(Long, Long)] = Nil
		accounts.foreach(account =>{
			ranking = (account._1, account._2.diffPoints) :: ranking
		})
		ranking.sortWith((a, b) => a._2 > b._2)
	}
	
	def getLevelRanking = {
		var ranking: List[(Long, Int, Long)] = Nil
		accounts.foreach(account =>{
			ranking = (account._1, account._2.level, account._2.points) :: ranking
		})
		ranking.sortWith((a, b) => if(a._2 == b._2) a._3 > b._3 else a._2 > b._2)
	}
	
	def addPoints(id: Long, addition: Int) = {
		if(!accounts.contains(id)) accounts += id -> new Account(id)
		accounts.apply(id).addPoints(addition)
	}
	
	def upSourceFlg(id: Long){
		if(!accounts.contains(id)) accounts += id -> new Account(id)
		accounts.apply(id).srcFlg = true
	}
	
	def downSourceFlg(id: Long){
		if(!accounts.contains(id)) accounts += id -> new Account(id)
		accounts.apply(id).srcFlg = false
	}
	
	def sourceFlgOf(id: Long) = {
		if(!accounts.contains(id)) accounts += id -> new Account(id)
		accounts.apply(id).srcFlg
	}
	
}