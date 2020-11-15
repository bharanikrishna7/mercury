package net.chekuri.mercury

class StringUtilities(punctuators: Set[Char], escape: Char) {
  def charIsPunctuator(ch: Char): Boolean = {
    punctuators.contains(ch)
  }

  def isEscaped(ch: Char): Boolean = {
    if (ch == escape) {
      true
    } else {
      false
    }
  }

  def isEnglishLetter(ch: Char): Boolean = {
    if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
      true
    } else {
      false
    }
  }
}
