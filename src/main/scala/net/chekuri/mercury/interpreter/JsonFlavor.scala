package net.chekuri.mercury.interpreter

object JsonFlavor {
  val oneCharTokens: Set[Char] = Set[Char]('{', '}', '[', ']', ':', ',')
  val twoCharTokens: Set[String] = Set[String]()
  val singleLineCommentTokens: String = ""
  val multiLineCommentTokens: String = ""

  val jsonPunctuators: Set[Char] = {
    val results: Set[Char] = Set[Char]('\'', '"')
    results ++ this.oneCharTokens
  }

  val escape: Char = '\\'
}
