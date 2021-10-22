package mycellar.requester;

import mycellar.Program;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2014</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.8
 * @since 10/09/21
 */

@SuppressWarnings(value = {"rawtypes", "unchecked"})
public class CollectionFilter<T> {

  private static final CollectionFilter INSTANCE = new CollectionFilter();
  private static final Log LOGGER = LogFactory.getLog(CollectionFilter.class);
  private static Collection result = Collections.EMPTY_LIST;
  private static String error = "";

  public static <T> CollectionFilter<T> init(Collection<T> src) {
    result = src;
    return INSTANCE;
  }

  /**
   * Select objects that matches the predicates
   *
   * @param src       List to filter
   * @param predicate
   * @return
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
   * Select objects that matches the predicates with the corresponding value
   *
   * @param src       List to filter
   * @param predicate
   * @param value
   * @param type
   * @return
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
   * Select objects that matches the collection of predicates (with keywords between predicates)
   *
   * @param src
   * @param predicates
   * @return
   */
  public static <T> CollectionFilter<T> select(Collection<T> src, Collection<Predicates> predicates) {
    if (src == null || predicates == null) {
      throw new NullPointerException("The collection of source objets or the collection of predicates is null!");
    }
    result = null;
    for (Predicates p : predicates) {
      LOGGER.debug(p.toString());
    }
    if (!validatePredicates(predicates)) {
      return INSTANCE;
    }

    LOGGER.debug("Predicates OK");

    Collection<LinkedList<Predicates>> split = splitPredicates(predicates);
    if (split.size() > 1) {
      for (LinkedList<Predicates> list : split) {
        LOGGER.debug("Display List");
        if (LOGGER.isDebugEnabled()) {
          StringBuilder sb = new StringBuilder();
          for (Predicates p : list) {
            sb.append(p.toString()).append(" ");
          }
          LOGGER.debug(sb.toString());
        }
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
   *
   * @param src
   * @param predicate
   * @return
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
   *
   * @param predicate
   * @return
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
   *
   * @param predicate
   * @param value
   * @param type
   * @return
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
   *
   * @param predicate
   * @return
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
   *
   * @param predicates
   * @return
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
   *
   * @param src        Source list to filter
   * @param predicates
   * @return
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
   *
   * @param src
   * @return
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
   * Filter the existing list to keep items that are include in the source list
   *
   * @param src
   * @return
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
   *
   * @param predicates
   * @return
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
   * @param src        Source list to filter
   * @param predicates
   * @return
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
   *
   * @param predicates
   * @return
   */
  public static boolean validatePredicates(Collection<Predicates> predicates) {
    if (predicates == null) {
      return true;
    }
    error = null;
    boolean first = true;
    IPredicate<?> previous = null;
    int openParenthesis, closeParenthesis;
    openParenthesis = closeParenthesis = 0;
    for (Predicates predicate : predicates) {
      if (first && Predicates.isKeywordPredicate(predicate.getPredicate())) {
        error = Program.getLabel("CollectionFilter.ErrorStart");
        LOGGER.debug("Cant start by AND/OR");
        return false;
      }
      if (predicate.getPredicate().isValueRequired()) {
        if (predicate.getValue() == null) {
          error = Program.getLabel("CollectionFilter.ErrorValueRequired");
          LOGGER.debug("Value required for this predicate");
          return false;
        }
        if (predicate.getPredicate().isEmptyValueForbidden() && predicate.getValue().toString().isEmpty()) {
          error = Program.getLabel("CollectionFilter.ErrorValueRequired");
          LOGGER.debug("Value required for this predicate");
          return false;
        }
      }
      if (previous != null) {
        if ((Predicates.OPEN_PARENTHESIS.equals(previous) || Predicates.isKeywordPredicate(previous)) && Predicates.isKeywordPredicate(predicate.getPredicate())) {
          error = Program.getLabel("CollectionFilter.ErrorKeywordParameter");
          LOGGER.debug("Cant put AND/OR after open parenthesis or keyword");
          return false;
        }
        if (Predicates.OPEN_PARENTHESIS.equals(previous) && Predicates.isKeywordPredicate(predicate.getPredicate())) {
          error = Program.getLabel("CollectionFilter.ErrorKeywordParenthesis");
          LOGGER.debug("Cant put keyword after open parenthesis");
          return false;
        }
        if (Predicates.CLOSE_PARENTHESIS.equals(predicate.getPredicate()) && Predicates.isKeywordPredicate(previous)) {
          error = Program.getLabel("CollectionFilter.ErrorParenthesisKeyword");
          LOGGER.debug("Cant put close parenthesis after keyword");
          return false;
        }
        if (Predicates.CLOSE_PARENTHESIS.equals(previous) && !Predicates.isKeywordPredicate(predicate.getPredicate())) {
          error = Program.getLabel("CollectionFilter.ErrorFieldParenthesis");
          LOGGER.debug("Cant put field after close parenthesis");
          return false;
        }
        if (Predicates.isFieldPredicate(predicate.getPredicate()) && Predicates.isFieldPredicate(previous)) {
          error = Program.getLabel("CollectionFilter.ErrorFieldField");
          LOGGER.debug("Cant put field after field");
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
      error = Program.getLabel("CollectionFilter.ErrorParenthesis");
      LOGGER.debug("Should have the same number of open and close parenthesis");
      return false;
    }
    return true;
  }

  /**
   * Get the error to display
   *
   * @return
   */
  public static String getError() {
    return error;
  }

  /**
   * Split the list of predicates in a collection of predicates to do (group by parenthesis)
   *
   * @param predicates
   * @return
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

  /**
   * Get the result list
   *
   * @return
   */
  public Collection<T> getResults() {
    return result;
  }

  /**
   * Filter the existing list with OR condition on the predicate
   *
   * @param predicate
   * @return
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
   *
   * @param predicate
   * @param value
   * @return
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
