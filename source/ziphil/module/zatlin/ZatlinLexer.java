package ziphil.module.zatlin;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import ziphil.module.ExtendedBufferedReader;


public class ZatlinLexer implements Closeable, AutoCloseable {

  private ExtendedBufferedReader reader;
  private boolean first = true;
  private boolean afterSemicolon = false;

  public ZatlinLexer(Reader reader) {
    this.reader = new ExtendedBufferedReader(reader);
  }

  // 次のトークンを取得します。
  // ファイルの終わりに達した場合は null を返します。
  // スペースとコメントは無視されます。
  // もともとの入力で改行前のセミコロンが省略されている場合でも、自動的にセミコロンを補って動作します。
  public ZatlinToken nextToken() throws IOException {
    boolean nextLine = skipBlank();
    ZatlinToken token = null;
    if (first || afterSemicolon || !nextLine) {
      first = false;
      reader.mark(3);
      int codePoint = reader.read();
      if (codePoint == '"') {
        reader.reset();
        token = nextQuoteLiteral();
      } else if (codePoint == '=') {
        token = new ZatlinToken(ZatlinTokenType.EQUAL, "=", reader);
      } else if (codePoint == '|') {
        token = new ZatlinToken(ZatlinTokenType.VERTICAL, "|", reader);
      } else if (codePoint == '-') {
        token = new ZatlinToken(ZatlinTokenType.MINUS, "-", reader);
      } else if (codePoint == '^') {
        token = new ZatlinToken(ZatlinTokenType.CIRCUMFLEX, "^", reader);
      } else if (codePoint == '%') {
        token = new ZatlinToken(ZatlinTokenType.PERCENT, "%", reader);
      } else if (codePoint == ';') {
        token = new ZatlinToken(ZatlinTokenType.SEMICOLON, ";", reader);
      } else if (ZatlinLexer.isNumeric(codePoint)) {
        reader.reset();
        token = nextNumeric();
      } else if (ZatlinLexer.isLetter(codePoint)) {
        reader.reset();
        token = nextIdentifier();
      } else if (codePoint == -1) {
        if (!afterSemicolon) {
          token = new ZatlinToken(ZatlinTokenType.SEMICOLON, "", reader);
        } else {
          token = null;
        }
      } else {
        throw new ZatlinParseException("Invalid symbol", codePoint, reader);
      }
    } else {
      token = new ZatlinToken(ZatlinTokenType.SEMICOLON, "", reader);
    }
    afterSemicolon = false ;
    if (token != null) {
      ZatlinTokenType tokenType = token.getType();
      if (tokenType.equals(ZatlinTokenType.SEMICOLON)) {
        afterSemicolon = true;
      }
    }
    return token;
  }

  private ZatlinToken nextIdentifier() throws IOException {
    StringBuilder currentName = new StringBuilder();
    while (true) {
      reader.mark(1);
      int codePoint = reader.read();
      if (ZatlinLexer.isLetter(codePoint)) {
        currentName.appendCodePoint(codePoint);
      } else {
        reader.reset();
        break;
      }
    }
    ZatlinToken token = new ZatlinToken(ZatlinTokenType.IDENTIFIER, currentName.toString(), reader);
    return token;
  }

  private ZatlinToken nextNumeric() throws IOException {
    StringBuilder currentNumber = new StringBuilder();
    while (true) {
      reader.mark(1);
      int codePoint = reader.read();
      if (ZatlinLexer.isLetter(codePoint)) {
        if (ZatlinLexer.isNumeric(codePoint)) {
          currentNumber.appendCodePoint(codePoint);
        } else {
          throw new ZatlinParseException("Invalid numeric literal", codePoint, reader);
        }
      } else {
        reader.reset();
        break;
      }
    }
    ZatlinToken token = new ZatlinToken(ZatlinTokenType.NUMERIC, currentNumber.toString(), reader);
    return token;
  }

  private ZatlinToken nextQuoteLiteral() throws IOException {
    StringBuilder currentContent = new StringBuilder();
    boolean inside = false;
    while (true) {
      int codePoint = reader.read();
      if (inside) {
        if (codePoint == '\\') {
          int nextCodePoint = reader.read();
          if (nextCodePoint == '"' || nextCodePoint == '\\') {
            currentContent.appendCodePoint(nextCodePoint);
          } else if (nextCodePoint == 'u') {
            StringBuilder escapeCodePointString = new StringBuilder();
            for (int i = 0 ; i < 4 ; i ++) {
              int escapeCodePoint = reader.read();
              if (ZatlinLexer.isHex(escapeCodePoint)) {
                escapeCodePointString.appendCodePoint(escapeCodePoint);
              } else {
                throw new ZatlinParseException("Invalid escape sequence", codePoint, reader);
              }
            }
            currentContent.appendCodePoint(Integer.parseInt(escapeCodePointString.toString(), 16));
          } else {
            throw new ZatlinParseException("Invalid escape sequence", codePoint, reader);
          }
        } else if (codePoint == '\n') {
          throw new ZatlinParseException("The line ended before a string literal is closed", -1, reader);
        } else if (codePoint == -1) {
          throw new ZatlinParseException("The line ended before a string literal is closed", -1, reader);
        } else if (codePoint == '"') {
          break;
        } else {
          currentContent.appendCodePoint(codePoint);
        }
      } else {
        if (codePoint == '"') {
          inside = true;
        } else if (codePoint == -1) {
          break;
        }
      }
    }
    String text = currentContent.toString();
    ZatlinToken token = new ZatlinToken(ZatlinTokenType.QUOTE_LITERAL, text, "\"" + text + "\"", reader);
    return token;
  }

  private boolean skipBlank() throws IOException {
    boolean inComment = false;
    boolean nextLine = false;
    while (true) {
      if (inComment) {
        int codePoint = reader.read();
        if (codePoint == '\n' || codePoint == -1) {
          inComment = false;
          nextLine = true;
        }
      } else {
        reader.mark(2);
        int codePoint = reader.read();
        if (codePoint == '#') {
          inComment = true;
        } else if (codePoint == '\n') {
          nextLine = true;
        } else if (!ZatlinLexer.isWhitespace(codePoint)) {
          reader.reset();
          break;
        }
      }
    }
    return nextLine;
  }

  public static boolean isWhitespace(int codePoint) {
    return Character.isWhitespace(codePoint);
  }

  public static boolean isAllWhitespace(String string) {
    for (int i = 0 ; i < string.length() ; i ++) {
      if (!Character.isWhitespace(string.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  public static boolean isNumeric(int codePoint) {
    return codePoint >= 48 && codePoint <= 57;
  }

  public static boolean isLetter(int codePoint) {
    return (codePoint >= 48 && codePoint <= 57) || (codePoint >= 65 && codePoint <= 90) || (codePoint >= 97 && codePoint <= 122) || codePoint == 95;
  }

  public static boolean isHex(int codePoint) {
    return (codePoint >= 48 && codePoint <= 57) || (codePoint >= 65 && codePoint <= 70) || (codePoint >= 97 && codePoint <= 102);
  }

  public void close() throws IOException {
    reader.close();
  }

}