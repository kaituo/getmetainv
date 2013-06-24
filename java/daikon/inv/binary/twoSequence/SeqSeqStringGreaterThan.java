  // "define" PAIRWISECOMP_EQ PairwiseStringEqual
  // "define" PAIRWISECOMP_LT PairwiseStringLessThan
  // "define" PAIRWISECOMP_GT PairwiseStringGreaterThan
  // "define" PAIRWISECOMP_LE PairwiseStringLessEqual
  // "define" PAIRWISECOMP_GE PairwiseStringGreaterEqual

  // "define" PAIRWISEINTEQUAL PairwiseStringGreaterThan

// ***** This file is automatically generated from SeqComparison.java.jpp

package daikon.inv.binary.twoSequence;

import daikon.*;
import daikon.inv.*;
import daikon.suppress.*;
import daikon.derive.binary.*;
import daikon.Quantify.QuantFlags;

import plume.*;
import java.util.logging.Logger;
import java.util.*;

/**
 * Represents invariants between two sequences of String values.  If order
 * matters for each variable (which it does by default), then the
 * sequences are compared lexically.
 * Prints as <samp>x[] > y[] lexically</samp>.
 *

 * If the auxiliary information (e.g., order matters)
 * doesn't match between two variables, then this invariant cannot
 * apply to those variables.
 **/
