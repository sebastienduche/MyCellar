package mycellar.core.text;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2016
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.6
 * @since 17/03/25
 */
public class LabelProperty {

  public static final LabelProperty PLURAL = new LabelProperty(true);
  public static final LabelProperty SINGLE = new LabelProperty(false);
  public static final LabelProperty SINGLE_FOR_ACTION = new LabelProperty(false).withThreeDashes();
  public static final LabelProperty THE_SINGLE = new LabelProperty(false, false, Grammar.THE);
  public static final LabelProperty OF_THE_SINGLE = new LabelProperty(false, false, Grammar.OF_THE);
  public static final LabelProperty THE_PLURAL = new LabelProperty(true, false, Grammar.THE);
  public static final LabelProperty OF_THE_PLURAL = new LabelProperty(true, false, Grammar.OF_THE);
  public static final LabelProperty A_SINGLE = new LabelProperty(false, false, Grammar.SINGLE);
  private final boolean plural;
  private final boolean uppercaseFirst;
  private final Grammar grammar;
  private final boolean doubleQuote;
  private final boolean threeDashes;


  private LabelProperty(boolean plural, boolean uppercaseFirst, Grammar grammar, boolean doubleQuote, boolean threeDashes) {
    this.plural = plural;
    this.uppercaseFirst = uppercaseFirst;
    this.grammar = grammar;
    this.doubleQuote = doubleQuote;
    this.threeDashes = threeDashes;
  }

  private LabelProperty(boolean plural, boolean uppercaseFirst, Grammar grammar) {
    this.plural = plural;
    this.uppercaseFirst = uppercaseFirst;
    this.grammar = grammar;
    doubleQuote = false;
    threeDashes = false;
  }

  public LabelProperty(boolean plural) {
    this.plural = plural;
    uppercaseFirst = false;
    doubleQuote = false;
    threeDashes = false;
    grammar = Grammar.NONE;
  }

  public LabelProperty withCapital() {
    return new LabelProperty(plural, true, grammar);
  }

  public LabelProperty withDoubleQuote() {
    return new LabelProperty(plural, uppercaseFirst, grammar, true, false);
  }

  public LabelProperty withThreeDashes() {
    return new LabelProperty(plural, uppercaseFirst, grammar, false, true);
  }

  boolean isPlural() {
    return plural;
  }

  boolean isUppercaseFirst() {
    return uppercaseFirst;
  }

  boolean isDoubleQuote() {
    return doubleQuote;
  }

  boolean isThreeDashes() {
    return threeDashes;
  }

  Grammar getGrammar() {
    return grammar;
  }
}
