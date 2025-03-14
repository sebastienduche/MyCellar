package mycellar.requester;

import mycellar.Program;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import static mycellar.ProgramConstants.SPACE;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.general.ResourceKey.COLLECTIONFILTER_ERRORFIELDFIELD;
import static mycellar.general.ResourceKey.COLLECTIONFILTER_ERRORFIELDPARENTHESIS;
import static mycellar.general.ResourceKey.COLLECTIONFILTER_ERRORKEYWORDPARAMETER;
import static mycellar.general.ResourceKey.COLLECTIONFILTER_ERRORKEYWORDPARENTHESIS;
import static mycellar.general.ResourceKey.COLLECTIONFILTER_ERRORPARENTHESIS;
import static mycellar.general.ResourceKey.COLLECTIONFILTER_ERRORPARENTHESISKEYWORD;
import static mycellar.general.ResourceKey.COLLECTIONFILTER_ERRORSTART;
import static mycellar.general.ResourceKey.COLLECTIONFILTER_ERRORVALUEREQUIRED;

/**
 * <p>Titre : Cave &agrave; vin
 * <p>Description : Votre description
 * <p>Copyright : Copyright (c) 2014
 * <p>Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 1.0
 * @since 14/03/25
 */

@SuppressWarnings(value = {"rawtypes", "unchecked"})
public class CollectionFilter<T> {

  private static final CollectionFilter INSTANCE = new CollectionFilter();
  private static Collection result = Collections.EMPTY_LIST;
  private static String error = "";

  public static <T> CollectionFilter<T> init(Collection<T> src) {
    result = src;
    return INSTANCE;
  }

  /**
   * Select objects that match the predicates
   *
   * @param src List to filter
   */
  public static <T> CollectionFilter<T> select(Collection<T> src, IPredicate<T> predicate) {
    if (src == null || predicate == null) {
      throw new NullPointerException("The collection of source objets or the predicate is null!");
    }
    result = new ArrayList<T>();
    for (T b : src) {
      if (predicate.apply(b)) {
        result.add(b);
      }
    }
    return INSTANCE;
  }

  /**
   * Select objects that match the predicates with the corresponding value
   *
   * @param src List to filter
   */
  public static <T> CollectionFilter<T> select(Collection<T> src, IPredicate<T> predicate, Object value, int type) {
    if (src == null || predicate == null) {
      throw new NullPointerException("The collection of source objets or the predicate is null!");
    }
    result = new ArrayList<T>();
    for (T b : src) {
      if (predicate.apply(b, value, type)) {
        result.add(b);
      }
    }
    return INSTANCE;
  }

  /**
   * Select objects that match the collection of predicates (with keywords between predicates)
   */
  public static <T> CollectionFilter<T> select(Collection<T> src, Collection<Predicates> predicates) {
    if (src == null || predicates == null) {
      throw new NullPointerException("The collection of source objets or the collection of predicates is null!");
    }
    result = null;
    for (Predicates p : predicates) {
      Debug(p.toString());
    }
    if (!validatePredicates(predicates)) {
      return INSTANCE;
    }

    Debug("Predicates OK");

    Collection<LinkedList<Predicates>> split = splitPredicates(predicates);
    if (split.size() > 1) {
      for (LinkedList<Predicates> list : split) {
        Debug("Display predicates list..");
        StringBuilder sb = new StringBuilder();
        for (Predicates p : list) {
          sb.append(p.toString()).append(SPACE);
        }
        Debug(sb.toString());
      }

      boolean first = true;
      IPredicate last = null;
      Collection resultTmp = null;
      for (LinkedList<Predicates> list : split) {
        // Sur chaque list, on supprime le premier ou le dernier element qui est un mot clef.
        if (Predicates.isKeywordPredicate(list.getLast().getPredicate())) {
          last = list.getLast().getPredicate();
          list.removeLast();
        } else if (Predicates.isKeywordPredicate(list.getFirst().getPredicate())) {
          last = list.getFirst().getPredicate();
          list.removeFirst();
        }

        // On fait la selection
        select(src, list);
        if (first) {
          // Dans le cas du premier passage, on conserve la liste
          // Elle servira pour les intersections et unions des elements suivant
        } else {
          if (Predicates.AND.equals(last)) {
            intersect(resultTmp);
          } else if (Predicates.OR.equals(last)) {
            union(resultTmp);
          }
        }
        resultTmp = result;
        first = false;
      }
    } else {
      // On traite un ensemble de predicats
      IPredicate<?> previous = null;
      Collection<Predicates> predicatesToDo = new ArrayList<>();
      for (Predicates predicate : predicates) {
        if (Predicates.isParenthesisPredicate(predicate.getPredicate())) {
          continue;
        }
        if (Predicates.isKeywordPredicate(predicate.getPredicate())) {
          if (previous == null) {
            previous = predicate.getPredicate();
          }
          if (!previous.equals(predicate.getPredicate())) {
            if (Predicates.AND.equals(previous)) {
              and(predicatesToDo);
            } else if (Predicates.OR.equals(previous)) {
              or(predicatesToDo);
            }
            predicatesToDo.clear();
          }
        } else {
          if (result == null) {
            select(src, predicate);
          } else {
            predicatesToDo.add(predicate);
          }
        }
      }

      if (previous != null) {
        if (Predicates.AND.equals(previous)) {
          and(predicatesToDo);
        } else if (Predicates.OR.equals(previous)) {
          Collection resultTmp = result;
          select(src, predicatesToDo);
          union(resultTmp);
        }
      }
    }
    return INSTANCE;
  }

