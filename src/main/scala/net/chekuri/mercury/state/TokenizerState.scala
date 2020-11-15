package net.chekuri.mercury.state

import net.chekuri.mercury.TokenizerContext
import wvlet.log.LogSupport

abstract class TokenizerState extends LogSupport {
  protected var name: String = "TokenizerState"
  var context: TokenizerContext

  def this(context: TokenizerContext) = {
    this
    this.context = context
  }

  def getName: String = this.name

  protected def collectChar(): Boolean = {
    logger.trace("collecting next character.")
    val current_char: Char = context.getNext
    logger.trace("successfully collecting next character.")
    if (context.EOF(current_char)) {
      false
    } else {
      true
    }
  }

  protected def isOneCharToken(token: Char): Boolean = {
    context.getOneCharTokens.contains(token)
  }

  protected def isTwoCharToken(token: String): Boolean = {
    context.getMultiCharTokens.contains(token)
  }

  protected def charToString(ch: Char): String = ch.toString

  def eatChars: Unit

  def consumeChars(): Unit = {
    context.getCurrentState.eatChars
    context.setCurrentState(nextState)
  }

  def canRead: Boolean = {
    if (context.peekNext == '\0') {
      logger.trace(
        "unable to read from input stream. peeking next element is returning -1 ASCII code."
      )
      false
    } else {
      logger.trace(
        "can read from input source. peeking next element is returning correct ASCII code."
      )
      true
    }
  }

  def getToken: String = {
    context.token
  }

  def hasToken: Boolean = {
    if (getToken.isEmpty) {
      false
    } else {
      true
    }
  }

  def currentLineNumber: Long = {
    context.getLineCount
  }

  def checkIfSpecialSingleCharToken(token: Char): Boolean = {
    context.getOneCharTokens.contains(token)
  }

  def checkIfSpecialMultiCharToken(token: String): Boolean = {
    context.getMultiCharTokens.contains(token)
  }

  def checkIfWhiteSpaceToken(token: Char): Boolean = {
    if (Character.isWhitespace(token) && token != '\n') {
      true
    } else {
      false
    }
  }

  def checkIfSingleQuoteStringToken(token: Char): Boolean = {
    if (token == '\'' && context.getPreviousChar != '\\') {
      true
    } else {
      false
    }
  }

  def checkIfDoubleQuoteStringToken(token: Char): Boolean = {
    if (token == '"' && context.getPreviousChar != '\\') {
      true
    } else {
      false
    }
  }

  def checkIfSingleLineCommentToken(token: String): Boolean = {
    context.getSingleLineCommentTokens.equals(token)
  }

  def checkIfMultiLineCommentToken(token: String): Boolean = {
    context.getMultiLineCommentTokens.equals(token)
  }

  def checkIfAlphanumericToken(token: Char): Boolean = {
    if (
      context.getStringUtilities
        .isEnglishLetter(token) || token.equals('_') || Character.isDigit(token)
    ) {
      true
    } else {
      false
    }
  }

  def nextState: TokenizerState = {
    if (!this.canRead) {
      return null
    }

    val current_char = context.getNext
    val peek_next = context.peekNext
    if (!this.canRead) {
      // TODO: Close mercury input stream
      return context.getEndTokenState
    }

    if (
      context.getSingleLineCommentTokens.nonEmpty && this.canRead && this
        .checkIfSingleLineCommentToken(s"$current_char$peek_next")
    ) {
      return context.getSingleLineCommentTokenState
    }

    if (
      context.getMultiLineCommentTokens.nonEmpty && this.canRead && this
        .checkIfMultiLineCommentToken(s"$current_char$peek_next")
    ) {
      return context.getMultiLineCommentTokenState
    }

    if (Character.isDigit(current_char)) {
      return context.getNumericTokenState
    }

    if (this.checkIfAlphanumericToken(current_char)) {
      return context.getAlphanumericTokenState
    }

    if (this.checkIfSingleQuoteStringToken(current_char)) {
      return context.getSingleQuoteStringTokenState
    }

    if (this.checkIfDoubleQuoteStringToken(current_char)) {
      return context.getDoubleQuoteStringTokenState
    }

    if (canRead) {
      context.getWhitespaceTokenState
    }

    context.getEndTokenState
  }
}
