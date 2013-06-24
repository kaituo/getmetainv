// ***** This file is automatically generated from Range.java.jpp

package daikon.inv.unary.scalar;

import daikon.*;
import daikon.inv.*;
import daikon.inv.unary.sequence.*;
import daikon.inv.binary.sequenceScalar.*;
import daikon.derive.unary.*;
import daikon.Quantify.QuantFlags;

import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import plume.*;

/**
 * Baseclass for unary range based invariants.  Each invariant is a
 * special stateless version of bound or oneof.  For example
 * EqualZero, BooleanVal, etc). These are never printed, but are used
 * internally as suppressors for ni-suppressions.
 *
 * Each specific invariant is implemented in a subclass (typically in
 * this file).
 */

public abstract class RangeInt extends SingleScalar {

  // We are Serializable, so we specify a version to allow changes to
  // method signatures without breaking serialization.  If you add or
  // remove fields, you should change this number to the current date.
  static final long serialVersionUID = 20040311L;

  protected RangeInt (PptSlice ppt) {
    super (ppt);
  }

  protected /*@Prototype*/ RangeInt () {
    super ();
  }

  /**
   * Check that instantiation is ok.  The type must be integral
   * (not boolean or hash code)
   */
  public boolean instantiate_ok (VarInfo[] vis) {

    if (!valid_types (vis))
      return (false);

    if (!vis[0].file_rep_type.baseIsIntegral())
      return (false);

    return (true);
  }

  /**
   * Returns a string in the specified format that describes the invariant.
   *
   * The generic format string is obtained from the subclass specific
   * get_format_str().  Instances of %var1% are replaced by the variable
   * name in the specified format.
   */
  public String format_using(OutputFormat format) {

    String fmt_str = get_format_str (format);

    VarInfo var1 = ppt.var_infos[0];
    String v1 = null;

    if (v1 == null)
      v1 = var1.name_using(format);

    fmt_str = UtilMDE.replaceString(fmt_str, "%var1%", v1);
    return (fmt_str);
  }

  public InvariantStatus check_modified (long x, int count) {
    if (eq_check (x))
      return (InvariantStatus.NO_CHANGE);
    else
      return (InvariantStatus.FALSIFIED);
  }

  public InvariantStatus add_modified (long x, int count) {
    return check_modified (x, count);
  }

  protected double computeConfidence() {
    return CONFIDENCE_JUSTIFIED;
  }

  public boolean isSameFormula (Invariant other) {
    assert other.getClass() == getClass();
    return (true);
  }
  public boolean isExclusiveFormula(Invariant other) {
    return false;
  }

  /**
   * All range invariants except Even and PowerOfTwo are obvious since they
   * represented by some version of OneOf or Bound.
   */
  public /*@Nullable*/ DiscardInfo isObviousDynamically (VarInfo[] vis) {

    return new DiscardInfo (this, DiscardCode.obvious,
                            "Implied by Oneof or Bound");
  }

  /**
   * Looks for a OneOf invariant over vis.  Used by Even and PowerOfTwo
   * to dynamically suppress those invariants if a OneOf exists
   */
  protected /*@Nullable*/ OneOfScalar find_oneof (VarInfo[] vis) {
    return (OneOfScalar) ppt.parent.find_inv_by_class (vis, OneOfScalar.class);
  }

  /**
   * Return a format string for the specified output format.  Each instance
   * of %varN% will be replaced by the correct name for varN.
   */
  public abstract String get_format_str (OutputFormat format);

  /**
   * Returns true if x and y don't invalidate the invariant.
   */
  public abstract boolean eq_check (long x);

