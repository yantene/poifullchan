package net.yantene.poifullchan

import twitter4j._
import java.util.Scanner
import java.net.URL
import java.util.Calendar
import java.util.Locale

class PoifullStream extends UserStreamAdapter {
  val poifullBehavior = new PoifullBehavior
  var twtCnt = 0

  //フォロー返し
  override def onFollow(source: User, target: User) {
    if (target.getId == 1229249964) poifullBehavior.follow(source.getId)
  }

  //HomeTLに新しいツイートが流れてきた
  override def onStatus(status: Status) {
    //定期としての反応をする(ランキングなど、時間に応じて反応するもの)
    poifullBehavior.regular(status)

    //一般のツイートであれば、リアクションをする
    if(status.getUser.getId != 1229249964 && !status.isRetweet) poifullBehavior.reaction(status)

    //普通のツイートをする
    twtCnt = poifullBehavior.monologue(twtCnt)
  }
}