  /**
   * Select objects that matches the {@link Predicates}
   */
  private static <T> CollectionFilter<T> select(Collection<T> src, Predicates predicate) {
    if (src == null || predicate == null) {
      throw new NullPointerException("The collection of source objets or the predicate is null!");
    }
    result = new ArrayList<T>();
    for (T b : src) {
      if (predicate.getPredicate().apply(b, predicate.getValue(), predicate.getType())) {
        result.add(b);
      }
    }
    return INSTANCE;
  }

  /**
   * Filter the existing list with AND condition on the predicate
   */
  public static <T> CollectionFilter<T> and(IPredicate<T> predicate) {
    if (predicate == null) {
      throw new NullPointerException("The predicate is null!");
    }
    Collection<T> result1 = new ArrayList<>();
    for (Object b : result) {
      if (predicate.apply((T) b)) {
        result1.add((T) b);
      }
    }
    result = result1;
    return INSTANCE;
  }

  /**
   * Filter the existing list with AND condition on the predicate with the corresponding value
   */
  public static <T> CollectionFilter<T> and(IPredicate<T> predicate, Object value, int type) {
    if (predicate == null) {
      throw new NullPointerException("The predicate is null!");
    }
    Collection<T> result1 = new ArrayList<>();
    for (Object b : result) {
      if (predicate.apply((T) b, value, type)) {
        result1.add((T) b);
      }
    }
    result = result1;
    return INSTANCE;
  }

  /**
   * Filter the existing list with AND condition on the {@link Predicates}
   */
  public static <T> CollectionFilter<T> and(Predicates predicate) {
    if (predicate == null) {
      throw new NullPointerException("The predicate is null!");
    }
    Collection<T> result1 = new ArrayList<>();
    for (Object b : result) {
      if (predicate.getPredicate().apply(b, predicate.getValue(), predicate.getType())) {
        result1.add((T) b);
      }
    }
    result = result1;
    return INSTANCE;
  }

  /**
   * Filter the existing list with AND condition for the collection of {@link Predicates}
   */
  private static <T> CollectionFilter<T> and(Collection<Predicates> predicates) {
    if (predicates == null) {
      throw new NullPointerException("The collection of predicates is null!");
    }
    Collection<T> result1 = new ArrayList<>();
    for (Object b : result) {
      boolean apply = true;
      for (Predicates predicate : predicates) {
        if (!predicate.getPredicate().apply(b, predicate.getValue(), predicate.getType())) {
          apply = false;
          break;
        }
      }
      if (apply) {
        result1.add((T) b);
      }
    }
    result = result1;
    return INSTANCE;
  }

  /**
   * Filter the source list with AND condition for the collection of {@link Predicates}
   */
  public static <T> CollectionFilter<T> and(Collection<T> src, Collection<Predicates> predicates) {
    if (src == null || predicates == null) {
      throw new NullPointerException("The collection of source objets or the collection of predicates is null!");
    }
    result = new ArrayList<T>();
    for (Object b : src) {
      boolean apply = true;
      for (Predicates predicate : predicates) {
        if (!predicate.getPredicate().apply(b, predicate.getValue(), predicate.getType())) {
          apply = false;
          break;
        }
      }
      if (apply) {
        result.add(b);
      }
    }
    return INSTANCE;
  }