  /**
   * Returns a list of prototypes of all of the range
   * invariants
   */
  public static List</*@Prototype*/ Invariant> get_proto_all () {

    List</*@Prototype*/ Invariant> result = new ArrayList</*@Prototype*/ Invariant>();
    result.add (EqualZero.get_proto());
    result.add (EqualOne.get_proto());
    result.add (EqualMinusOne.get_proto());
    result.add (GreaterEqualZero.get_proto());
    result.add (GreaterEqual64.get_proto());

      result.add (BooleanVal.get_proto());
      result.add (PowerOfTwo.get_proto());
      result.add (Even.get_proto());
      result.add (Bound0_63.get_proto());

    return (result);
  }

  /**
   * Internal invariant representing long scalars that are equal
   * to zero.  Used for non-instantiating suppressions.  Will never
   * print since OneOf accomplishes the same thing.
   */
  public static class EqualZero extends RangeInt {

    // We are Serializable, so we specify a version to allow changes to
    // method signatures without breaking serialization.  If you add or
    // remove fields, you should change this number to the current date.
    static final long serialVersionUID = 20040113L;

    protected EqualZero (PptSlice ppt) {
      super (ppt);
    }

    protected /*@Prototype*/ EqualZero () {
      super ();
    }

    private static /*@Prototype*/ EqualZero proto;

    /** returns the prototype invariant **/
    public static /*@Prototype*/ EqualZero get_proto() {
      if (proto == null)
        proto = new /*@Prototype*/ EqualZero ();
      return proto;
    }

    /** Returns whether or not this invariant is enabled **/
    public boolean enabled() {
      return OneOfScalar.dkconfig_enabled;
    }

    /** instantiates the invariant on the specified slice **/
    public EqualZero instantiate_dyn (/*>>> @Prototype EqualZero this,*/ PptSlice slice) {
      return new EqualZero (slice);
    }

    public String get_format_str (OutputFormat format) {
      if (format == OutputFormat.SIMPLIFY)
        return ("(EQ 0 %var1%)");
      else
        return ("%var1% == 0");
    }

    public boolean eq_check (long x) {
      return (x == 0);
    }
  }

  /**
   * Internal invariant representing long scalars that are equal
   * to one.  Used for non-instantiating suppressions.  Will never
   * print since OneOf accomplishes the same thing
   */
  public static class EqualOne extends RangeInt {

    // We are Serializable, so we specify a version to allow changes to
    // method signatures without breaking serialization.  If you add or
    // remove fields, you should change this number to the current date.
    static final long serialVersionUID = 20040113L;

    protected EqualOne (PptSlice ppt) {
      super (ppt);
    }

    protected /*@Prototype*/ EqualOne () {
      super ();
    }

    private static /*@Prototype*/ EqualOne proto;

    /** returns the prototype invariant **/
    public static /*@Prototype*/ EqualOne get_proto() {
      if (proto == null)
        proto = new /*@Prototype*/ EqualOne ();
      return proto;
    }

    /** Returns whether or not this invariant is enabled **/
    public boolean enabled() {
      return OneOfScalar.dkconfig_enabled;
    }

    /** instantiates the invariant on the specified slice **/
    public EqualOne instantiate_dyn (/*>>> @Prototype EqualOne this,*/ PptSlice slice) {
      return new EqualOne (slice);
    }

    public String get_format_str (OutputFormat format) {
      if (format == OutputFormat.SIMPLIFY)
        return ("(EQ 1 %var1%)");
      else
        return ("%var1% == 1");
    }

    public boolean eq_check (long x) {
      return (x == 1);
    }
  }

  /**
   * Internal invariant representing long scalars that are equal
   * to minus one.  Used for non-instantiating suppressions.  Will never
   * print since OneOf accomplishes the same thing
   */
  public static class EqualMinusOne extends RangeInt {

    // We are Serializable, so we specify a version to allow changes to
    // method signatures without breaking serialization.  If you add or
    // remove fields, you should change this number to the current date.
    static final long serialVersionUID = 20040824L;

    protected EqualMinusOne (PptSlice ppt) {
      super (ppt);
    }

    protected /*@Prototype*/ EqualMinusOne () {
      super ();
    }

