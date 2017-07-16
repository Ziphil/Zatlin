package ziphil.module.zatlin;

import java.util.List;


public class ZatlinCompound implements ZatlinGeneratable {

  private static final int MAX_TRIAL_NUMBER = 100;

  private ZatlinGeneratable generatable = null;
  private ZatlinMatchable matchable = null;

  public String generate(ZatlinRoot root) {
    String output = "";
    for (int i = 0 ; i < MAX_TRIAL_NUMBER ; i ++) {
      String temporaryOutput = generatable.generate(root);
      if (matchable == null || !matchable.match(temporaryOutput, root)) {
        output = temporaryOutput;
        break;
      }
    }
    return output;
  }

  public ZatlinToken findUnknownIdentifier(ZatlinRoot root) {
    return generatable.findUnknownIdentifier(root);
  }

  public ZatlinToken findCircularIdentifier(List<ZatlinToken> identifiers, ZatlinRoot root) {
    return generatable.findCircularIdentifier(identifiers, root);
  }

  public String toString() {
    StringBuilder string = new StringBuilder();
    string.append(generatable);
    string.append(" - ");
    string.append(matchable);
    return string.toString();
  }

  public ZatlinGeneratable getGeneratable() {
    return this.generatable;
  }

  public void setGeneratable(ZatlinGeneratable generatable) {
    this.generatable = generatable;
  }

  public ZatlinMatchable getMatchable() {
    return this.matchable;
  }

  public void setMatchable(ZatlinMatchable matchable) {
    this.matchable = matchable;
  }

}