public class SeqSeqStringGreaterThan
  extends TwoSequenceString
  implements Comparison
{
  // We are Serializable, so we specify a version to allow changes to
  // method signatures without breaking serialization.  If you add or
  // remove fields, you should change this number to the current date.
  static final long serialVersionUID = 20030822L;

  // Variables starting with dkconfig_ should only be set via the
  // daikon.config.Configuration interface.
  /**
   * Boolean.  True iff SeqSeqStringGreaterThan invariants should be considered.
   **/
  public static boolean dkconfig_enabled = true;

  /**
   * Debugging logger.
   **/
  static final Logger debug = Logger.getLogger ("daikon.inv.binary.twoSequence.SeqSeqStringGreaterThan");

  @SuppressWarnings("interning")  // bug with generics
  static Comparator<String[]> comparator = new ArraysMDE.StringArrayComparatorLexical();

  boolean orderMatters;

  protected SeqSeqStringGreaterThan(PptSlice ppt, boolean order) {
    super(ppt);
    orderMatters = order;
  }

  protected /*@Prototype*/ SeqSeqStringGreaterThan(boolean order) {
    super();
    orderMatters = order;
  }

  protected SeqSeqStringGreaterThan(SeqSeqStringLessThan seq_swap) {
    super(seq_swap.ppt);
    orderMatters = seq_swap.orderMatters;
  }

  private static /*@Prototype*/ SeqSeqStringGreaterThan proto;

  /** Returns the prototype invariant for SeqSeqStringGreaterThan **/
  public static /*@Prototype*/ SeqSeqStringGreaterThan get_proto() {
    if (proto == null)
      proto = new /*@Prototype*/ SeqSeqStringGreaterThan (true);
    return (proto);
  }

  /** Returns whether or not this invariant is enabled **/
  public boolean enabled() {
    return dkconfig_enabled;
  }

  /** Non-Equal SeqComparison is only valid on integral types **/
  public boolean instantiate_ok (VarInfo[] vis) {

    if (!valid_types (vis))
      return (false);

    VarInfo var1 = vis[0];
    VarInfo var2 = vis[1];
    ProglangType type1 = var1.type;
    ProglangType type2 = var2.type;

      // This intentonally checks dimensions(), not pseudoDimensions.
      boolean only_eq = (! ((type1.dimensions() == 1)
                            && type1.baseIsString()
                            && (type2.dimensions() == 1)
                            && type2.baseIsString()));
      if (only_eq)
        return (false);

      // non equality comparisons don't make sense if the arrays aren't ordered
      if (!var1.aux.getFlag(VarInfoAux.HAS_ORDER)
        || !var2.aux.getFlag(VarInfoAux.HAS_ORDER))
        return (false);

    return (true);
  }

  /** Instantiates the invariant on the specified slice **/
  protected SeqSeqStringGreaterThan instantiate_dyn (/*>>> @Prototype SeqSeqStringGreaterThan this,*/ PptSlice slice) {
    boolean has_order = slice.var_infos[0].aux.getFlag(VarInfoAux.HAS_ORDER)
                      && slice.var_infos[1].aux.getFlag(VarInfoAux.HAS_ORDER);
    return new SeqSeqStringGreaterThan(slice, has_order);
  }

  protected Invariant resurrect_done_swapped() {

    return new SeqSeqStringLessThan(this);
  }

  public String repr() {
    return "SeqSeqStringGreaterThan" + varNames() + ": "
      + ",orderMatters=" + orderMatters
      + ",enoughSamples=" + enoughSamples()
      ;
  }

  public String format_using(OutputFormat format) {
    // System.out.println("Calling SeqSeqStringGreaterThan.format for: " + repr());

    if (format == OutputFormat.SIMPLIFY) {
      return format_simplify();
    }

    if (format == OutputFormat.DAIKON)
    {
      String name1 = var1().name_using(format);
      String name2 = var2().name_using(format);

      String lexically = (var1().aux.getFlag(VarInfoAux.HAS_ORDER)
                          ? " (lexically)"
                          : "");
      return name1 + " > " + name2 + lexically;
    }

    if (format.isJavaFamily()) {
      String name1 = var1().name_using(format);
      String name2 = var2().name_using(format);

      return "daikon.Quant." + (var1().aux.getFlag(VarInfoAux.HAS_ORDER)
                                ? "lexGT"
                                : "setEqual" )
        + "(" + name1 + ", " + name2 + ")";

    }

    return format_unimplemented(format);
  }

  public String format_simplify() {
    if (Invariant.dkconfig_simplify_define_predicates)
      return format_simplify_defined();
    else
      return format_simplify_explicit();
  }

  private String format_simplify_defined() {
    String[] var1_name = var1().simplifyNameAndBounds();
    String[] var2_name = var2().simplifyNameAndBounds();
    if (var1_name == null || var2_name == null) {
      return "format_simplify can't handle one of these sequences: "
        + format();
    }
    return "(|lexical->| " +
      var1_name[0] + " " + var1_name[1] + " " + var1_name[2] + " " +
      var2_name[0] + " " + var2_name[1] + " " + var2_name[2] + ")";
  }

  private String format_simplify_explicit() {

      String classname = this.getClass().toString().substring(6);
      return "warning: method " + classname
        + ".format_simplify_explicit() needs to be implemented: " + format();

  }

  public InvariantStatus check_modified(String /*@Interned*/ [] v1, String /*@Interned*/ [] v2, int count) {
    /// This does not do the right thing; I really want to avoid comparisons
    /// if one is missing, but not if one is zero-length.
    // // Don't make comparisons with empty arrays.
    // if ((v1.length == 0) || (v2.length == 0)) {
    //   return;
    // }

    int comparison = 0;
    if (orderMatters) {
      // Standard element wise comparison
       comparison = comparator.compare(v1, v2);
    } else {
      // Do a double subset comparison
      comparison = (ArraysMDE.isSubset (v1, v2) && ArraysMDE.isSubset ( v2, v1)) ? 0 : -1;
    }

    if (! (comparison > 0) ) {
      return InvariantStatus.FALSIFIED;
    }
    return InvariantStatus.NO_CHANGE;
  }

  public InvariantStatus add_modified(String /*@Interned*/ [] v1, String /*@Interned*/ [] v2, int count) {
    if (logDetail())
      log ("add_modified (" + ArraysMDE.toString(v1) + ", " +
           ArraysMDE.toString(v2) + ") ");
        return check_modified(v1, v2, count);
  }

  protected double computeConfidence() {

    return 1 - Math.pow(.5, ppt.num_values());
  }

  // For Comparison interface
  public double eq_confidence() {

      return Invariant.CONFIDENCE_NEVER;
  }

  public boolean isSameFormula(Invariant o) {
    return true;
  }

  public boolean isExclusiveFormula(Invariant o) {
    return false;
  }

  /**
   *  Since this invariant can be a postProcessed equality, we have to
   *  handle isObvious especially to avoid circular isObvious
   *  relations.
   **/
  public /*@Nullable*/ DiscardInfo isObviousStatically_SomeInEquality() {
    if (var1().equalitySet == var2().equalitySet) {
      return isObviousStatically (this.ppt.var_infos);
    } else {
      return super.isObviousStatically_SomeInEquality();
    }
  }

  /**
   *  Since this invariant can be a postProcessed equality, we have to
   *  handle isObvious especially to avoid circular isObvious
   *  relations.
   **/
  public /*@Nullable*/ DiscardInfo isObviousDynamically_SomeInEquality() {
    if (logOn())
      log ("Considering dynamically_someInEquality");
    if (var1().equalitySet == var2().equalitySet) {
      return isObviousDynamically (this.ppt.var_infos);
    } else {
      return super.isObviousDynamically_SomeInEquality();
    }
  }

  public /*@Nullable*/ DiscardInfo isObviousStatically(VarInfo[] vis) {

    return super.isObviousStatically (vis);
  }

  public /*@Nullable*/ DiscardInfo isObviousDynamically(VarInfo[] vis) {
    DiscardInfo super_result = super.isObviousDynamically(vis);
    if (super_result != null) {
      return super_result;
    }
    assert ppt != null;

    return null;
  }

  public void repCheck() {
    super.repCheck();
    /*
      This code is no longer needed now that the can_be_x's are gone
    if (! (this.can_be_eq || this.can_be_lt || this.can_be_gt)
        && ppt.num_samples() != 0) {
      System.err.println (this.repr());
      System.err.println (this.ppt.num_samples());
      throw new Error();
    }
    */
  }

  public boolean isEqual() {

    return false;
  }

  // Look up a previously instantiated invariant.
  public static /*@Nullable*/ SeqSeqStringGreaterThan find(PptSlice ppt) {
    assert ppt.arity() == 2;
    for (Invariant inv : ppt.invs) {
      if (inv instanceof SeqSeqStringGreaterThan)
        return (SeqSeqStringGreaterThan) inv;
    }
    return null;
  }

  /**
   * Returns a list of non-instantiating suppressions for this invariant.
   */
  public /*@Nullable*/ NISuppressionSet get_ni_suppressions() {
    return (suppressions);
  }

  /** Definition of this invariant (the suppressee) **/
  private static NISuppressee suppressee
    = new NISuppressee (SeqSeqStringGreaterThan.class, 2);

    private static /*@Nullable*/ NISuppressionSet suppressions = null;

}
