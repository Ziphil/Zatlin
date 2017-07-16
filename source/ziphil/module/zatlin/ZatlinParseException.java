package ziphil.module.zatlin;

import java.util.List;
import ziphil.module.ExtendedBufferedReader;


public class ZatlinParseException extends RuntimeException {

  private String message = "";
  private String fullMessage = "";

  public ZatlinParseException() {
    super();
  }

  public ZatlinParseException(String message) {
    super();
    this.message = message;
    makeFullMessage((ZatlinToken)null);
  }

  public ZatlinParseException(String message, ZatlinToken token) {
    super();
    this.message = message;
    makeFullMessage(token);
  }

  public ZatlinParseException(String message, List<ZatlinToken> tokens) {
    super();
    this.message = message;
    makeFullMessage(tokens);
  }

  public ZatlinParseException(String message, int codePoint, ExtendedBufferedReader reader) {
    super();
    this.message = message;
    makeFullMessage(codePoint, reader);
  }

  private void makeFullMessage(ZatlinToken token) {
    StringBuilder subFullMessage = new StringBuilder();
    subFullMessage.append("Parse Error: ");
    subFullMessage.append(message);
    if (token != null) {
      subFullMessage.append("\n  ");
      subFullMessage.append(token.getFullText());
      subFullMessage.append(" (at line ");
      int lineNumber = token.getLineNumber();
      int columnNumber = token.getColumnNumber();
      if (lineNumber >= 0) {
        subFullMessage.append(lineNumber);
      } else {
        subFullMessage.append("?");
      }
      subFullMessage.append(" column ");
      if (columnNumber >= 0) {
        subFullMessage.append(columnNumber);
      } else {
        subFullMessage.append("?");
      }
      subFullMessage.append(")");
    }
    fullMessage = subFullMessage.toString();
  }

  private void makeFullMessage(List<ZatlinToken> tokens) {
    StringBuilder subFullMessage = new StringBuilder();
    subFullMessage.append("Parse Error: ");
    subFullMessage.append(message);
    if (!tokens.isEmpty()) {
      subFullMessage.append("\n  ");
      for (ZatlinToken token : tokens) {
        subFullMessage.append(token.getFullText());
      }
      subFullMessage.append(" (at line ");
      int lineNumber = tokens.get(tokens.size() - 1).getLineNumber();
      int columnNumber = tokens.get(tokens.size() - 1).getColumnNumber();
      if (lineNumber >= 0) {
        subFullMessage.append(lineNumber);
      } else {
        subFullMessage.append("?");
      }
      subFullMessage.append(" column ");
      if (columnNumber >= 0) {
        subFullMessage.append(columnNumber);
      } else {
        subFullMessage.append("?");
      }
      subFullMessage.append(")");
    }
    fullMessage = subFullMessage.toString();
  }

  private void makeFullMessage(int codePoint, ExtendedBufferedReader reader) {
    StringBuilder subFullMessage = new StringBuilder();
    subFullMessage.append("Parse Error: ");
    subFullMessage.append(message);
    if (codePoint >= 0) {
      subFullMessage.append("\n  ");
      subFullMessage.appendCodePoint(codePoint);
    }
    if (reader != null) {
      if (codePoint < 0) {
        subFullMessage.append("\n  ");
      }
      subFullMessage.append(" (at line ");
      int lineNumber = reader.getLineNumber();
      int columnNumber = reader.getColumnNumber();
      if (lineNumber >= 0) {
        subFullMessage.append(lineNumber);
      } else {
        subFullMessage.append("?");
      }
      subFullMessage.append(" column ");
      if (columnNumber >= 0) {
        subFullMessage.append(columnNumber);
      } else {
        subFullMessage.append("?");
      }
      subFullMessage.append(")");
    }
    fullMessage = subFullMessage.toString();
  }

  public String getMessage() {
    return this.message;
  }

  public String getFullMessage() {
    return this.fullMessage;
  }

}