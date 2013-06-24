// ***** This file is automatically generated from SeqIndexComparison.java.jpp

package daikon.inv.unary.sequence;

import daikon.*;
import daikon.inv.*;
import daikon.inv.binary.twoSequence.*;
import daikon.suppress.*;
import daikon.Quantify.QuantFlags;

import plume.*;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * Represents an invariant over sequences of double values between the
 * index of an element of the sequence and the element itself.
 * Prints as <samp>x[i] != i</samp>.
 **/
public class SeqIndexFloatNonEqual extends SingleFloatSequence {
  // We are Serializable, so we specify a version to allow changes to
  // method signatures without breaking serialization.  If you add or
  // remove fields, you should change this number to the current date.
  static final long serialVersionUID = 20040203L;

  /** Debug tracer. **/
  public static final Logger debug
    = Logger.getLogger ("daikon.inv.unary.sequence.SeqIndexFloatNonEqual");

  // Variables starting with dkconfig_ should only be set via the
  // daikon.config.Configuration interface.
  /**
   * Boolean.  True iff SeqIndexFloatNonEqual invariants should be considered.
   **/
  public static boolean dkconfig_enabled = false;

  private SeqIndexFloatNonEqual (/*>>> @Prototype SeqIndexFloatNonEqual this*/) {
    super ();
  }

  protected SeqIndexFloatNonEqual (PptSlice slice) {
    super (slice);
    assert slice != null;
    assert var().rep_type == ProglangType.DOUBLE_ARRAY;
  }

  private static /*@Nullable*/ /*@Prototype*/ SeqIndexFloatNonEqual proto;

  /** Returns the prototype invariant for SeqIndexFloatNonEqual **/
  public static /*@Prototype*/ SeqIndexFloatNonEqual get_proto() {
    if (proto == null)
      proto = new /*@Prototype*/ SeqIndexFloatNonEqual ();
    return (proto);
  }

  /** returns whether or not we are enabled **/
  public boolean enabled() {
    return (dkconfig_enabled && !dkconfig_SeqIndexDisableAll);
  }

  /** Check that SeqIndex comparisons make sense over these vars **/
  public boolean instantiate_ok (VarInfo[] vis) {

    if (!valid_types (vis))
      return (false);

    // Don't compare indices to object addresses.
    ProglangType elt_type = vis[0].file_rep_type.elementType();
    if (!elt_type.baseIsFloat())
      return (false);

    // Make sure that the indices are comparable to the elements
    VarInfo seqvar = vis[0];
    assert seqvar.comparability != null;
    VarComparability elt_compar = seqvar.comparability.elementType();
    VarComparability index_compar = seqvar.comparability.indexType(0);
    if (! VarComparability.comparable (elt_compar, index_compar)) {
      return (false);
    }

    return (true);
  }

  /** Instantiate the invariant on the specified slice **/
  public SeqIndexFloatNonEqual instantiate_dyn (/*>>> @Prototype SeqIndexFloatNonEqual this,*/ PptSlice slice) {
    return new SeqIndexFloatNonEqual (slice);
  }

  /** returns the ni-suppressions for SeqIndexFloatNonEqual **/
  public /*@Nullable*/ NISuppressionSet get_ni_suppressions() {
    return null;
  }

  protected Invariant resurrect_done_swapped() {

      return this;
  }

  public String getComparator() {
    return "!=";
  }

  public String format_using(OutputFormat format) {
    if (format.isJavaFamily()) return format_java_family(format);

    // TODO: Eliminate the unnecessary format_xxx() below if the
    // format_java_family() can handle all the Java family output.

    if (format == OutputFormat.DAIKON) return format_daikon();
    if (format == OutputFormat.ESCJAVA) return format_esc();
    if (format == OutputFormat.SIMPLIFY) return format_simplify();

    return format_unimplemented(format);
  }

  public String format_daikon() {

    // If this is an array/container and not a subsequence
    if (var().isDerivedSubSequenceOf() == null) {
      return var().apply_subscript ("i") + " != i";
    } else {
      return var().name() + " != (index)";
    }
  }

  // Bad code here: if the first index is changed from i this breaks
  public String format_esc() {
    String[] form = VarInfo.esc_quantify (var());
    return form[0] + "(" + form[1] + " != i)" + form[2];
  }

  public String format_java_family(OutputFormat format) {
    return "daikon.Quant.eltsNotEqualIndex("
      + var().name_using(format) + ")";
  }

  public String format_simplify() {
    String[] form = VarInfo.simplify_quantify (QuantFlags.include_index(),
                                               var());
    return form[0] + "(NEQ " + form[1] + " " + form[2] + ")"
      + form[3];
  }

