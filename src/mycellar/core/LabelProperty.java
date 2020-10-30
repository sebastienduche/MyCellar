package mycellar.core;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2016</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.3
 * @since 30/10/20
 */
public class LabelProperty {

  private final boolean plural;
  private final boolean uppercaseFirst;
  private final Grammar grammar;

  public static final LabelProperty PLURAL = new LabelProperty(true);
  public static final LabelProperty SINGLE = new LabelProperty(false);
  public static final LabelProperty THE_SINGLE = new LabelProperty(false, false, Grammar.THE);
  public static final LabelProperty OF_THE_SINGLE = new LabelProperty(false, false, Grammar.OF_THE);
  public static final LabelProperty THE_PLURAL = new LabelProperty(true, false, Grammar.THE);
  public static final LabelProperty OF_THE_PLURAL = new LabelProperty(true, false, Grammar.OF_THE);
  public static final LabelProperty A_SINGLE = new LabelProperty(false, false, Grammar.SINGLE);


  private LabelProperty(boolean plural, boolean uppercaseFirst, Grammar grammar) {
    this.plural = plural;
    this.uppercaseFirst = uppercaseFirst;
    this.grammar = grammar;
  }

  public LabelProperty(boolean plural) {
    this.plural = plural;
    uppercaseFirst = false;
    grammar = Grammar.NONE;
  }

  public LabelProperty withCapital() {
    return new LabelProperty(plural, true, grammar);
  }

  public boolean isPlural() {
    return plural;
  }

  public boolean isUppercaseFirst() {
    return uppercaseFirst;
  }

  public Grammar getGrammar() {
    return grammar;
  }
}
