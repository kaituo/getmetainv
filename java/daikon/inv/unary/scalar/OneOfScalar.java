// ***** This file is automatically generated from OneOf.java.jpp

package daikon.inv.unary.scalar;

import daikon.*;
import daikon.inv.*;
import daikon.inv.unary.OneOf;

  import daikon.derive.unary.*;
  import daikon.inv.unary.sequence.*;
  import daikon.inv.binary.sequenceScalar.*;

import plume.*;

import java.io.*;
import java.util.logging.Logger;
import java.util.logging.Level;

import java.util.*;

// This subsumes an "exact" invariant that says the value is always exactly
// a specific value.  Do I want to make that a separate invariant
// nonetheless?  Probably not, as this will simplify implication and such.

  /**
   * Represents long scalars that take on only a few distinct values.  Prints
   * as either <samp>x == c</samp> (when there is only one value),
   * <samp>x one of {c1, c2, c3}</samp> (when there are multiple values),
   * or <samp>x has only one value</samp> (when <samp>x</samp> is
   * a hashcode (pointer) - this is because the numerical value of the
   * hashcode (pointer) is uninteresting).
   */

public final class OneOfScalar
  extends SingleScalar
  implements OneOf
{
  // We are Serializable, so we specify a version to allow changes to
  // method signatures without breaking serialization.  If you add or
  // remove fields, you should change this number to the current date.
  static final long serialVersionUID = 20030822L;

  /**
   * Debugging logger.
   **/
  public static final Logger debug
    = Logger.getLogger (OneOfScalar.class.getName());

  // Variables starting with dkconfig_ should only be set via the
  // daikon.config.Configuration interface.
  /**
   * Boolean.  True iff OneOf invariants should be considered.
   **/
  public static boolean dkconfig_enabled = true;

  /**
   * Positive integer.  Specifies the maximum set size for this type
   * of invariant (x is one of <code>size</code> items).
   **/

  public static int dkconfig_size = 3;

  /**
   * Boolean.  If true, invariants describing hashcode-typed variables
   * as having any particular value will have an artificial value
   * substituted for the exact hashhode values.  The artificial values
   * will stay the same from run to run even if the actual hashcode
   * values change (as long as the OneOf invariants remain the same).
   * If false, hashcodes will be formatted as the application of a
   * hashcode uninterpreted function to an integer representing the
   * bit pattern of the hashcode.  One might wish to omit the exact
   * values of the hashcodes because they are usually uninteresting;
   * this is the same reason they print in the native Daikon format,
   * for instance, as <samp>var has only one value</samp> rather than
   * <samp>var == 150924732</samp>.
   **/
  public static boolean dkconfig_omit_hashcode_values_Simplify = false;

  // Probably needs to keep its own list of the values, and number of each seen.
  // (That depends on the slice; maybe not until the slice is cleared out.
  // But so few values is cheap, so this is quite fine for now and long-term.)

  /*@Unused(when=Prototype.class)*/
  private long[] elts;
  /*@Unused(when=Prototype.class)*/
  private int num_elts;

  public /*@Prototype*/ OneOfScalar () {
    super();
  }

  public OneOfScalar (PptSlice slice) {
    super (slice);

    elts = new long[dkconfig_size];

    num_elts = 0;

  }

  private static /*@Prototype*/ OneOfScalar proto;

  /** Returns the prototype invariant for OneOfScalar **/
  public static /*@Prototype*/ OneOfScalar get_proto() {
    if (proto == null)
      proto = new /*@Prototype*/ OneOfScalar ();
    return (proto);
  }

  /** returns whether or not this invariant is enabled **/
  public boolean enabled() {
    return dkconfig_enabled;
  }

  /** instantiate an invariant on the specified slice **/
  public OneOfScalar instantiate_dyn (/*>>> @Prototype OneOfScalar this,*/ PptSlice slice) {
    return new OneOfScalar(slice);
  }

  public boolean is_boolean() {
     return (var().file_rep_type == ProglangType.BOOLEAN);
  }
  public boolean is_hashcode() {
    return (var().file_rep_type == ProglangType.HASHCODE);
  }

  @SuppressWarnings("interning") // clone method re-does interning
  public OneOfScalar clone() {
    OneOfScalar result = (OneOfScalar) super.clone();
    result.elts = elts.clone();

    result.num_elts = this.num_elts;
    return result;
  }

  public int num_elts() {
    return num_elts;
  }

  public Object elt() {
    return elt(0);
  }

  public Object elt(int index) {
    if (num_elts <= index)
      throw new Error("Represents " + num_elts + " elements, index " + index + " not valid");

    // Not sure whether interning is necessary (or just returning an Integer
    // would be sufficient), but just in case...
    return Intern.internedLong(elts[index]);
  }

  @SuppressWarnings("interning") // generics bug in (at least interning) checker

  private void sort_rep() {
    Arrays.sort(elts, 0, num_elts );
  }

  public long min_elt() {
    if (num_elts == 0)
      throw new Error("Represents no elements");
    sort_rep();
    return elts[0];
  }

  public long max_elt() {
    if (num_elts == 0)
      throw new Error("Represents no elements");
    sort_rep();
    return elts[num_elts-1];
  }

  // Assumes the other array is already sorted
  public boolean compare_rep(int num_other_elts, long[] other_elts) {
    if (num_elts != num_other_elts)
      return false;
    sort_rep();
    for (int i=0; i < num_elts; i++)
      if (! ((elts[i]) == (other_elts[i]))) // elements are interned
        return false;
    return true;
  }

  private String subarray_rep() {
    // Not so efficient an implementation, but simple;
    // and how often will we need to print this anyway?
    sort_rep();
    StringBuffer sb = new StringBuffer();
    sb.append("{ ");
    for (int i=0; i<num_elts; i++) {
      if (i != 0)
        sb.append(", ");

      if (PrintInvariants.dkconfig_static_const_infer) {
        boolean curVarMatch = false;
        PptTopLevel pptt = ppt.parent;
        for (VarInfo vi : pptt.var_infos) {
          if (vi.isStaticConstant() && VarComparability.comparable(vi, var())) {
            Object constantVal = vi.constantValue();
            if (constantVal.equals(elts[i])) {
              sb.append(vi.name());
              curVarMatch = true;
            }
          }
        }

        if (curVarMatch == false) {
          sb.append(((Integer.MIN_VALUE <= elts[i] && elts[i] <= Integer.MAX_VALUE) ? String.valueOf(elts[i]) : (String.valueOf(elts[i]) + "L")));
        }
      }
      else {
        sb.append(((Integer.MIN_VALUE <= elts[i] && elts[i] <= Integer.MAX_VALUE) ? String.valueOf(elts[i]) : (String.valueOf(elts[i]) + "L")));
      }

    }
    sb.append(" }");
    return sb.toString();
  }

  public String repr() {
    return "OneOfScalar" + varNames() + ": "
      + "falsified=" + falsified
      + ", num_elts=" + num_elts
      + ", elts=" + subarray_rep();
  }

  public long[] getElts() {
    long[] temp = new long[elts.length];
    for (int i=0; i < elts.length; i++) {
      temp[i] = elts[i];
    }
    return temp;
  }

  public String format_using(OutputFormat format) {
    sort_rep();

    if (format.isJavaFamily()) return format_java_family(format);

    if (format == OutputFormat.DAIKON) {
      return format_daikon();
    } else if (format == OutputFormat.SIMPLIFY) {
      return format_simplify();
    } else if (format == OutputFormat.ESCJAVA) {
      String result = format_esc();
      return result;
    } else {
      return format_unimplemented(format);
    }
  }

  public String format_daikon() {
    String varname = var().name();
    if (num_elts == 1) {

        if (is_boolean()) {
          if ((elts[0] != 0) && (elts[0] != 1))
              System.out.println ("WARNING:: Variable "
              + varname + " is of type boolean, but has non boolean value: "
              + elts[0]);
          return varname + " == " + ((elts[0] == 0) ? "false" : "true");
        } else if (is_hashcode()) {
          if (elts[0] == 0) {
            return varname + " == null";
          } else {
            return varname + " has only one value"
              // + " (hashcode=" + elts[0] + ")"
              ;
          }
        } else {
          if (PrintInvariants.dkconfig_static_const_infer) {
            PptTopLevel pptt = ppt.parent;
            for (VarInfo vi : pptt.var_infos) {
              if (vi.isStaticConstant() && VarComparability.comparable(vi, var())) {
                Object constantVal = vi.constantValue();
                if (constantVal.equals(elts[0])) {
                  return varname + " == " + vi.name();
                }
              }
            }
          }
          return varname + " == " + ((Integer.MIN_VALUE <= elts[0] && elts[0] <= Integer.MAX_VALUE) ? String.valueOf(elts[0]) : (String.valueOf(elts[0]) + "L"));
        }

    } else {
      return varname + " one of " + subarray_rep();
    }
  }

  public String format_esc() {
    sort_rep();

    String varname = var().esc_name();

    String result;

    if (is_boolean()) {
      assert num_elts == 1;
      assert (elts[0] == 0) || (elts[0] == 1);
      result = varname + " == " + ((elts[0] == 0) ? "false" : "true");
    } else if (is_hashcode()) {
      if (num_elts == 1) {
        if (elts[0] == 0) {
          result = varname + " == null";
        } else {
          // This seems wrong, because there is already a "!= null" invariant.
          // This invariant (OneOfScalar) just shouldn't print for ESC format.
          result = varname + " != null";
          // varname + " has only one value"
          // + " (hashcode=" + elts[0] + ")"
        }
      } else if (num_elts == 2) {
        // add_modified allows two elements iff one is null
        assert elts[0] == 0;
        assert elts[1] != 0;
        return format_unimplemented(OutputFormat.ESCJAVA); // "needs to be implemented"
      } else if (num_elts == 0) {
        // Do nothing
        return format_unimplemented(OutputFormat.ESCJAVA); // "needs to be implemented"
      } else {
        throw new Error ("Contains more than 2 elements");
      }
    } else {
      result = "";
      for (int i=0; i<num_elts; i++) {
        if (i != 0) { result += " || "; }
        result += varname + " == " + ((Integer.MIN_VALUE <= elts[i] && elts[i] <= Integer.MAX_VALUE) ? String.valueOf(elts[i]) : (String.valueOf(elts[i]) + "L"));
      }
    }

    return result;
  }

  public String format_java_family(OutputFormat format) {

    String result;

    // Setting up the name of the unary variable
    String varname = var().name_using(format);

    if (is_boolean()) {
      assert num_elts == 1;
      assert (elts[0] == 0) || (elts[0] == 1);
      result = varname + " == " + ((elts[0] == 0) ? "false" : "true");
    } else if (is_hashcode()) {
      if (num_elts == 2) {
        return "true";          // one elt is null, the other is non-null
      } else if (elts[0] == 0) {
        result = varname + " == null";
      } else {
        result = varname + " != null";
          // varname + " has only one value"
          // + " (hashcode=" + elts[0] + ")"
      }
    } else {
      result = "";
      for (int i=0; i<num_elts; i++) {
        if (i != 0) { result += " || "; }

        result += varname + " == " + ((Integer.MIN_VALUE <= elts[i] && elts[i] <= Integer.MAX_VALUE) ? String.valueOf(elts[i]) : (String.valueOf(elts[i]) + "L"));

      }
    }

    return result;
  }

  public String format_simplify() {

    // if (is_hashcode() && dkconfig_omit_hashcode_values_Simplify)
    //   return "(AND)";

    sort_rep();

    String varname =
      var().simplifyFixup(var().name_using(OutputFormat.SIMPLIFY));

    String result;

    if (is_boolean()) {
      assert num_elts == 1;
      assert (elts[0] == 0) || (elts[0] == 1);
      result = "(EQ " + varname + " " + ((elts[0] == 0) ? "|@false|" : "|@true|") + ")";
    } else if (is_hashcode()) {
      if (num_elts == 1) {
        long hashcode_val = get_hashcode_val (elts[0]);
        result = "(EQ " + varname + " " +
          ((elts[0] == 0) ? "null" :
           ("(hashcode " + simplify_format_long(hashcode_val) + ")")) + ")";
      } else if (num_elts == 2) {
        // add_modified allows two elements iff one is null
        assert elts[0] == 0;
        assert elts[1] != 0;
        long hashcode_val = get_hashcode_val (elts[1]);
        result = "(OR (EQ " + varname + " null) (EQ " + varname
          + "(hashcode " + simplify_format_long(hashcode_val) + ")))";
      } else if (num_elts == 0) {
        return format_too_few_samples(OutputFormat.SIMPLIFY, null);
      } else {
        throw new Error ("Contains more than 2 elements");
      }
    } else {
      result = "";
      for (int i=0; i<num_elts; i++) {
        result += " (EQ " + varname + " " + simplify_format_long(elts[i]) + ")";
      }
      if (num_elts > 1) {
        result = "(OR" + result + ")";
      } else if (num_elts == 1) {
        // chop leading space
        result = result.substring(1);
      } else {
        // Haven't actually seen any data, so we're vacuously true
        return format_too_few_samples(OutputFormat.SIMPLIFY, null);
      }
    }

    if (result.indexOf("format_simplify") == -1)
      daikon.simplify.SimpUtil.assert_well_formed(result);
    return result;
  }

  public InvariantStatus add_modified(long a, int count) {
    return runValue(a, count, true);
  }

  public InvariantStatus check_modified(long a, int count) {
    return runValue(a, count, false);
  }

  private InvariantStatus runValue(long v, int count, boolean mutate) {
    InvariantStatus status;
    if (mutate) {
      status = add_mod_elem(v, count);
    } else {
      status = check_mod_elem(v, count);
    }
    if (status == InvariantStatus.FALSIFIED) {
      if (logOn() && mutate) {
        StringBuffer eltString = new StringBuffer();
        for (int i = 0; i < num_elts; i++) {
          eltString.append(((Integer.MIN_VALUE <= elts[i] && elts[i] <= Integer.MAX_VALUE) ? String.valueOf(elts[i]) : (String.valueOf(elts[i]) + "L")) + " ");
        }
        log ("destroyed on sample " + ((Integer.MIN_VALUE <= v && v <= Integer.MAX_VALUE) ? String.valueOf(v) : (String.valueOf(v) + "L")) + " previous vals = { "
             + eltString + "} num_elts = " + num_elts);
      }
      return InvariantStatus.FALSIFIED;
    }
    return status;
  }

  /**
   * Adds a single sample to the invariant.  Returns
   * the appropriate InvariantStatus from the result
   * of adding the sample to this.
   */
  public InvariantStatus add_mod_elem (long v, int count) {
    InvariantStatus status = check_mod_elem(v, count);
    if (status == InvariantStatus.WEAKENED) {
      elts[num_elts] = v;
      num_elts++;
    }
    return status;
  }

  /**
   * Checks a single sample to the invariant.  Returns
   * the appropriate InvariantStatus from the result
   * of adding the sample to this.
   */
  public InvariantStatus check_mod_elem (long v, int count) {

    // Look for v in our list of previously seen values.  If it's
    // found, we're all set.
    for (int i=0; i<num_elts; i++) {
      //if (logDetail())
      //  log ("add_modified (" + v + ")");
      if (((elts[i]) == ( v))) {
        return (InvariantStatus.NO_CHANGE);
      }
    }

    if (num_elts == dkconfig_size) {
      return (InvariantStatus.FALSIFIED);
    }

    if ((is_boolean() && (num_elts == 1)) || (is_hashcode() && (num_elts == 2))) {
      return (InvariantStatus.FALSIFIED);
    }

    if (is_hashcode() && (num_elts == 1)) {
      // Permit two object values only if one of them is null
      if ((elts[0] != 0) && (v != 0)) {
        return (InvariantStatus.FALSIFIED);
      }
    }

    return (InvariantStatus.WEAKENED);
  }

  protected double computeConfidence() {
    // This is not ideal.
    if (num_elts == 0) {
      return Invariant.CONFIDENCE_UNJUSTIFIED;
    } else if (is_hashcode() && (num_elts > 1)) {
      // This should never happen
      return Invariant.CONFIDENCE_UNJUSTIFIED;
    } else {
      return Invariant.CONFIDENCE_JUSTIFIED;
    }
  }

  public /*@Nullable*/ DiscardInfo isObviousStatically(VarInfo[] vis) {
    // Static constants are necessarily OneOf precisely one value.
    // This removes static constants from the output, which might not be
    // desirable if the user doesn't know their actual value.
    if (vis[0].isStaticConstant()) {
      assert num_elts <= 1;
      return new DiscardInfo(this, DiscardCode.obvious, vis[0].name() + " is a static constant.");
    }
    return super.isObviousStatically(vis);
  }

  public /*@Nullable*/ DiscardInfo isObviousDynamically(VarInfo[] vis) {
    DiscardInfo super_result = super.isObviousDynamically(vis);
    if (super_result != null) {
      return super_result;
    }

    VarInfo v = vis[0];

    Debug dlog = new Debug (getClass(), ppt, vis);

    if (logOn())
      dlog.log ("enter isObviousDynamically");

    // Obvious if we are 'size(array) == 0'  The thinking is that this
    // will also always show up as 'array == []' which is a little more
    // illuminating.  At this point we insist that we are a SequenceLength
    // derivation of a base (underived) array.  Its quite possible this should
    // just apply to all arrays.
    if (v.isDerived() && (v.derived instanceof SequenceLength)
      && (num_elts == 1) && (elts[0] == 0)) {
      @SuppressWarnings("nullness") // checker bug: flow
      /*@NonNull*/ VarInfo[] bv = v.derived.getBases();
      if (!bv[0].isDerived()) {
        if (logOn())
          dlog.log ("isObviousDynamically '" + v.name()
                    + " == 0' ==> '" + bv[0].name() + " == []'");
        return new DiscardInfo(this, DiscardCode.obvious, "size(array) == 0 is implied by array == []");
      }
    }

    if (v.isDerived() && (v.derived instanceof SequenceLength)) {
      @SuppressWarnings("nullness") // checker bug: flow
      /*@NonNull*/ SequenceLength sl = (SequenceLength) v.derived;
      if (sl.shift != 0) {
        String discardString = v.name() + " is derived with shift!=0 (shift==" + sl.shift + ")";
        return new DiscardInfo(this, DiscardCode.obvious, discardString);
      }
    }

    // For every EltOneOf at this program point, see if this variable is
    // an obvious member of that sequence.
    PptTopLevel parent = ppt.parent;
    for (Iterator<Invariant> itor = parent.invariants_iterator(); itor.hasNext(); ) {
      Invariant inv = itor.next();
      if ((inv instanceof EltOneOf) && inv.enoughSamples()) {
        VarInfo v1 = var();
        VarInfo v2 = inv.ppt.var_infos[0];
        // System.out.println("isObviousImplied: calling  Member.isObviousMember(" + v1.name + ", " + v2.name + ")");
        // Don't use isEqualToObviousMember:  that is too subtle
        // and eliminates desirable invariants such as "return == null".
        if (Member.isObviousMember(v1, v2)) {
          EltOneOf other = (EltOneOf) inv;
          if (num_elts == other.num_elts()) {
            sort_rep();
            if (other.compare_rep(num_elts, elts)) {
              // System.out.println("isObviousImplied true");
              String discardString = v1.name()+" is a member of "+v2.name()+" for which this Invariant also holds";
              return new DiscardInfo(this, DiscardCode.obvious, discardString);
            }
          }
        }
      }
    }

    return null;
  }

  /**
   * Oneof can merge different formulas from lower points to create a single
   * formula at an upper point.
   */
  public boolean mergeFormulasOk() {
    return (true);
  }

  public boolean isSameFormula(Invariant o) {
    OneOfScalar other = (OneOfScalar) o;
    if (num_elts != other.num_elts)
      return false;
    if (num_elts == 0 && other.num_elts == 0)
      return true;

    sort_rep();
    other.sort_rep();

    // All nonzero hashcode values should be considered equal to each other
    //
    // Examples:
    // inv1  inv2  result
    // ----  ----  ------
    // 19    0     false
    // 19    22    true
    // 0     0     true

    if (is_hashcode() && other.is_hashcode()) {
      if (num_elts == 1 && other.num_elts == 1) {
        return ((elts[0] == 0 && other.elts[0] == 0) ||
                (elts[0] != 0 && other.elts[0] != 0));
      } else if (num_elts == 2 && other.num_elts == 2) {
        // add_modified allows two elements iff one is null
        assert elts[0] == 0;
        assert other.elts[0] == 0;
        assert elts[1] != 0;
        assert other.elts[1] != 0;

        // Since we know the first elements of each invariant are
        // zero, and the second elements are nonzero, we can immediately
        // return true
        return true;
      } else {
        return false;
      }
    }

    for (int i=0; i < num_elts; i++) {
      if (! ((elts[i]) == (other.elts[i])))
        return false;
    }

    return true;
  }

  public boolean isExclusiveFormula(Invariant o) {
    if (o instanceof OneOfScalar) {
      OneOfScalar other = (OneOfScalar) o;

      if (num_elts == 0 || other.num_elts == 0)
        return false;
      for (int i=0; i < num_elts; i++) {
        for (int j=0; j < other.num_elts; j++) {
          if (((elts[i]) == (other.elts[j]))) // elements are interned
            return false;
        }
      }

      // Don't consider two instances of "non-null" as exclusive.
      if (is_hashcode() && num_elts == 1
          && other.is_hashcode() && other.num_elts == 1
          && elts[0] != 0 && other.elts[0] != 0) {
        return false;
      }

      // Be even more aggressive about rejecting these for use in
      // implications in this case, since, we'd be printing them as
      // "true"
      /*
      if (dkconfig_omit_hashcode_values_Simplify &&
          (is_hashcode() || other.is_hashcode())) {
        return false;
      }
      */

      return true;
    }

    // Many more checks can be added here:  against nonzero, modulus, etc.
    if ((o instanceof NonZero) && (num_elts == 1) && (elts[0] == 0)) {
      return true;
    }
    long elts_min = Long.MAX_VALUE;
    long elts_max = Long.MIN_VALUE;
    for (int i=0; i < num_elts; i++) {
      elts_min = Math.min(elts_min, elts[i]);
      elts_max = Math.max(elts_max, elts[i]);
    }
    if ((o instanceof LowerBound) && (elts_max < ((LowerBound)o).min()))
      return true;
    if ((o instanceof UpperBound) && (elts_min > ((UpperBound)o).max()))
      return true;

    return false;
  }

  // OneOf invariants that indicate a small set of possible values are
  // uninteresting.  OneOf invariants that indicate exactly one value
  // are interesting.
  public boolean isInteresting() {
    if (num_elts() > 1) {
      return false;
    } else {
      return true;
    }
  }

  public boolean hasUninterestingConstant() {

    for (int i = 0; i < num_elts; i++) {
      if (elts[i] < -1 || elts[i] > 2)
        return true;
    }

    return false;
  }

  public boolean isExact() {
    return (num_elts == 1);
  }

  // Look up a previously instantiated invariant.
  public static /*@Nullable*/ OneOfScalar find(PptSlice ppt) {
    assert ppt.arity() == 1;
    for (Invariant inv : ppt.invs) {
      if (inv instanceof OneOfScalar)
        return (OneOfScalar) inv;
    }
    return null;
  }

  // Interning is lost when an object is serialized and deserialized.
  // Manually re-intern any interned fields upon deserialization.
  @SuppressWarnings("interning") // readObject re-interns
  private void readObject(ObjectInputStream in) throws IOException,
    ClassNotFoundException {
    in.defaultReadObject();

    for (int i=0; i < num_elts; i++)
      elts[i] = Intern.intern(elts[i]);
  }

  /**
   * Merge the invariants in invs to form a new invariant.  Each must be
   * a OneOfScalar invariant.  This code finds all of the oneof values
   * from each of the invariants and returns the merged invariant (if any).
   *
   * @param invs       List of invariants to merge.  The invariants must all be
   *                   of the same type and should come from the children of
   *                   parent_ppt.  They should also all be permuted to match
   *                   the variable order in parent_ppt.
   * @param parent_ppt Slice that will contain the new invariant
   */
  @SuppressWarnings("interning") // cloning requires re-interning
  public /*@Nullable*/ Invariant merge (List<Invariant> invs, PptSlice parent_ppt) {

    // Create the initial parent invariant from the first child
    OneOfScalar  first = (OneOfScalar) invs.get(0);
    OneOfScalar result = first.clone();
    result.ppt = parent_ppt;

    // Loop through the rest of the child invariants
    for (int i = 1; i < invs.size(); i++ ) {

      // Get this invariant
      OneOfScalar inv = (OneOfScalar) invs.get (i);

      // Loop through each distinct value found in this child and add
      // it to the parent.  If the invariant is falsified, there is no parent
      // invariant
      for (int j = 0; j < inv.num_elts; j++) {
        long val = inv.elts[j];

        InvariantStatus status = result.add_mod_elem(val, 1);
        if (status == InvariantStatus.FALSIFIED) {
          result.log ("child value '" + val + "' destroyed oneof");
          return (null);
        }
      }
    }

    result.log ("Merged '" + result.format() + "' from " + invs.size()
                + " child invariants");
    return (result);
  }

  /**
   * Setup the invariant with the specified elements.  Normally
   * used when searching for a specified OneOf.  The elements of vals
   * are not necessarily interned; this method interns each element.
   */
  public void set_one_of_val (long[] vals) {

    num_elts = vals.length;
    for (int i = 0; i < num_elts; i++)
      elts[i] = Intern.intern (vals[i]);
  }

  /**
   * Returns true if every element in this invariant is contained in
   * the specified state.  For example if x = 1 and the state contains
   * 1 and 2, true will be returned.
   */
  public boolean state_match (Object state) {

    if (num_elts == 0)
      return (false);

    if (!(state instanceof long[]))
      System.out.println ("state is of class '" + state.getClass().getName()
                          + "'");
    long[] e = (long[]) state;
    for (int i = 0; i < num_elts; i++) {
      boolean match = false;
      for (int j = 0; j < e.length; j++) {
        if (elts[i] == e[j]) {
          match = true;
          break;
        }
      }
      if (!match)
        return (false);
    }
    return (true);
  }

  /**
   * Map that holds dummy hashcode values for hashcodes.  Each hashcode
   * seen is assigned a small integer in the order they are seen.  These
   * values will be consistent as long as new hashcodes do not appear
   * in the output.  Not a perfect fix for regressions consistency, but
   * workable
   */
  private static Map<Long,Long> dummy_hashcode_vals
    = new LinkedHashMap<Long,Long>();
  private static long next_dummy_hashcode = 1001;

  private long get_hashcode_val (long hashcode) {
    if (!dkconfig_omit_hashcode_values_Simplify)
      return hashcode;

    Long val = dummy_hashcode_vals.get (hashcode);
    if (val != null)
      return val;
    dummy_hashcode_vals.put (hashcode, next_dummy_hashcode);
    return (next_dummy_hashcode++);
  }

}