  /**
   * Union the existing list with the source liste
   */
  private static <T> CollectionFilter<T> union(Collection<T> src) {
    if (src == null) {
      throw new NullPointerException("The collection of source object is null!");
    }
    for (Object b : src) {
      if (!result.contains(b)) {
        result.add(b);
      }
    }
    return INSTANCE;
  }

  /**
   * Filter the existing list to keep items that are included in the source list
   */
  private static <T> CollectionFilter<T> intersect(Collection<T> src) {
    if (src == null) {
      throw new NullPointerException("The collection of source object is null!");
    }
    Collection<T> list = new ArrayList<>();
    for (Object t : result) {
      if (src.contains(t)) {
        list.add((T) t);
      }
    }
    result = list;
    return INSTANCE;
  }

  /**
   * Filter the existing list with OR condition for the collection of {@link Predicates}
   */
  public static <T> CollectionFilter<T> or(Collection<Predicates> predicates) {
    if (predicates == null) {
      throw new NullPointerException("The collection of predicates is null!");
    }
    Collection<T> result1 = new ArrayList<>();
    for (Object b : result) {
      boolean apply = false;
      for (Predicates predicate : predicates) {
        if (predicate.getPredicate().apply(b, predicate.getValue(), predicate.getType())) {
          apply = true;
          break;
        }
      }
      if (apply) {
        result1.add((T) b);
      }
    }
    result = result1;
    return INSTANCE;
  }

  /**
   * Filter the source list with OR condition for the collection of {@link Predicates}
   *
   * @param src Source list to filter
   */
  public static <T> CollectionFilter<T> or(Collection<T> src, Collection<Predicates> predicates) {
    if (src == null || predicates == null) {
      throw new NullPointerException("The collection of source objets or the collection of predicates is null!");
    }
    result = new ArrayList<T>();
    for (Object b : src) {
      boolean apply = false;
      for (Predicates predicate : predicates) {
        if (predicate.getPredicate().apply(b, predicate.getValue(), predicate.getType())) {
          apply = true;
          break;
        }
      }
      if (apply) {
        result.add(b);
      }
    }
    return INSTANCE;
  }

  /**
   * Validate the collection of predicates
   */
  public static boolean validatePredicates(Collection<Predicates> predicates) {
    if (predicates == null) {
      return true;
    }
    error = null;
    int closeParenthesis;
    int openParenthesis = closeParenthesis = 0;
    boolean first = true;
    IPredicate<?> previous = null;
    for (Predicates predicate : predicates) {
      if (first && Predicates.isKeywordPredicate(predicate.getPredicate())) {
        error = getLabel(COLLECTIONFILTER_ERRORSTART);
        Debug("Cant start by AND/OR");
        return false;
      }
      if (predicate.getPredicate().isValueRequired()) {
        if (predicate.getValue() == null) {
          error = getLabel(COLLECTIONFILTER_ERRORVALUEREQUIRED);
          Debug("Value required for this predicate");
          return false;
        }
        if (predicate.getPredicate().isEmptyValueForbidden() && predicate.getValue().toString().isEmpty()) {
          error = getLabel(COLLECTIONFILTER_ERRORVALUEREQUIRED);
          Debug("Value required for this predicate");
          return false;
        }
      }
      if (previous != null) {
        if ((Predicates.OPEN_PARENTHESIS.equals(previous) || Predicates.isKeywordPredicate(previous)) && Predicates.isKeywordPredicate(predicate.getPredicate())) {
          error = getLabel(COLLECTIONFILTER_ERRORKEYWORDPARAMETER);
          Debug("Cant put AND/OR after open parenthesis or keyword");
          return false;
        }
        if (Predicates.OPEN_PARENTHESIS.equals(previous) && Predicates.isKeywordPredicate(predicate.getPredicate())) {
          error = getLabel(COLLECTIONFILTER_ERRORKEYWORDPARENTHESIS);
          Debug("Cant put keyword after open parenthesis");
          return false;
        }
        if (Predicates.CLOSE_PARENTHESIS.equals(predicate.getPredicate()) && Predicates.isKeywordPredicate(previous)) {
          error = getLabel(COLLECTIONFILTER_ERRORPARENTHESISKEYWORD);
          Debug("Cant put close parenthesis after keyword");
          return false;
        }
        if (Predicates.CLOSE_PARENTHESIS.equals(previous) && !Predicates.isKeywordPredicate(predicate.getPredicate())) {
          error = getLabel(COLLECTIONFILTER_ERRORFIELDPARENTHESIS);
          Debug("Cant put field after close parenthesis");
          return false;
        }
        if (Predicates.isFieldPredicate(predicate.getPredicate()) && Predicates.isFieldPredicate(previous)) {
          error = getLabel(COLLECTIONFILTER_ERRORFIELDFIELD);
          Debug("Cant put field after field");
          return false;
        }
      }
      if (Predicates.OPEN_PARENTHESIS.equals(predicate.getPredicate())) {
        openParenthesis++;
      }
      if (Predicates.CLOSE_PARENTHESIS.equals(predicate.getPredicate())) {
        closeParenthesis++;
      }
      previous = predicate.getPredicate();
      first = false;
    }
    if (openParenthesis != closeParenthesis) {
      error = getLabel(COLLECTIONFILTER_ERRORPARENTHESIS);
      Debug("Should have the same number of open and close parenthesis");
      return false;
    }
    return true;
  }

