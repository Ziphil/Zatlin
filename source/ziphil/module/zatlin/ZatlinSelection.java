package ziphil.module.zatlin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class ZatlinSelection implements ZatlinGeneratable {

  public static final ZatlinSelection EMPTY_SELECTION = new ZatlinSelection();
  private static final Random RANDOM = new Random();

  private List<ZatlinGeneratable> generatables = new ArrayList();
  private List<Integer> weights = new ArrayList();

  public String generate(ZatlinRoot root) {
    String output = "";
    int number = RANDOM.nextInt(totalWeight());
    int currentWeight = 0;
    for (int i = 0 ; i < generatables.size() ; i ++) {
      currentWeight += weights.get(i);
      if (number < currentWeight) {
        output = generatables.get(i).generate(root);
        break;
      }
    }
    return output;
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

  private int totalWeight() {
    int totalWeight = 0;
    for (int weight : weights) {
      totalWeight += weight;
    }
    return totalWeight;
  }

  public String toString() {
    StringBuilder string = new StringBuilder();
    string.append("(");
    for (int i = 0 ; i < generatables.size() ; i ++) {
      string.append(generatables.get(i));
      string.append(" ");
      string.append(weights.get(i));
      if (i < generatables.size() - 1) {
        string.append(" | ");
      }
    }
    string.append(")");
    return string.toString();
  }

  public List<ZatlinGeneratable> getGeneratables() {
    return this.generatables;
  }

  public void setGeneratables(List<ZatlinGeneratable> generatables) {
    this.generatables = generatables;
  }

  public List<Integer> getWeights() {
    return this.weights;
  }

  public void setWeights(List<Integer> weights) {
    this.weights = weights;
  }

}