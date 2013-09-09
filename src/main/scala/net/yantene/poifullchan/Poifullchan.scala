package net.yantene.poifullchan

import twitter4j._

object Poifullchan {

	def main(args: Array[String]) {
		val twitterStream = new TwitterStreamFactory().getInstance
		twitterStream.addListener(new PoifullStream)
		twitterStream.user
	}
}