  /**
   * Get the error to display
   */
  public static String getError() {
    return error;
  }

  /**
   * Split the list of predicates in a collection of predicates to do (group by parenthesis)
   */
  private static Collection<LinkedList<Predicates>> splitPredicates(Collection<Predicates> predicates) {
    Collection<LinkedList<Predicates>> predicatesToDo = new LinkedList<>();
    if (predicates == null) {
      return predicatesToDo;
    }
    LinkedList<Predicates> predicatesToDoParenthesis = null;
    LinkedList<Predicates> predicatesToDoNoParenthesis = null;
    for (Predicates predicate : predicates) {
      if (predicate.getPredicate().equals(Predicates.OPEN_PARENTHESIS)) {
        if (predicatesToDoNoParenthesis != null) {
          predicatesToDo.add(predicatesToDoNoParenthesis);
          predicatesToDoNoParenthesis = null;
        }
        predicatesToDoParenthesis = new LinkedList<>();
        continue;
      } else if (predicate.getPredicate().equals(Predicates.CLOSE_PARENTHESIS)) {
        if (predicatesToDoParenthesis != null) {
          predicatesToDo.add(predicatesToDoParenthesis);
        }
        predicatesToDoParenthesis = null;
        continue;
      }
      if (predicatesToDoParenthesis == null) {
        if (predicatesToDoNoParenthesis == null) {
          predicatesToDoNoParenthesis = new LinkedList<>();
        }
        if (!Predicates.isParenthesisPredicate(predicate.getPredicate())) {
          predicatesToDoNoParenthesis.add(predicate);
        }
      } else {
        if (!Predicates.isParenthesisPredicate(predicate.getPredicate())) {
          predicatesToDoParenthesis.add(predicate);
        }
      }
    }

    if (predicatesToDoNoParenthesis != null && !predicatesToDoNoParenthesis.isEmpty()) {
      predicatesToDo.add(predicatesToDoNoParenthesis);
    }

    return predicatesToDo;
  }

  private static void Debug(String text) {
    Program.Debug("CollectionFilter: " + text);
  }

  /**
   * Get the result list
   */
  public Collection<T> getResults() {
    return result;
  }

  /**
   * Filter the existing list with OR condition on the predicate
   */
  public CollectionFilter<T> or(Collection<T> src, IPredicate<T> predicate) {
    if (src == null || predicate == null) {
      throw new NullPointerException("The collection of source objets or the predicate is null!");
    }
    for (T b : src) {
      if (predicate.apply(b) && !result.contains(b)) {
        result.add(b);
      }
    }
    return INSTANCE;
  }

  /**
   * Filter the existing list with OR condition on the predicate with the corresponding value
   */
  public CollectionFilter<T> or(Collection<T> src, IPredicate<T> predicate, Object value, int type) {
    if (src == null || predicate == null) {
      throw new NullPointerException("The collection of source objets or the predicate is null!");
    }
    for (T b : src) {
      if (predicate.apply(b, value, type) && !result.contains(b)) {
        result.add(b);
      }
    }
    return INSTANCE;
  }
}