    private static /*@Prototype*/ EqualMinusOne proto;

    /** returns the prototype invariant **/
    public static /*@Prototype*/ EqualMinusOne get_proto() {
      if (proto == null)
        proto = new /*@Prototype*/ EqualMinusOne ();
      return proto;
    }

    /** Returns whether or not this invariant is enabled **/
    public boolean enabled() {
      return OneOfScalar.dkconfig_enabled;
    }

    /** instantiates the invariant on the specified slice **/
    public EqualMinusOne instantiate_dyn (/*>>> @Prototype EqualMinusOne this,*/ PptSlice slice) {
      return new EqualMinusOne (slice);
    }

    public String get_format_str (OutputFormat format) {
      if (format == OutputFormat.SIMPLIFY)
        return ("(EQ -1 %var1%)");
      else
        return ("%var1% == -1");
    }

    public boolean eq_check (long x) {
      return (x == -1);
    }
  }

  /**
   * Internal invariant representing long scalars that are greater
   * than or equal to 0.  Used for non-instantiating suppressions.  Will never
   * print since Bound accomplishes the same thing
   */
  public static class GreaterEqualZero extends RangeInt {

    // We are Serializable, so we specify a version to allow changes to
    // method signatures without breaking serialization.  If you add or
    // remove fields, you should change this number to the current date.
    static final long serialVersionUID = 20040113L;

    protected GreaterEqualZero (PptSlice ppt) {
      super (ppt);
    }

    protected /*@Prototype*/ GreaterEqualZero () {
      super ();
    }

    private static /*@Prototype*/ GreaterEqualZero proto;

    /** returns the prototype invariant **/
    public static /*@Prototype*/ GreaterEqualZero get_proto() {
      if (proto == null)
        proto = new /*@Prototype*/ GreaterEqualZero ();
      return proto;
    }

    /** Returns whether or not this invariant is enabled **/
    public boolean enabled() {
      return LowerBound.dkconfig_enabled;
    }

    /** instantiates the invariant on the specified slice **/
    public GreaterEqualZero instantiate_dyn (/*>>> @Prototype GreaterEqualZero this,*/ PptSlice slice) {
      return new GreaterEqualZero (slice);
    }

    public String get_format_str (OutputFormat format) {
      if (format == OutputFormat.SIMPLIFY)
        return ("(>= %var1% 0)");
      else
        return ("%var1% >= 0");
    }

    public boolean eq_check (long x) {
      return (x >= 0);
    }
  }

  /**
   * Internal invariant representing long scalars that are greater
   * than or equal to 64.  Used for non-instantiating suppressions.  Will never
   * print since Bound accomplishes the same thing
   */
  public static class GreaterEqual64 extends RangeInt {

    // We are Serializable, so we specify a version to allow changes to
    // method signatures without breaking serialization.  If you add or
    // remove fields, you should change this number to the current date.
    static final long serialVersionUID = 20040113L;

    protected GreaterEqual64 (PptSlice ppt) {
      super (ppt);
    }

    protected /*@Prototype*/ GreaterEqual64 () {
      super ();
    }

    private static /*@Prototype*/ GreaterEqual64 proto;

    /** returns the prototype invariant **/
    public static /*@Prototype*/ GreaterEqual64 get_proto() {
      if (proto == null)
        proto = new /*@Prototype*/ GreaterEqual64 ();
      return proto;
    }

    /** Returns whether or not this invariant is enabled **/
    public boolean enabled() {
      return LowerBound.dkconfig_enabled;
    }

    /** instantiates the invariant on the specified slice **/
    public GreaterEqual64 instantiate_dyn (/*>>> @Prototype GreaterEqual64 this,*/ PptSlice slice) {
      return new GreaterEqual64 (slice);
    }

    public String get_format_str (OutputFormat format) {
      if (format == OutputFormat.SIMPLIFY)
        return ("(>= 64 %var1%)");
      else
        return ("%var1% >= 64");
    }

