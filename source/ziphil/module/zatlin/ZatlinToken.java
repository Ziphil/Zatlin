package ziphil.module.zatlin;

import java.util.List;
import ziphil.module.ExtendedBufferedReader;


public class ZatlinToken implements ZatlinGeneratable {

  private ZatlinTokenType type;
  private String text;
  private String fullText;
  private int lineNumber;
  private int columnNumber;

  public ZatlinToken(ZatlinTokenType type, String text, String fullText, int lineNumber, int columnNumber) {
    this.type = type;
    this.text = text;
    this.fullText = fullText;
    this.lineNumber = lineNumber;
    this.columnNumber = columnNumber;
  }

  public ZatlinToken(ZatlinTokenType type, String text, int lineNumber, int columnNumber) {
    this.type = type;
    this.text = text;
    this.fullText = fullText;
    this.lineNumber = lineNumber;
    this.columnNumber = columnNumber;
  }

  public ZatlinToken(ZatlinTokenType type, String text, String fullText, ExtendedBufferedReader reader) {
    this.type = type;
    this.text = text;
    this.fullText = fullText;
    this.lineNumber = reader.getLineNumber();
    this.columnNumber = reader.getColumnNumber();
  }

  public ZatlinToken(ZatlinTokenType type, String text, ExtendedBufferedReader reader) {
    this.type = type;
    this.text = text;
    this.fullText = text;
    this.lineNumber = reader.getLineNumber();
    this.columnNumber = reader.getColumnNumber();
  }

  public String generate(ZatlinRoot root) {
    String output = null;
    if (type.equals(ZatlinTokenType.QUOTE_LITERAL)) {
      output = generateByQuoteLiteral(root);
    } else if (type.equals(ZatlinTokenType.IDENTIFIER)) {
      output = generateByIdentifier(root);
    }
    return output;
  }

  private String generateByQuoteLiteral(ZatlinRoot root) {
    return text;
  }

  private String generateByIdentifier(ZatlinRoot root) {
    ZatlinGeneratable content = root.findContentOf(text);
    if (content != null) {
      String output = content.generate(root);
      return output;
    } else {
      throw new ZatlinException("This cannot happen");
    }
  }

  public ZatlinToken findUnknownIdentifier(ZatlinRoot root) {
    if (type.equals(ZatlinTokenType.IDENTIFIER)) {
      if (!root.containsDefinitionOf(text)) {
        return this;
      } else {
        return null;
      }
    } else {
      return null;
    }
  }

  public ZatlinToken findCircularIdentifier(List<ZatlinToken> identifiers, ZatlinRoot root) {
    if (type.equals(ZatlinTokenType.IDENTIFIER)) {
      ZatlinToken containedIdentifier = null;
      for (ZatlinToken identifier : identifiers) {
        if (equals(identifier)) {
          containedIdentifier = identifier;
          break;
        };
      }
      if (containedIdentifier != null) {
        return containedIdentifier;
      } else {
        ZatlinDefinition definition = root.findDefinitionOf(text);
        if (definition != null) {
          return definition.findCircularIdentifier(identifiers, root);
        } else {
          return null;
        }
      }
    } else {
      return null;
    }
  }

  public boolean equals(Object object) {
    if (object instanceof ZatlinToken) {
      return type.equals(((ZatlinToken)object).getType()) && text.equals(((ZatlinToken)object).getText());
    } else {
      return false;
    }
  }

  public String toString() {
    StringBuilder string = new StringBuilder();
    string.append("<");
    string.append(type);
    string.append(": '");
    string.append(fullText);
    string.append("'>");
    return string.toString();
  }

  public ZatlinTokenType getType() {
    return this.type;
  }

  public String getText() {
    return this.text;
  }

  public String getFullText() {
    return this.fullText;
  }

  public int getLineNumber() {
    return this.lineNumber;
  }

  public int getColumnNumber() {
    return this.columnNumber;
  }

}