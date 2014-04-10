package net.yantene.poifullchan

import scala.util.control.Breaks.{ break, breakable }
import collection.JavaConversions._
import scala.io.Source
import scala.util.Random
import net.reduls.gomoku._
import java.io.FileWriter

class SentenceGenerator {
  var vocabulary: List[String] = Source.fromFile("vocabulary").getLines.toList //ファイルから語彙を取得
  val rmRgExpr = "(https?://t.co/[0-9a-zA-Z]{7,}|(RT|QT).*|(@|#)[0-9a-zA-Z_]*)" //ツイートから削除の正規表現

  def addNewVocabulary(str: String) {
    //新しいツイートから新語を抽出
    var words: List[String] = Nil
    words = "__SENTENCE_BEGIN__" :: words
    Tagger.wakati(str.replaceAll(rmRgExpr, "")).foreach(morpheme => words = morpheme :: words)
    words = "__SENTENCE_END__" :: words

    //語彙に新語を追加
    vocabulary = vocabulary ::: words.reverse

    //新語についてファイルに保存
    val writer = new FileWriter("vocabulary", true)
    words.reverse.foreach(word => {
        writer.write(word + "\n")
      })
    writer.close
  }

  def getVocabulary = vocabulary.length

  //制限文字数内で文章を生成
  def createSentence(limit: Int = 140) = {
    var sentence = ""
    do {
      sentence = ""
      var word1 = "__SENTENCE_END__"
      var word2 = "__SENTENCE_BEGIN__"
      breakable {
        while (true) {
          val words = getLastWords(word1, word2)
          val word = words.get(Random.nextInt(words.length))
          if (word == "__SENTENCE_END__") break
          sentence += word
          word1 = word2
          word2 = word
        }
      }
    } while (sentence.length > limit)
    sentence
  }

  //渡された二語に続く単語を提供
  def getLastWords(word1: String, word2: String) = {
    var matchWords: List[String] = Nil
    var temp1 = ""
    var temp2 = "__SENTENCE_END__"
    vocabulary.foreach(word => {
        //目標の単語であればmatchWordsリストに追加
        if (temp1 == word1 && temp2 == word2) matchWords = word :: matchWords
        temp1 = temp2
        temp2 = word
      })
    matchWords
  }
}
