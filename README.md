# poifullchan

マルコフ連鎖bot、ぽいふるちゃん([@poifullchan](http://twitter.com/poifullchan))のソースコードです。Scalaで書かれています。継ぎ足し継ぎ足しで書いていたのでソースコードは基本汚いですが、ご承知願います。いつか書き直す。

## 試し方

* 必要なものを揃える

 poifullchanにはいくつかのライブラリが必要ですが、同梱されていません。lib/下にtwitter4jとgomokuのjarファイルを配置してください。

 + <https://github.com/yusuke/twitter4j/> - Twitter4J
 + <https://github.com/sile/gomoku> - gomoku

 また、ルートディレクトリにtwitter4j.propertiesを置き、試したいアカウントのconsumerKey、consumerSecret、accessToken、accessTokenSecretをそれぞれ記述してください。

 > oauth.consumerKey=*consumerKey*  
 > oauth.consumerSecret=*consumerSecret*  
 > oauth.accessToken=*accessToken*
 > oauth.accessTokenSecret=*accessTokenSecret*

* sbtでビルドする

 sbtを使ってjarファイルを生成してください。

 これでとりあえずは使えるようになります。具体的な使い方についてはソースコードを参照してください。