  public InvariantStatus check_modified(double /*@Interned*/ [] a, int count) {
    for (int i=0; i<a.length; i++) {
      if (!(Global.fuzzy.ne (a[i], i)))
        return InvariantStatus.FALSIFIED;
    }
    return InvariantStatus.NO_CHANGE;
  }

  public InvariantStatus add_modified(double /*@Interned*/ [] a, int count) {

    if (logDetail())
      log ("Entered add_modified: ppt.num_values()==" +
           ppt.num_values() + ", sample== " + ArraysMDE.toString(a));
    InvariantStatus stat = check_modified (a, count);
    if (logDetail())
      log ("Exiting add_modified status = " + stat);

    return stat;
  }

  protected double computeConfidence() {

    // Make sure there have been some elements in the sequence
    ValueSet.ValueSetFloatArray vs = (ValueSet.ValueSetFloatArray) ppt.var_infos[0].get_value_set();
    if (vs.elem_cnt() == 0)
      return Invariant.CONFIDENCE_UNJUSTIFIED;

    int num_values = ppt.num_values();
    if (num_values == 0)
      return Invariant.CONFIDENCE_UNJUSTIFIED;

      int max_indx = vs.max_length() - 1;
      if ((vs.min() > max_indx) || (vs.max() < 0))
        return Invariant.CONFIDENCE_UNJUSTIFIED;
      else {
        long overlap = (long)(Math.min (vs.max(), max_indx) - Math.max (vs.min(), 0));

        if (overlap < 0)
          return Invariant.CONFIDENCE_UNJUSTIFIED;
        overlap++;
        double range1 = (double)max_indx + 1;
        double range2 = (vs.max()) - vs.min() + 1;

        // probability of being equal by chance
        //  = (overlap/range1) * (overlap/range2) * (1/overlap)
        //  = overlap/(range1 * range2)

        double confidence_one_nonequal = ((double)overlap)/(range1 * range2);

        // The factor of 2 is a hack, but this seems too stringent otherwise
        confidence_one_nonequal = Math.min(1.0, 2.0*confidence_one_nonequal);

        assert ! Double.isNaN(confidence_one_nonequal) &&
                          confidence_one_nonequal >= 0 &&
                          confidence_one_nonequal <= 1
        : "overlap=" + overlap
                          + ", range1=" + range1
                          + ", range2=" + range2
                          ;
        return 1 - Math.pow (1 - confidence_one_nonequal, vs.elem_cnt());
      }

  }

  public boolean isSameFormula(Invariant other) {
    return true;
  }

  public boolean isExclusiveFormula(Invariant other) {
    return false;
  }

  // Look up a previously instantiated invariant.
  public static /*@Nullable*/ SeqIndexFloatNonEqual find(PptSlice ppt) {
    assert ppt.arity() == 1;
    for (Invariant inv : ppt.invs) {
      if (inv instanceof SeqIndexFloatNonEqual)
        return (SeqIndexFloatNonEqual) inv;
    }
    return null;
  }

  /**
   * Checks to see if this is obvious over the specified variables
   * Implements the following checks: <pre>
   *
   *    (A[] subsequence B[]) ^ (B[i] op i) ==> A[i] op i
   * </pre>
   *
   * JHP: Its not obvious (to me) that this is true except when the
   * subsequence starts at index 0.  If B[] = {0, 1, 2, 3, 4} and
   * A[] = {2, 3, 4}, A[] is a subsequence of B[] and B[i] == i,
   * but A[i] = i is not true.
   */
  public /*@Nullable*/ DiscardInfo isObviousDynamically(VarInfo[] vis) {

    DiscardInfo super_result = super.isObviousDynamically(vis);
    if (super_result != null) {
      return super_result;
    }

    VarInfo seqvar = vis[0];

    // For each other sequence variable, if it is a supersequence of this
    // one and it has the same invariant, then this one is obvious.
    // We have to check for the same equality set here, because
    // isObviousDynamically is called for each member of the equality set.
    // We don't want other members of our own equality set to make this obvious
    PptTopLevel pptt = ppt.parent;
    for (int i=0; i<pptt.var_infos.length; i++) {
      VarInfo vi = pptt.var_infos[i];
      if (vi.equalitySet == seqvar.equalitySet)
        continue;
      if (SubSequenceFloat.isObviousSubSequenceDynamically(this, seqvar, vi)) {
        PptSlice1 other_slice = pptt.findSlice(vi);
        if (other_slice != null) {
          SeqIndexFloatNonEqual other_sine = SeqIndexFloatNonEqual.find(other_slice);
          if ((other_sine != null) && other_sine.enoughSamples()) {
            return new DiscardInfo(this, DiscardCode.obvious,
                        "The invariant " + format() + " over var "
                       + seqvar.name() + " also holds over "
                       +" the supersequence " + vi.name());
          }
        }
      }
    }

    return null;
  }

}