    public boolean eq_check (long x) {
      return (x >= 64);
    }
  }

  /**
   * Internal invariant representing longs whose values are always 0
   * or 1.  Used for non-instantiating suppressions.  Will never print
   * since OneOf accomplishes the same thing.
   */
  public static class BooleanVal extends RangeInt {

    // We are Serializable, so we specify a version to allow changes to
    // method signatures without breaking serialization.  If you add or
    // remove fields, you should change this number to the current date.
    static final long serialVersionUID = 20040113L;

    protected BooleanVal (PptSlice ppt) {
      super (ppt);
    }

    protected /*@Prototype*/ BooleanVal () {
      super ();
    }

    private static /*@Prototype*/ BooleanVal proto;

    /** returns the prototype invariant **/
    public static /*@Prototype*/ BooleanVal get_proto() {
      if (proto == null)
        proto = new /*@Prototype*/ BooleanVal ();
      return (proto);
    }

    /** Returns whether or not this invariant is enabled **/
    public boolean enabled() {
      return LowerBound.dkconfig_enabled && UpperBound.dkconfig_enabled;
    }

    /** instantiates the invariant on the specified slice **/
    public BooleanVal instantiate_dyn (/*>>> @Prototype BooleanVal this,*/ PptSlice slice) {
      return new BooleanVal (slice);
    }

    public String get_format_str (OutputFormat format) {
      if (format == OutputFormat.SIMPLIFY)
        return ("(EQ %var1% (OR 0 1))");
      else
        return ("%var1% is boolean");
    }

    public boolean eq_check (long x) {
      return ((x == 0) || (x == 1));
    }
  }

  /**
   * Invariant representing longs whose values are always a power of 2
   * (exactly one bit is set).  Used for non-instantiating
   * suppressions.  Since this is not covered by the Bound or OneOf
   * invariants it is printed.
   */
  public static class PowerOfTwo extends RangeInt {

    // We are Serializable, so we specify a version to allow changes to
    // method signatures without breaking serialization.  If you add or
    // remove fields, you should change this number to the current date.
    static final long serialVersionUID = 20040113L;

    /** Boolean.  True if PowerOfTwo invariants should be considered. **/
    public static boolean dkconfig_enabled = true;

    protected PowerOfTwo (PptSlice ppt) {
      super (ppt);
    }

    protected /*@Prototype*/ PowerOfTwo () {
      super ();
    }

    private static /*@Prototype*/ PowerOfTwo proto;

    /** returns the prototype invariant **/
    public static /*@Prototype*/ PowerOfTwo get_proto() {
      if (proto == null)
        proto = new /*@Prototype*/ PowerOfTwo ();
      return (proto);
    }

    /** returns whether or not this invariant is enabled **/
    public boolean enabled() {
      return dkconfig_enabled;
    }

    /** instantiates the invariant on the specified slice **/
    public PowerOfTwo instantiate_dyn (/*>>> @Prototype PowerOfTwo this,*/ PptSlice slice) {
      return new PowerOfTwo (slice);
    }

    public String get_format_str (OutputFormat format) {
      if (format == OutputFormat.SIMPLIFY)
        return ("(EXISTS (p) (EQ %var1% (pow 2 p)))");
      else
        return ("%var1% is a power of 2");
    }

    /**
     * Returns true if x is a power of 2 (has one bit on).  The check is
     * to and x with itself - 1.  The theory is that if there are multiple
     * bits turned on, at least one of those bits is unmodified by a subtract
     * operation and thus the & will be non-zero.  There is probably a more
     * elegant way to do this
     */
    public boolean eq_check (long x) {
      return ((x >= 1) && ((x & (x - 1)) == 0));
    }

