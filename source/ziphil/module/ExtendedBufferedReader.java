package ziphil.module;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;


public class ExtendedBufferedReader extends BufferedReader {

  private int lineNumber = 1;
  private int columnNumber = 0;
  private boolean skipsLineFeed = false;
  private boolean passesLineFeed = false;
  private int markedLineNumber = 1;
  private int markedColumnNumber = 0;
  private boolean markedSkipsLineFeed = false;
  private boolean markedPassesLineFeed = false;

  public ExtendedBufferedReader(Reader reader) {
    super(reader);
  }

  public int read() throws IOException {
    synchronized (lock) {
      int result = super.read();
      if (passesLineFeed) {
        lineNumber ++;
        columnNumber = 0;
        passesLineFeed = false;
      }
      columnNumber ++;
      if (skipsLineFeed) {
        skipsLineFeed = false;
        if (result == '\n') {
          result = super.read();
          columnNumber ++;
        }
      }
      if (result == '\r') {
        skipsLineFeed = true;
        passesLineFeed = true;
        result = '\n';
      } else if (result == '\n') {
        passesLineFeed = true;
      }
      return result;
    }
  }

  public int read(char[] buffer, int offset, int length) throws IOException {
    synchronized (lock) {
      int size = super.read(buffer, offset, length);
      for (int i = offset ; i < offset + size ; i ++) {
        int character = buffer[i];
        if (passesLineFeed) {
          lineNumber ++;
          columnNumber = 0;
          passesLineFeed = false;
        }
        columnNumber ++;
        if (skipsLineFeed) {
          skipsLineFeed = false;
          if (character == '\n') {
            continue;
          }
        }
        if (character == '\r') {
          skipsLineFeed = true;
          passesLineFeed = true;
          break;
        } else {
          passesLineFeed = true;
          break;
        }
      }
      return size;
    }
  }

  public String readLine() throws IOException {
    throw new UnsupportedOperationException();
  }

  public long skip(long size) throws IOException {
    throw new UnsupportedOperationException();
  }

  public void mark(int readAheadLimit) throws IOException {
    synchronized (lock) {
      super.mark(readAheadLimit);
      markedLineNumber = lineNumber;
      markedColumnNumber = columnNumber;
      markedSkipsLineFeed = skipsLineFeed;
      markedPassesLineFeed = passesLineFeed;
    }
  }

  public void reset() throws IOException {
    synchronized (lock) {
      super.reset();
      lineNumber = markedLineNumber;
      columnNumber = markedColumnNumber;
      skipsLineFeed = markedSkipsLineFeed;
      passesLineFeed = markedPassesLineFeed;
    }
  }

  public int getLineNumber() {
    return this.lineNumber;
  }

  public int getColumnNumber() {
    return this.columnNumber;
  }

}