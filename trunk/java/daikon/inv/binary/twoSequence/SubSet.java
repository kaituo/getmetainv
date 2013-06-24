// ***** This file is automatically generated from SubSet.java.jpp

package daikon.inv.binary.twoSequence;

import daikon.*;
import daikon.inv.*;
import daikon.suppress.*;

import java.util.*;
import plume.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Represents two sequences of long values where one of the sequences is a
 * subset of the other; that is each element of one sequence appears in the
 * other.
 * Prints as either <samp>x[] is a subset of y[]</samp> or as
 * <samp>x[] is a superset of y[]</samp>.
 **/
public class SubSet
  extends TwoSequence
{
  // We are Serializable, so we specify a version to allow changes to
  // method signatures without breaking serialization.  If you add or
  // remove fields, you should change this number to the current date.
  static final long serialVersionUID = 20031024L;

  private static final Logger debug =
    Logger.getLogger("daikon.inv.binary.twoSequence.SubSet");

  // Variables starting with dkconfig_ should only be set via the
  // daikon.config.Configuration interface.
  /**
   * Boolean.  True iff SubSet invariants should be considered.
   **/
  public static boolean dkconfig_enabled = false;

  protected SubSet(PptSlice ppt) {
    super(ppt);
  }

  protected /*@Prototype*/ SubSet() {
    super();
  }

  private static /*@Prototype*/ SubSet proto;

  /** Returns the prototype invariant for SubSet **/
  public static /*@Prototype*/ SubSet get_proto() {
    if (proto == null)
      proto = new /*@Prototype*/ SubSet ();
    return (proto);
  }

  /** returns whether or not this invariant is enabled **/
  public boolean enabled() {
    return dkconfig_enabled;
  }

  public boolean instantiate_ok (VarInfo[] vis) {

    if (!valid_types (vis))
      return (false);

    return (true);
  }

  /** instantiates the invariant on the specified slice **/
  public SubSet instantiate_dyn (/*>>> @Prototype SubSet this,*/ PptSlice slice) {
    return new SubSet (slice);
  }

  protected Invariant resurrect_done_swapped() {
    return new SuperSet(ppt);
  }

  public String repr() {
    return "SubSet" + varNames() + ": "
      + ",falsified=" + falsified;
  }

  public String format_using(OutputFormat format) {
    if (format == OutputFormat.DAIKON) return format();
    if (format == OutputFormat.ESCJAVA) return format_esc();
    if (format == OutputFormat.SIMPLIFY) return format_simplify();
    if (format.isJavaFamily()) {
      return format_java_family(format);
    }

    return format_unimplemented(format);
  }

  public String format() {
    String v1 = var1().name();
    String v2 = var2().name();

    return v1 + " is a subset of " + v2;
  }

  public String format_esc() {
    String classname = this.getClass().toString().substring(6); // remove leading "class"
    return "warning: method " + classname + ".format_esc() needs to be implemented: " + format();
  }

  public String format_simplify() {
    if (Invariant.dkconfig_simplify_define_predicates)
      return format_simplify_defined();
    else
      return format_simplify_explicit();
  }

  private String format_simplify_defined() {

    VarInfo subvar = var1();
    VarInfo supervar = var2();

    String[] sub_name = subvar.simplifyNameAndBounds();
    String[] super_name = supervar.simplifyNameAndBounds();

    if (sub_name == null || super_name == null) {
      return "format_simplify can't handle one of these sequences: "
        + format();
    }

    return "(subset " +
      sub_name[0] + " " + sub_name[1] + " " + sub_name[2] + " " +
      super_name[0] + " " + super_name[1] + " " + super_name[2] + ")";
  }

  private String format_simplify_explicit() {

    VarInfo subvar = var1();
    VarInfo supervar = var2();

    String[] sub_name = subvar.simplifyNameAndBounds();
    String[] super_name = supervar.simplifyNameAndBounds();

    if (sub_name == null || super_name == null) {
      return "format_simplify can't handle one of these sequences: "
        + format();
    }

    String indices[] = VarInfo.get_simplify_free_indices (subvar, supervar);

    // (FORALL (a i j b ip jp)
    //    (IFF (subset a i j b ip jp)
    //         (FORALL (x)
    //           (IMPLIES
    //              (AND (<= i x) (<= x j))
    //              (EXISTS (y)
    //                  (AND (<= ip y) (<= y jp)
    //                        (EQ (select (select elems a) x)
    //                            (select (select elems b) y))))))))

    return "(FORALL (" + indices[0] + ") (IMPLIES (AND (<= " + sub_name[1]
      + " " + indices[0] + ") (<= " + indices[0] + " " + sub_name[2] + "))"
      + "(EXISTS (" + indices[1] + ")(AND (<= " + super_name[1] + " "
      + indices[1] + ") (<= " + indices[1] + " " + super_name[2]+ ")"
      + "(EQ (select (select elems " + sub_name[0] + ") " + indices[0] + ") "
      + "(select (select elems " + super_name[0] +") " + indices[1] + "))))))";

  }

  public String format_java_family(OutputFormat format) {

    String v1 = var1().name_using(format);
    String v2 = var2().name_using(format);

    return "daikon.Quant.subsetOf(" + v1 + ", " + v2 + ")";
  }

  public InvariantStatus check_modified(long[] a1, long[] a2, int count) {

    if (!ArraysMDE.isSubset(a1, a2))
    {
      return InvariantStatus.FALSIFIED;
    }
    return InvariantStatus.NO_CHANGE;
  }

  public InvariantStatus add_modified(long[] a1, long[] a2, int count) {
    if (debug.isLoggable(Level.FINE)) {
      debug.fine (ArraysMDE.toString(a1));
      debug.fine (ArraysMDE.toString(a2));
    }
    return check_modified(a1, a2, count);
  }

  protected double computeConfidence() {
    return Invariant.CONFIDENCE_JUSTIFIED;
  }

  // Convenience name to make this easier to find.
  public static /*@Nullable*/ DiscardInfo isObviousSubSet(Invariant inv, VarInfo subvar, VarInfo supervar) {
    return SubSequence.isObviousSubSequence(inv, subvar, supervar);
  }

  public /*@Nullable*/ DiscardInfo isObviousStatically(VarInfo[] vis) {
    VarInfo var1 = vis[0];
    VarInfo var2 = vis[1];

    DiscardInfo di;
    di = SubSet.isObviousSubSet(this, var1, var2);
    if (di == null) {
      di = SubSet.isObviousSubSet(this, var2, var1);
    }
    if (di != null) {
      return di;
    }

    VarInfo subvar, supervar;

    subvar = var1;
    supervar = var2;

    // Uninteresting if this is of the form x[0..i] subsequence
    // x[0..j].  Not necessarily obvious.
    VarInfo subvar_super = subvar.isDerivedSubSequenceOf();
    if (subvar_super == supervar) {
      debug.fine ("  returning true because subvar_super == supervar");
      return new DiscardInfo(this, DiscardCode.obvious, "x[0..i] subsequence of x[0..j] is uninteresting");
    }

    VarInfo supervar_super = supervar.isDerivedSubSequenceOf();
    if (subvar_super != null && subvar_super == supervar_super) {
      debug.fine ("  returning true because subvar_super == supervar_super");
      return new DiscardInfo(this, DiscardCode.obvious, "x[0..i] subsequence of x[0..j] is uninteresting");
    }

    di = SubSequence.isObviousSubSequence(this, var1, var2);
    if (di == null) {
      di = SubSequence.isObviousSubSequence(this, var2, var1);
    }
    if (di != null) {
      return di;
    }
    return super.isObviousStatically(vis);
  }

  // Look up a previously instantiated SubSet relationship.
  public static /*@Nullable*/ SubSet find(PptSlice ppt) {
    assert ppt.arity() == 2;
    for (Invariant inv : ppt.invs) {
      if (inv instanceof SubSet)
        return (SubSet) inv;
    }
    return null;
  }

  public boolean isSameFormula(Invariant other) {
    assert other instanceof SubSet;
    return true;
  }

  /** NI suppressions, initialized in get_ni_suppressions() **/
  private static /*@Nullable*/ NISuppressionSet suppressions = null;

  /** returns the ni-suppressions for SubSet **/
  public /*@NonNull*/ NISuppressionSet get_ni_suppressions() {
    if (suppressions == null) {
      NISuppressee suppressee = new NISuppressee (SubSet.class, 2);

      // suppressor definitions (used in suppressions below)
      NISuppressor v1_eq_v2 = new NISuppressor (0, 1, SeqSeqIntEqual.class);
      NISuppressor v1_pw_eq_v2 = new NISuppressor (0, 1, PairwiseIntEqual.class);

      // sub/super set suppressions.  We have both SeqSeq and Pairwise
      // as suppressions because each can be enabled separately.
      suppressions = new NISuppressionSet (new NISuppression[] {
          // a[] == b[] ==> a[] sub/superset b[]
          new NISuppression (v1_eq_v2, suppressee),
          // a[] == b[] ==> a[] sub/superset b[]
          new NISuppression (v1_pw_eq_v2, suppressee),
        });
    }

    return (suppressions);
  }

}