    /**
     * Since PowerOfTwo is not covered by Bound or OneOf, it is not obvious
     * (and should thus be printed)
     */
     public /*@Nullable*/ DiscardInfo isObviousDynamically (VarInfo[] vis) {

       OneOfScalar oneof = find_oneof (vis);
       if (oneof != null)
         return new DiscardInfo (this, DiscardCode.obvious, "Implied by Oneof");

       return null;
     }

  }

  /**
   * Invariant representing longs whose values are always even.
   * Used for non-instantiating suppressions.  Since this is not
   * covered by the Bound or OneOf invariants it is printed.
   */
  public static class Even extends RangeInt {

    // We are Serializable, so we specify a version to allow changes to
    // method signatures without breaking serialization.  If you add or
    // remove fields, you should change this number to the current date.
    static final long serialVersionUID = 20040113L;

    /** Boolean.  True if Even invariants should be considered. **/
    public static boolean dkconfig_enabled = false;

    protected Even (PptSlice ppt) {
      super (ppt);
    }

    protected /*@Prototype*/ Even () {
      super ();
    }

    private static /*@Prototype*/ Even proto;

    /** returns the prototype invariant **/
    public static /*@Prototype*/ Even get_proto() {
      if (proto == null)
        proto = new /*@Prototype*/ Even ();
      return proto;
    }

    /** returns whether or not this invariant is enabled **/
    public boolean enabled() {
      return dkconfig_enabled;
    }

    /** instantiates the invariant on the specified slice **/
    public Even instantiate_dyn (/*>>> @Prototype Even this,*/ PptSlice slice) {
      return new Even (slice);
    }

    public String get_format_str (OutputFormat format) {
      if (format == OutputFormat.SIMPLIFY)
        return ("(EQ (MOD %var1% 2) 0)");
      else
        return ("%var1% is even");
    }

    public boolean eq_check (long x) {
      return ((x & 1) == 0);
    }

    /**
     * Since Even is not covered by Bound or OneOf, it is not obvious
     * (and should thus be printed)
     */
     public /*@Nullable*/ DiscardInfo isObviousDynamically (VarInfo[] vis) {
       // If there is a oneof, it implies this
       OneOfScalar oneof = find_oneof (vis);
       if (oneof != null)
         return new DiscardInfo (this, DiscardCode.obvious, "Implied by Oneof");

       return null;
     }
  }

  /**
   * Internal invariant representing longs whose values are between 0
   * and 63.  Used for non-instantiating suppressions.  Will never print
   * since Bound accomplishes the same thing.
   */
  public static class Bound0_63 extends RangeInt {

    // We are Serializable, so we specify a version to allow changes to
    // method signatures without breaking serialization.  If you add or
    // remove fields, you should change this number to the current date.
    static final long serialVersionUID = 20040113L;

    protected Bound0_63 (PptSlice ppt) {
      super (ppt);
    }

    protected /*@Prototype*/ Bound0_63 () {
      super ();
    }

    private static /*@Prototype*/ Bound0_63 proto;

    /** Returns whether or not this invariant is enabled **/
    public boolean enabled() {
      return LowerBound.dkconfig_enabled && UpperBound.dkconfig_enabled;
    }

    /** returns the prototype invariant **/
    public static /*@Prototype*/ Bound0_63 get_proto() {
      if (proto == null)
        proto = new /*@Prototype*/ Bound0_63 ();
      return (proto);
    }

    /** instantiates the invariant on the specified slice **/
    public Bound0_63 instantiate_dyn (/*>>> @Prototype Bound0_63 this,*/ PptSlice slice) {
      return new Bound0_63 (slice);
    }

    public String get_format_str (OutputFormat format) {
      if (format == OutputFormat.SIMPLIFY)
        return ("(AND (>= %var1% 0) (>= 63 %var1%))");
      else
        return ("0 <= %var1% <= 63");
    }

    public boolean eq_check (long x) {
      return ((x >= 0) && (x <= 63));
    }
  }

}
