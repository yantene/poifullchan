package net.yantene.poifullchan

import twitter4j.Status
import scala.util.Random
import java.util.Scanner
import java.net.URL
import java.util.Calendar
import java.util.Locale
import twitter4j.Twitter
import twitter4j.StatusUpdate
import twitter4j.TwitterFactory

class PoifullBehavior {
	/*
	 * 現在はこのクラスにあらゆる振る舞いが記述されていますが、
	 * 今後は外部のXMLファイルに記述する方式に変更する予定です。
	 */
	val friends = new Friends
	val twitter = TwitterFactory.getSingleton
	val sentenceGenerator = new SentenceGenerator
	var yesterday = Calendar.getInstance(Locale.JAPAN)

	def reaction(status: Status) {
    print(status.getText)
		var addition = 1

		//そのツイートが語彙収集に値するなら収集する
		if (friends.sourceFlgOf(status.getUser.getId)) sentenceGenerator.addNewVocabulary(status.getText)

		//そのツイートが自分へのリプライなら返信する
		if (status.getInReplyToUserId == 1229249964) addition += reply(status)
		//受信したツイート内容からのポイント計算
		addition += calcPoint(status.getText)

		//ひなほーチェッカー
		addition += hinahoChecker(status)

		//ぽいントの加算&レベルアップのお知らせ
		if (friends.addPoints(status.getUser.getId, addition)) reply("レベルが" + friends.getLevel(status.getUser.getId) + "に上がったよ！", status)
	}

	def calcPoint(text: String): Int = {
		//textのツイート内容から加点をチェック
		var addition = 0
		if (text.contains("ぽいふる") || text.contains("ポイフル") || text.contains("ポイポイ")) addition += 10
		addition
	}

	def reply(status: Status): Int = {
		var addition = 0
		var replySentence = ""
		//語彙力を尋ねていたら
		if (status.getText.contains("語彙力")) {
			replySentence = "現在の語彙力は" + sentenceGenerator.getVocabulary + "だよ！もっと言葉を覚えたいな。"
			addition += 10

			//ぽいントを尋ねていたら
		} else if (status.getText.contains("ぽいント") || status.getText.contains("ポイント") || status.getText.contains("ぽいんと")) {
			replySentence = "現在、" + status.getUser.getName + "さんは" + friends.getPoints(status.getUser.getId) + "ぽいントを獲得しているね。"
			addition += 10

			//レベルを尋ねていたら
		} else if (status.getText.contains("レベル")) {
			replySentence = status.getUser.getName + "さんは現在、レベル" + friends.getLevel(status.getUser.getId) + "だよ！"
			addition += 10

			//自分が技科大生であると主張していたら
		} else if (status.getText.contains("自分は技科大生")) {
			friends.upSourceFlg(status.getUser.getId)
			replySentence = status.getUser.getName + "さんのツイートも技科大生のものという事で、全力でツイートの参考にするね！ありがとう！"

			//「今日の運勢」を含んでいれば
		} else if (status.getText.contains("今日の運勢")) {
			val cal = Calendar.getInstance(Locale.JAPAN)
			cal.setTime(status.getCreatedAt)
			val factor = (status.getUser.getId % 100000).toInt * cal.get(Calendar.DAY_OF_YEAR)
			val un = Array("凶", "吉", "末吉", "中吉", "大吉")
			val deng = Array("テストの範囲", "通学途中", "帰り道", "背後", "体の異変", "心の不調", "頭上", "トイレの中", "自分を呼ぶ声")
			val lucky = Array("三角定規", "バレン", "携帯電話", "ギザ10", "とがった鉛筆", "マグカップ", "折りたたみ傘", "オペアンプ", "魔法石", "テレホンカード", "図書カード", "永久磁石", "宇宙ゴマ", "酸化銀", "ちくわ", "ポイフル", "ピン札")
			val bad = Array("茶色い石ころ", "黒い虫眼鏡", "赤い財布", "昭和の5円玉", "不透明な定規", "輪ゴム", "残りの少ない消しゴム", "怪しいプロセス", "青い食べ物", "穴の多いベルト", "サバイバルナイフ", "期限切れのカード", "季節外れの衣服")
			replySentence = status.getUser.getName + "さんの今日の運勢は" + un.apply(factor % un.length) + "だよ!\nそうだなあ…" + deng.apply(factor % deng.length) + "には十分気をつけてね。 ラッキーアイテムは" + lucky.apply(factor % lucky.length) + "、バッドアイテムは" + bad.apply(factor % bad.length) + "だよ！ 当たるかな〜?"
			addition += 10

			//それ以外の場合、マルコフ連鎖で文を生成し、返信
		} else {
			replySentence = sentenceGenerator.createSentence(138 - status.getUser.getScreenName.length)
			addition += 10
		}

		reply(replySentence, status)

		addition
	}

