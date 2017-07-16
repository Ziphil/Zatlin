package ziphil.module.zatlin;

import java.util.ArrayList;
import java.util.List;


public class ZatlinSequence implements ZatlinGeneratable {

  public static final ZatlinSequence EMPTY_SEQUENCE = new ZatlinSequence();

  private List<ZatlinGeneratable> generatables = new ArrayList();

  public String generate(ZatlinRoot root) {
    StringBuilder output = new StringBuilder();
    for (ZatlinGeneratable generatable : generatables) {
      output.append(generatable.generate(root));
    }
    return output.toString();
  }

  public ZatlinToken findUnknownIdentifier(ZatlinRoot root) {
    for (ZatlinGeneratable generatable : generatables) {
      ZatlinToken unknownIdentifier = generatable.findUnknownIdentifier(root);
      if (unknownIdentifier != null) {
        return unknownIdentifier;
      }
    }
    return null;
  }

  public ZatlinToken findCircularIdentifier(List<ZatlinToken> identifiers, ZatlinRoot root) {
    for (ZatlinGeneratable generatable : generatables) {
      ZatlinToken circularIdentifier = generatable.findCircularIdentifier(identifiers, root);
      if (circularIdentifier != null) {
        return circularIdentifier;
      }
    }
    return null;
  }

  public String toString() {
    StringBuilder string = new StringBuilder();
    for (int i = 0 ; i < generatables.size() ; i ++) {
      string.append(generatables.get(i));
      if (i < generatables.size() - 1) {
        string.append(" ");
      }
    }
    return string.toString();
  }

  public boolean hasGeneratable() {
    return !generatables.isEmpty();
  }

  public List<ZatlinGeneratable> getGeneratables() {
    return this.generatables;
  }

  public void setGeneratables(List<ZatlinGeneratable> generatables) {
    this.generatables = generatables;
  }

}