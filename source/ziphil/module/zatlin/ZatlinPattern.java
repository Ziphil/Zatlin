package ziphil.module.zatlin;


public class ZatlinPattern implements ZatlinMatchable {

  private ZatlinToken token;
  private boolean leading = false;
  private boolean trailing = false;

  public boolean match(String input, ZatlinRoot root) {
    boolean predicate = false;
    if (leading && trailing) {
      predicate = input.equals(token.getText());
    } else if (leading && !trailing) {
      predicate = input.startsWith(token.getText());
    } else if (!leading && trailing) {
      predicate = input.endsWith(token.getText());
    } else {
      predicate = input.contains(token.getText());
    }
    return predicate;
  }

  public String toString() {
    StringBuilder string = new StringBuilder();
    if (leading) {
      string.append("^ ");
    }
    string.append(token);
    if (trailing) {
      string.append(" ^");
    }
    return string.toString();
  }

  public boolean hasToken() {
    return token != null;
  }

  public ZatlinToken getToken() {
    return this.token;
  }

  public void setToken(ZatlinToken token) {
    this.token = token;
  }

  public boolean isLeading() {
    return this.leading;
  }

  public void setLeading(boolean leading) {
    this.leading = leading;
  }

  public boolean isTrailing() {
    return this.trailing;
  }

  public void setTrailing(boolean trailing) {
    this.trailing = trailing;
  }

}