	def hinahoChecker(status: Status): Int = {
		val gap = (status.getCreatedAt.getTime / 1000 + 15 * 60 * 60) % (12 * 60 * 60) - 6 * 60 * 60
		if (status.getText.contains("なほー") && Math.abs(gap) < 60) {
			if (gap == 0) {
				reply("ひなほー成功！ おめでとう！\n300ぽいント" + decStr("ゲットだよ！", "げっと！", "を手に入れたよ！"), status)
				300
			} else {
				val penalty = Math.round(Math.pow(1.085, Math.abs(gap)) + 29).toInt
				reply("ひなほー失敗…。" + Math.abs(gap) + "秒" + (if (gap > 0) "遅" else "早") + "かった。" + penalty + "ぽいント" + decStr("失ったよ。", "減点や…。", "差し引くね。"), status)
				-penalty
			}
		} else {
			0
		}
	}

	def regular(status: Status) {
		val cal = Calendar.getInstance(Locale.JAPAN)
		cal.setTime(status.getCreatedAt)
		ranking(cal)
	}

	def ranking(cal: Calendar) {
		if (cal.get(Calendar.DATE) != yesterday.get(Calendar.DATE)) {
			//昨日のぽいント増加量ランキング(毎日)
			val ranking = friends.getIncreaseRanking
			tweet(
				"**昨日のぽいント増加量ランキング**\n" + //20文字
					"1位 " + "@" + twitter.showUser(friends.getIncreaseRanking(0)._1).getScreenName + " " + friends.getIncreaseRanking(0)._2 + "ぽいント\n" + //30文字程度
					"2位 " + "@" + twitter.showUser(friends.getIncreaseRanking(1)._1).getScreenName + " " + friends.getIncreaseRanking(1)._2 + "ぽいント\n" + //30文字程度
					"3位 " + "@" + twitter.showUser(friends.getIncreaseRanking(2)._1).getScreenName + " " + friends.getIncreaseRanking(2)._2 + "ぽいント\n" //30文字程度
			)
			friends.changeDay
			//先週のレベルランキング(毎週月曜)
			if (cal.get(Calendar.DAY_OF_WEEK) == 2) {
				val ranking = friends.getLevelRanking
				tweet(
					"**先週時点でのレベルランキング**\n" + //20文字
						"1位 " + "@" + twitter.showUser(ranking(0)._1).getScreenName + " レベル" + ranking(0)._2 + "\n" + //30文字程度
						"2位 " + "@" + twitter.showUser(ranking(1)._1).getScreenName + " レベル" + ranking(1)._2 + "\n" + //30文字程度
						"3位 " + "@" + twitter.showUser(ranking(2)._1).getScreenName + " レベル" + ranking(2)._2 + "\n" //30文字程度
				)
			}
			yesterday = cal
		}
	}

	def monologue(count: Int): Int = {
		if (count == 0) {
			tweet(sentenceGenerator.createSentence())
			Random.nextInt((friends.getNumOfFriends * 0.8).toInt)
		} else {
			count - 1
		}
	}
	
	def decStr(strs: String*) = strs.apply(Random.nextInt(strs.size))

	def tweet(str: String) {
		twitter.updateStatus(str)
	}

	def reply(str: String, status: Status) {
		if (str.length <= 138 - status.getUser.getScreenName.length) {
			val statusUpdate = new StatusUpdate("@" + status.getUser.getScreenName + " " + str)
			statusUpdate.setInReplyToStatusId(status.getId)
			twitter.updateStatus(statusUpdate)
			//println("@" + status.getUser.getScreenName + " " + str)
		}
	}

	def follow(id: Long) {
		twitter.createFriendship(id)
	}
}
