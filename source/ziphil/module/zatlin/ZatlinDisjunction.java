package ziphil.module.zatlin;

import java.util.ArrayList;
import java.util.List;


public class ZatlinDisjunction implements ZatlinMatchable {

  public static final ZatlinDisjunction EMPTY_DISJUNCTION = new ZatlinDisjunction();

  private List<ZatlinMatchable> matchables = new ArrayList();

  public boolean match(String input, ZatlinRoot root) {
    boolean predicate = false;
    for (ZatlinMatchable matchable : matchables) {
      if (matchable.match(input, root)) {
        predicate = true;
        break;
      }
    }
    return predicate;
  }

  public String toString() {
    StringBuilder string = new StringBuilder();
    string.append("(");
    for (int i = 0 ; i < matchables.size() ; i ++) {
      string.append(matchables.get(i));
      if (i < matchables.size() - 1) {
        string.append(" | ");
      }
    }
    string.append(")");
    return string.toString();
  }

  public List<ZatlinMatchable> getMatchables() {
    return this.matchables;
  }

  public void setMatchables(List<ZatlinMatchable> matchables) {
    this.matchables = matchables;
  }

}