package plume;

import java.io.Serializable;
import java.util.*;


/**
 * LimitedSizeSet stores up to some maximum number of unique
 * values, at which point its rep is nulled, in order to save space.
 **/
public class LimitedSizeSet<T>
  implements Serializable, Cloneable
{
  // We are Serializable, so we specify a version to allow changes to
  // method signatures without breaking serialization.  If you add or
  // remove fields, you should change this number to the current date.
  static final long serialVersionUID = 20031021L;

  // public final int max_values;

  // If null, then at least num_values distinct values have been seen.
  // The size is not separately stored, because that would take extra space.
  protected /*@Nullable*/ T /*@Nullable*/ [] values;
  // The number of active elements (equivalently, the first unused index).
  int num_values;

  public LimitedSizeSet(int max_values) {
    assert max_values > 0;
    // this.max_values = max_values;
    @SuppressWarnings("unchecked")
    /*@Nullable*/ T[] new_values_array = (/*@Nullable*/ T[]) new /*@Nullable*/ Object[max_values];
    values = new_values_array;
    num_values = 0;
  }

  public void add(T elt) {
    if (values == null)
      return;

    if (contains(elt)) {
      return;
    }
    if (num_values == values.length) {
      values = null;
      num_values++;
      return;
    }
    values[num_values] = elt;
    num_values++;
  }

  public void addAll(LimitedSizeSet<? extends T> s) {
    if (repNulled())
      return;
    if (s.repNulled()) {
      int values_length = values.length;
      // We don't know whether the elements of this and the argument were
      // disjoint.  There might be anywhere from max(size(), s.size()) to
      // (size() + s.size()) elements in the resulting set.
      if (s.size() > values_length) {
        num_values = values_length+1;
        values = null;
        return;
      } else {
        throw new Error("Arg is rep-nulled, so we don't know its values and can't add them to this.");
      }
    }
    for (int i=0; i<s.size(); i++) {
      assert s.values != null : "@SuppressWarnings(nullness): no relevant side effect:  add's side effects do not affect s.values, whether or not this == s";
      add(s.values[i]);
      if (repNulled()) {
        return;                 // optimization, not necessary for correctness
      }
    }
  }

  /*@Pure*/
  public boolean contains(T elt) {
    if (values == null) {
      throw new UnsupportedOperationException();
    }
    for (int i=0; i < num_values; i++) {
      @SuppressWarnings("nullness") // object invariant: used portion of array
      T value = values[i];
      if (value.equals(elt)) {
        return true;
      }
    }
    return false;
  }


  /**
   * A lower bound on the number of elements in the set.  Returns either
   * the number of elements that have been inserted in the set, or
   * max_size(), whichever is less.
   **/
  /*@Pure*/
  public int size() {
    return num_values;
  }

  /**
   * An upper bound how many distinct elements can be individually
   * represented in the set.
   * Returns max_values+1 (where max_values is the argument to the constructor).
   **/
  public int max_size() {
    if (values == null) {
      return num_values;
    } else {
      return values.length + 1;
    }
  }

  /*@AssertNonNullIfFalse("values")*/
  /*@Pure*/
  public boolean repNulled() {
    return values == null;
  }

  public LimitedSizeSet<T> clone() {
    LimitedSizeSet<T> result;
    try {
      @SuppressWarnings("unchecked")
      LimitedSizeSet<T> result_as_lss = (LimitedSizeSet<T>) super.clone();
      result = result_as_lss;
    } catch (CloneNotSupportedException e) {
      throw new Error(); // can't happen
    }
    if (values != null) {
      result.values = values.clone();
    }
    return result;
  }


  /**
   * Merges a list of LimitedSizeSet&lt;T&gt; objects into a single object that
   * represents the values seen by the entire list.  Returns the new
   * object, whose max_values is the given integer.
   **/
  public static <T> LimitedSizeSet<T> merge(int max_values, List<LimitedSizeSet<? extends T>> slist) {
    LimitedSizeSet<T> result = new LimitedSizeSet<T>(max_values);
    for (LimitedSizeSet<? extends T> s : slist) {
      result.addAll(s);
    }
    return result;
  }

  @SuppressWarnings("nullness") // bug in flow; to fix later
  public String toString() {
    return ("[size=" + size() + "; " +
            ((values == null) ? "null" : ArraysMDE.toString(values))
            + "]");
  }

}
