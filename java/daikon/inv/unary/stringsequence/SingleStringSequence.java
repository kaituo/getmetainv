package daikon.inv.unary.stringsequence;

import daikon.*;
import daikon.inv.*;
import daikon.inv.unary.UnaryInvariant;
import plume.*;

/**
 * Abstract base class used to evaluate single string sequences.
 **/
public abstract class SingleStringSequence
  extends UnaryInvariant
{
  // We are Serializable, so we specify a version to allow changes to
  // method signatures without breaking serialization.  If you add or
  // remove fields, you should change this number to the current date.
  static final long serialVersionUID = 20020122L;

  protected SingleStringSequence(PptSlice ppt) {
    super(ppt);
  }

  protected /*@Prototype*/ SingleStringSequence() {
    super();
  }

  /** Returns whether or not the specified types are valid  **/
  public final boolean valid_types (VarInfo[] vis) {
    return ((vis.length == 1)
            && vis[0].file_rep_type.baseIsString()
            && vis[0].file_rep_type.isArray());
  }

  public VarInfo var() {
    return ppt.var_infos[0];
  }

  // Should never be called with modified == ValueTuple.MISSING_NONSENSICAL.
  // Subclasses need not override this except in special cases;
  // just implement @link{add_modified(Object,int)}.
  public InvariantStatus add(/*@Interned*/ Object val, int mod_index, int count) {
    assert ! falsified;
    assert (mod_index >= 0) && (mod_index < 2);
    assert Intern.isInterned(val);
    // System.out.println("SingleStringSequence.add(" + ArraysMDE.toString(value) + ", " + modified + ", " + count + ")");
    /*@Interned*/ String[] value = (/*@Interned*/ String[]) val;
    if (value == null) {
    } else if (mod_index == 0) {
      return add_unmodified(value, count);
    } else {
      return add_modified(value, count);
    }
    return InvariantStatus.NO_CHANGE;
  }

  public InvariantStatus check(/*@Interned*/ Object val, int mod_index, int count) {
    assert ! falsified;
    assert (mod_index >= 0) && (mod_index < 2);
    assert Intern.isInterned(val);
    /*@Interned*/ String[] value = (/*@Interned*/ String[]) val;
    if (value == null) {
      return InvariantStatus.NO_CHANGE;
    } else if (mod_index == 0) {
      return check_unmodified(value, count);
    } else {
      return check_modified(value, count);
    }
  }

  public abstract InvariantStatus check_modified(/*@Interned*/ String /*@Interned*/ [] value, int count);

  public InvariantStatus check_unmodified(/*@Interned*/ String /*@Interned*/ [] value, int count) {
    return InvariantStatus.NO_CHANGE;
  }

  /**
   * This method need not check for falsified;
   * that is done by the caller.
   **/
  public abstract InvariantStatus add_modified(/*@Interned*/ String /*@Interned*/ [] value, int count);

  /**
   * By default, do nothing if the value hasn't been seen yet.
   * Subclasses can override this.
   **/
  public InvariantStatus add_unmodified(/*@Interned*/ String /*@Interned*/ [] value, int count) {
    return InvariantStatus.NO_CHANGE;
  }


}
