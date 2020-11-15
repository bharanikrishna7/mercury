package net.chekuri.mercury.state

import net.chekuri.mercury.TokenizerContext

class WhitespaceTokenizerState extends TokenizerState {
  def this(context: TokenizerContext) = {
    this()
    this.context = context
  }

  override def eatChars: Unit = {
    context.token = ""
    logger.debug("will consume characters till a char fails the defined whitespace check.")
    while(checkIfWhiteSpaceToken(context.peekNext)) {
      if(!collectChar()) {
        return
      }
    }
  }

  override var context: TokenizerContext = _
}
