package plume;

import java.util.*;

/**
 * Given two sorted iterators, this class returns a new iterator that pairs
 * equal elements of the inputs, according to the sort order or the given
 * comparator.  If an element has no equal element in the other iterator,
 * then the element is paired with null.
 * <p>
 * 
 * For example, suppose that the inputs are
 *   [1, 2, 3, 5] and
 *   [1, 3, 5, 7, 9].
 * Then the output is
 *   [(1,1), (2,null), (3,3), (5,5), (null,7), (null, 9)].
 * <p>
 * 
 * (This operation is similar to, but not the same as, the operation called
 * "zipping".)
 * <p>
 *
 * In some cases this is just the right abstraction.  But in some cases
 * it's appropriate to use set intersection/difference instead.
 */
// T need not extend Comparable<T>, because a comparator can be passed in.
public class OrderedPairIterator<T> implements java.util.Iterator<Pair</*@Nullable*/ T,/*@Nullable*/ T>> {

  Iterator<T> itor1, itor2;
  /*@Nullable*/ T next1, next2;
  /*@Nullable*/ Comparator<? super T> comparator;

  // For this constructor, the arg type is actually Iterator<T extends
  // Comparable<T>>, but T is already bound above and can't be changed.
  public OrderedPairIterator(Iterator<T> itor1, Iterator<T> itor2) {
    this.itor1 = itor1;
    this.itor2 = itor2;
    setnext1();
    setnext2();
  }
  public OrderedPairIterator(Iterator<T> itor1, Iterator<T> itor2, Comparator<T> comparator) {
    this(itor1, itor2);
    this.comparator = comparator;
  }
  /** Set the next1 variable. */
  private void setnext1() {
    next1 = itor1.hasNext() ? itor1.next() : null;
  }
  /** Set the next2 variable. */
  private void setnext2() {
    next2 = itor2.hasNext() ? itor2.next() : null;
  }
  // Have the caller do this directly, probably.
  // public OrderedPairIterator(Set s1, Set s2) {
  //   this((new TreeSet(s1)).iterator(), (new TreeSet(s2)).iterator());
  // }
  @Override
  public boolean hasNext() { return ((next1 != null) || (next2 != null)); }
  /** Return an element of the first iterator, paired with null. */
  private Pair</*@Nullable*/ T,/*@Nullable*/ T> return1() {
    Pair</*@Nullable*/ T,/*@Nullable*/ T> result = Pair.</*@Nullable*/ T,/*@Nullable*/ T>of(next1, (/*@Nullable*/ T)null);
    setnext1();
    return result;
  }
  /** Return a pair of null and an element of the second iterator. */
  private Pair</*@Nullable*/ T,/*@Nullable*/ T> return2() {
    Pair</*@Nullable*/ T,/*@Nullable*/ T> result = Pair.</*@Nullable*/ T,/*@Nullable*/ T>of((/*@Nullable*/ T)null, next2);
    setnext2();
    return result;
  }
  /** Return a pair containing an element from each iterator. */
  private Pair</*@Nullable*/ T,/*@Nullable*/ T> returnboth() {
    Pair</*@Nullable*/ T,/*@Nullable*/ T> result = Pair.</*@Nullable*/ T,/*@Nullable*/ T>of(next1, next2);
    setnext1();
    setnext2();
    return result;
  }

  @Override
  public Pair</*@Nullable*/ T,/*@Nullable*/ T> next() {
    if (next1 == null) {
      if (next2 == null) {
        throw new NoSuchElementException();
      } else {
        return return2();
      }
    } else {
      if (next2 == null) {
        return return1();
      } else {
        int comparison;
        // Either T extends Comparable<T>, or else a comparator was passed in.
        if (comparator == null) {
          @SuppressWarnings("unchecked")
          Comparable<T> cble1 = (Comparable<T>)next1;
          comparison = cble1.compareTo(next2);
        } else {
          comparison = comparator.compare(next1, next2);
        }
        if (comparison < 0)
          return return1();
        else if (comparison > 0)
          return return2();
        else
          return returnboth();
      }
    }
  }
  @Override
  public void remove() { throw new UnsupportedOperationException(); }
}
