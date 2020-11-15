package net.chekuri.mercury

import java.io.InputStream

import net.chekuri.mercury.state.{BeginTokenizerState, EndTokenizerState, TokenizerState, WhitespaceTokenizerState}
import wvlet.log.LogSupport

class TokenizerContext extends LogSupport {
  protected var oneCharTokens: Set[Char]
  protected var multiCharTokens: Set[String]
  protected var singleLineCommentTokens: String
  protected var multiLineCommentTokens: String

  def getOneCharTokens: Set[Char] = this.oneCharTokens

  def getMultiCharTokens: Set[String] = this.multiCharTokens

  def getSingleLineCommentTokens: String = this.singleLineCommentTokens

  def getMultiLineCommentTokens: String = this.multiLineCommentTokens

  var token: String
  protected var previous_char: Char = null
  protected var current_char: Char = null
  protected var line_count: Long = -1
  protected var stream: MercuryInputStream
  protected var string_utils: StringUtilities

  def getPreviousChar: Char = this.previous_char

  def getCurrentChar: Char = this.current_char

  def getLineCount: Long = this.line_count

  def getStringUtilities: StringUtilities = this.string_utils

  def this(
      stream: InputStream,
      oneCharTokens: Set[Char],
      multiCharTokens: Set[String],
      singleLineCommentTokens: String,
      multiLineCommentTokens: String,
      punctuators: Set[Char],
      escape: Char
  ) = {
    this()
    logger.debug("initializing mercury tokenizer context.")
    logger.debug(" >> initializing mercury tokenizer context class variables.")
    this.stream = new MercuryInputStream(stream)
    this.oneCharTokens = oneCharTokens
    this.multiCharTokens = multiCharTokens
    this.singleLineCommentTokens = singleLineCommentTokens
    this.multiLineCommentTokens = multiLineCommentTokens
    this.previous_char = '\0'
    this.current_char = '\0'
    this.line_count = 0
    this.string_utils = new StringUtilities(punctuators, escape)
    logger.debug(
      " >> successfully initialized mercury tokenizer context class variables."
    )
    logger.debug(" >> initializing mercury tokenizer context state variables.")
    init_states
    logger.debug(
      " >> successfully initialized mercury tokenizer context state variables."
    )
    logger.debug("successfully initialized mercury tokenizer context.")
  }

  def EOF(ch: Int): Boolean = {
    if (ch == -1) {
      true
    } else {
      false
    }
  }

  def getNext: Char = {
    val next = stream.read()
    if (EOF(next)) {
      logger.trace("reached EOF ('\\0').")
      '\0'
    } else {
      this.previous_char = this.current_char
      this.current_char = next.toChar
      this.getCurrentChar
    }
  }

  def peekNext: Char = {
    val next = stream.peek
    if (EOF(next)) {
      '\0'
    } else {
      next.toChar
    }
  }

  protected var current_state: TokenizerState
  protected var special_token_state: TokenizerState
  protected var whitespace_token_state: WhitespaceTokenizerState
  protected var single_quote_string_token_state: TokenizerState
  protected var double_quote_string_token_state: TokenizerState
  protected var alphanumeric_token_state: TokenizerState
  protected var numeric_token_state: TokenizerState
  protected var single_line_comment_token_state: TokenizerState
  protected var multi_line_comment_token_state: TokenizerState
  protected var begin_token_state: BeginTokenizerState
  protected var end_token_state: EndTokenizerState

  private def init_states: Unit = {
    this.begin_token_state = new BeginTokenizerState(this)
    this.whitespace_token_state = new WhitespaceTokenizerState(this)

    this.end_token_state = new EndTokenizerState(this)
  }

  def setCurrentState(nextState: TokenizerState): Unit = {
    logger.trace(s"current tokenizer context state: ${current_state.getName}")
    logger.trace(s"next tokenizer context state   : ${nextState.getName}")
  }

  def getCurrentState = this.current_state
  def getSpecialTokenState = this.special_token_state
  def getWhitespaceTokenState = this.whitespace_token_state
  def getSingleQuoteStringTokenState = this.single_quote_string_token_state
  def getDoubleQuoteStringTokenState = this.double_quote_string_token_state
  def getAlphanumericTokenState = this.alphanumeric_token_state
  def getNumericTokenState = this.numeric_token_state
  def getSingleLineCommentTokenState = this.single_line_comment_token_state
  def getMultiLineCommentTokenState = this.multi_line_comment_token_state
  def getEndTokenState = this.end_token_state
}
