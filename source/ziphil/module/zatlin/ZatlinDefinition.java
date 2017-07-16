package ziphil.module.zatlin;

import java.util.ArrayList;
import java.util.List;


public class ZatlinDefinition {

  private ZatlinToken identifier;
  private ZatlinGeneratable content;

  public ZatlinToken findUnknownIdentifier(ZatlinRoot root) {
    return content.findUnknownIdentifier(root);
  } 

  public ZatlinToken findCircularIdentifier(List<ZatlinToken> identifiers, ZatlinRoot root) {
    ArrayList nextIdentifiers = new ArrayList(identifiers);
    nextIdentifiers.add(identifier);
    return content.findCircularIdentifier(nextIdentifiers, root);
  }

  public ZatlinToken findCircularIdentifier(ZatlinRoot root) {
    ArrayList identifiers = new ArrayList();
    identifiers.add(identifier);
    return content.findCircularIdentifier(identifiers, root);
  }

  public String toString() {
    StringBuilder string = new StringBuilder();
    string.append(identifier);
    string.append(" = ");
    string.append(content);
    return string.toString();
  }

  public ZatlinToken getIdentifier() {
    return this.identifier;
  }

  public void setIdentifier(ZatlinToken identifier) {
    this.identifier = identifier;
  }

  public ZatlinGeneratable getContent() {
    return this.content;
  }

  public void setContent(ZatlinGeneratable content) {
    this.content = content;
  }

}