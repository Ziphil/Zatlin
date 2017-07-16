package ziphil.module.zatlin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;


public class Zatlin {

  private ZatlinRoot root;

  public void load(File file) {
    try {
      Reader reader = new InputStreamReader(new FileInputStream(file));
      parse(reader);
    } catch (FileNotFoundException exception) {
      exception.printStackTrace();
      root = null;
    }
  }

  public void load(String source) {
    Reader reader = new StringReader(source);
    parse(reader);
  }

  private void parse(Reader reader) {
    try {
      ZatlinParser parser = new ZatlinParser(reader);
      root = parser.readRoot();
    } catch (IOException exception) {
      exception.printStackTrace();
      root = null;
    }
  }

  public String generate() {
    if (root != null) {
      return root.generate();
    } else {
      return "";
    }
  }

}