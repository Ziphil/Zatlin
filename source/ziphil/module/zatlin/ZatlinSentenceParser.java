package ziphil.module.zatlin;

import java.util.ArrayList;
import java.util.List;


public class ZatlinSentenceParser {

  private List<ZatlinToken> tokens = new ArrayList();
  private int pointer = 0;

  public ZatlinSentenceParser(List<ZatlinToken> tokens) {
    tokens = tokens;
  }

  public ZatlinSentenceParser() {
  }

  public ZatlinDefinition readDefinition() {
    if (tokens.size() >= 4 && tokens.get(0).getType().equals(ZatlinTokenType.IDENTIFIER) && tokens.get(1).getType().equals(ZatlinTokenType.EQUAL)) {
      pointer += 2;
      ZatlinDefinition definition = new ZatlinDefinition();
      ZatlinToken identifier = tokens.get(0);
      ZatlinGeneratable content = nextCompound();
      definition.setIdentifier(identifier);
      definition.setContent(content);
      ZatlinToken token = tokens.get(pointer ++);
      ZatlinTokenType tokenType = (token != null) ? token.getType() : null;
      if (tokenType != null && tokenType.equals(ZatlinTokenType.SEMICOLON)) {
        return definition;
      } else {
        throw new ZatlinParseException("Invalid definition sentence", token);
      }
    } else {
      throw new ZatlinParseException("Invalid definition sentence", tokens.get(tokens.size() - 1));
    }
  }

  public ZatlinGeneratable readMainGeneratable() {
    if (tokens.size() >= 3 && tokens.get(0).getType().equals(ZatlinTokenType.PERCENT)) {
      pointer += 1;
      ZatlinGeneratable mainGeneratable = nextCompound();
      ZatlinToken token = tokens.get(pointer ++);
      ZatlinTokenType tokenType = (token != null) ? token.getType() : null;
      if (tokenType != null && tokenType.equals(ZatlinTokenType.SEMICOLON)) {
        return mainGeneratable;
      } else {
        throw new ZatlinParseException("Invalid definition sentence", token);
      }
    } else {
      throw new ZatlinParseException("Invalid definition sentence", tokens.get(tokens.size() - 1));
    }
  }

  private ZatlinCompound nextCompound() {
    ZatlinCompound compound = new ZatlinCompound();
    ZatlinGeneratable generatable = nextSelection();
    ZatlinToken token = tokens.get(pointer ++);
    ZatlinTokenType tokenType = (token != null) ? token.getType() : null;
    if (tokenType != null && tokenType.equals(ZatlinTokenType.MINUS)) {
      ZatlinMatchable matchable = nextDisjunction();
      compound.setGeneratable(generatable);
      compound.setMatchable(matchable);
    } else if (tokenType != null && tokenType.equals(ZatlinTokenType.SEMICOLON)) {
      pointer --;
      compound.setGeneratable(generatable);
    } else {
      throw new ZatlinParseException("Invalid definition sentence", token);
    }
    return compound;
  }

  private ZatlinSelection nextSelection() {
    ZatlinSelection selection = new ZatlinSelection();
    ZatlinSequence sequence = new ZatlinSequence();
    int weight = 1;
    boolean hasWeight = false;
    while (true) {
      ZatlinToken token = tokens.get(pointer ++);
      ZatlinTokenType tokenType = (token != null) ? token.getType() : null;
      if (tokenType != null && (tokenType.equals(ZatlinTokenType.QUOTE_LITERAL) || tokenType.equals(ZatlinTokenType.IDENTIFIER))) {
        if (!hasWeight) {
          sequence.getGeneratables().add(token);
        } else {
          throw new ZatlinParseException("Weight is not at the rightmost", token);
        }
      } else if (tokenType != null && tokenType.equals(ZatlinTokenType.NUMERIC)) {
        weight = Integer.parseInt(token.getText());
        hasWeight = true;
      } else if (tokenType != null && tokenType.equals(ZatlinTokenType.VERTICAL)) {
        if (sequence.hasGeneratable()) {
          selection.getGeneratables().add(sequence);
          selection.getWeights().add(weight);
          sequence = new ZatlinSequence();
          weight = 1;
          hasWeight = false;
        } else {
          throw new ZatlinParseException("Invalid selection expression", token);
        }
      } else {
        if (sequence.hasGeneratable()) {
          pointer --;
          selection.getGeneratables().add(sequence);
          selection.getWeights().add(weight);
          break;
        } else {
          throw new ZatlinParseException("Invalid selection expression", token);
        }
      }
    }
    return selection;
  }

  private ZatlinDisjunction nextDisjunction() {
    ZatlinDisjunction disjunction = new ZatlinDisjunction();
    ZatlinPattern pattern = new ZatlinPattern();
    while (true) {
      ZatlinToken token = tokens.get(pointer ++);
      ZatlinTokenType tokenType = (token != null) ? token.getType() : null;
      if (tokenType != null && tokenType.equals(ZatlinTokenType.QUOTE_LITERAL)) {
        if (!pattern.hasToken()) {
          pattern.setToken(token);
        } else {
          throw new ZatlinParseException("Two or more quote literals in the single expression", token);
        }
      } else if (tokenType != null && tokenType.equals(ZatlinTokenType.CIRCUMFLEX)) {
        if (pattern.hasToken()) {
          if (!pattern.isTrailing()) {
            pattern.setTrailing(true);
          } else {
            throw new ZatlinParseException("Duplicate circumflex", token);
          }
        } else {
          if (!pattern.isLeading()) {
            pattern.setLeading(true);
          } else {
            throw new ZatlinParseException("Duplicate circumflex", token);
          }
        }
      } else if (tokenType != null && tokenType.equals(ZatlinTokenType.VERTICAL)) {
        if (pattern.hasToken()) {
          disjunction.getMatchables().add(pattern);
          pattern = new ZatlinPattern();
        } else {
          throw new ZatlinParseException("Invalid disjunction expression", token);
        }
      } else {
        if (pattern.hasToken()) {
          pointer --;
          disjunction.getMatchables().add(pattern);
          break;
        } else {
          throw new ZatlinParseException("Invalid disjunction expression", token);
        }
      }
    }
    return disjunction;
  }

  public boolean isDefinition() {
    return !tokens.isEmpty() && !tokens.get(0).getType().equals(ZatlinTokenType.PERCENT);
  }

  public boolean isMainGeneratable() {
    return !tokens.isEmpty() && tokens.get(0).getType().equals(ZatlinTokenType.PERCENT);
  }

  public void clear() {
    tokens.clear();
    pointer = 0;
  }

  public void addToken(ZatlinToken token) {
    tokens.add(token);
  }

  public List<ZatlinToken> getTokens() {
    return tokens;
  }

  public void setTokens(List<ZatlinToken> tokens) {
    tokens = tokens;
  }

}