package ziphil.module.zatlin;

import java.io.IOException;
import java.io.Reader;


public class ZatlinParser {

  private ZatlinRoot root = new ZatlinRoot();
  private ZatlinLexer lexer;

  public ZatlinParser(Reader reader) {
    this.lexer = new ZatlinLexer(reader);
  }

  public ZatlinRoot readRoot() throws IOException {
    ZatlinSentenceParser sentenceParser = new ZatlinSentenceParser();
    for (ZatlinToken token ; (token = lexer.nextToken()) != null ;) {
      ZatlinTokenType tokenType = token.getType();
      if (tokenType.equals(ZatlinTokenType.SEMICOLON)) {
        sentenceParser.addToken(token);
        if (sentenceParser.isDefinition()) {
          ZatlinDefinition definition = sentenceParser.readDefinition();
          ZatlinToken identifier = definition.getIdentifier();
          if (!root.containsDefinitionOf(identifier.getText())) {
            root.getDefinitions().add(definition);
          } else {
            throw new ZatlinParseException("Duplicate definition", identifier);
          }
        } else if (sentenceParser.isMainGeneratable()) {
          ZatlinGeneratable mainGeneratable = sentenceParser.readMainGeneratable();
          if (!root.hasMainGeneratable()) {
            root.setMainGeneratable(mainGeneratable);
          } else {
            throw new ZatlinParseException("Duplicate definition of the main pattern", sentenceParser.getTokens().get(0));
          }
        } else {
          throw new ZatlinParseException("Invalid sentence", token);
        }
        sentenceParser.clear();
      } else {
        sentenceParser.addToken(token);
      }
    }
    ensureSafety();
    lexer.close();
    return root;
  }

  private void ensureSafety() {
    if (!root.hasMainGeneratable()) {
      throw new ZatlinParseException("No main pattern", (ZatlinToken)null);
    }
    ZatlinToken unknownIdentifier = root.findUnknownIdentifier();
    if (unknownIdentifier != null) {
      throw new ZatlinParseException("Undefined identifier", unknownIdentifier);
    }
    ZatlinToken circularIdentifier = root.findCircularIdentifier();
    if (circularIdentifier != null) {
      throw new ZatlinParseException("Circular reference involving identifier", circularIdentifier);
    }
  }

}