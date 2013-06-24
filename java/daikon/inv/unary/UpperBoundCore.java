// ***** This file is automatically generated from BoundCore.java.jpp

package daikon.inv.unary;

import daikon.*;
import daikon.inv.*;
import java.text.DecimalFormat;

import java.io.Serializable;

// One reason not to combine LowerBound and Upperbound is that they have
// separate justifications:  one may be justified when the other is not.

// What should we do if there are few values in the range?
// This can make justifying that invariant easier, because with few values
// naturally there are more instances of each value.
// This might also make justifying that invariant harder, because to get more
// than (say) twice the expected number of samples (under the assumption of
// uniform distribution) requires many samples.
// Which of these dominates?  Is the behavior what I want?

public class UpperBoundCore
  implements Serializable, Cloneable
{
  // We are Serializable, so we specify a version to allow changes to
  // method signatures without breaking serialization.  If you add or
  // remove fields, you should change this number to the current date.
  static final long serialVersionUID = 20030822L;

  static final int required_samples = 5; // for enoughSamples
  static final int required_samples_at_bound = 3; // for justification

  // max1 > max2 > max3
  public long max1 = Long.MIN_VALUE;
  public int num_max1 = 0;
  public long max2 = Long.MIN_VALUE;
  public int num_max2 = 0;
  public long max3 = Long.MIN_VALUE;
  public int num_max3 = 0;
  public long min = Long.MAX_VALUE;

  int samples = 0;

  public Invariant wrapper;

  public UpperBoundCore(Invariant wrapper) {
    this.wrapper = wrapper;
  }

  public long max() {
    return max1;
  }

  public UpperBoundCore clone() {
    try {
      return (UpperBoundCore) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new Error(); // can't happen
    }
  }

  private static DecimalFormat two_decimals = new java.text.DecimalFormat("#.##");

  public String repr() {
    long modulus = calc_modulus();
    long range = calc_range();
    double avg_samples_per_val = calc_avg_samples_per_val(modulus, range);
    return "max1=" + max1
      + ", num_max1=" + num_max1
      + ", max2=" + max2
      + ", num_max2=" + num_max2
      + ", max3=" + max3
      + ", num_max3=" + num_max3
      + ", min=" + min + ", range=" + range + ", " +
      "avg_samp=" + two_decimals.format(avg_samples_per_val);
  }

  private double calc_avg_samples_per_val(long modulus, double range) {
    // int num_samples = wrapper.ppt.num_mod_samples();
    int num_samples = wrapper.ppt.num_samples();
    double avg_samples_per_val =
      ((double) num_samples) * modulus / range;
    avg_samples_per_val = Math.min(avg_samples_per_val, 100);

    return avg_samples_per_val;
  }

  private long calc_range() {
    // If I used Math.abs, the order of arguments to minus would not matter.
    return -(min - max1) + 1;
  }

  private long calc_modulus() {
    // Need to reinstate this at some point.
    // {
    //   for (Invariant inv : wrapper.ppt.invs) {
    //     if ((inv instanceof Modulus) && inv.enoughSamples()) {
    //       modulus = ((Modulus) inv).modulus;
    //       break;
    //     }
    //   }
    // }
    return 1;
  }

  /**
   * Whether this would change if the given value was seen.  Used to
   * test for need of cloning and flowing before this would be
   * changed.
   **/
  public boolean wouldChange (long value) {
    long v = value;
    return (value > max1);
  }

  public InvariantStatus add_modified(long value, int count) {
    samples += count;

    // System.out.println("UpperBoundCore" + varNames() + ": "
    //                    + "add(" + value + ", " + modified + ", " + count + ")");

    long v = value;

    if (v < min) {
      min = v;
    }

    if (v == max1) {
      num_max1 += count;
    } else if (v > max1) {
      max3 = max2;
      num_max3 = num_max2;
      max2 = max1;
      num_max2 = num_max1;
      max1 = v;
      num_max1 = count;
      return InvariantStatus.WEAKENED;
    } else if (v == max2) {
      num_max2 += count;
    } else if (v > max2) {
      max3 = max2;
      num_max3 = num_max2;
      max2 = v;
      num_max2 = count;
    } else if (v == max3) {
      num_max3 += count;
    } else if (v > max3) {
      max3 = v;
      num_max3 = count;
    }
    return InvariantStatus.NO_CHANGE;
  }

  public InvariantStatus check(long value) {
    if (value > max1) {
      return InvariantStatus.WEAKENED;
    } else {
      return InvariantStatus.NO_CHANGE;
    }
  }

  public boolean enoughSamples() {
    return samples > required_samples;
  }

  // Convenience methods; avoid need for "Invariant." prefix.
  private final double prob_is_ge(double x, double goal) {
    return Invariant.prob_is_ge(x, goal);
  }
  private final double prob_and(double p1, double p2) {
    return Invariant.prob_and(p1, p2);
  }
  private final double prob_or(double p1, double p2) {
    return Invariant.prob_or(p1, p2);
  }

  public double computeConfidence() {
    if (PrintInvariants.dkconfig_static_const_infer && matchConstant())
      return Invariant.CONFIDENCE_JUSTIFIED;
    
    return 1 - computeProbability();
  }
  
  public boolean matchConstant() {
    PptTopLevel pptt = wrapper.ppt.parent;

    for (VarInfo vi : pptt.var_infos) {
      if (vi.isStaticConstant()) {
        if (vi.rep_type == ProglangType.DOUBLE) {
          // If variable is a double, then use fuzzy comparison
          Double constantVal = (Double)vi.constantValue();
          if (Global.fuzzy.eq(constantVal, max1) || (Double.isNaN(constantVal) && Double.isNaN(max1)))
            return true;
        } else {
          // Otherwise just use the equals method
          Object constantVal = vi.constantValue();
          if (constantVal.equals(max1)) {
            return true;
          }
        }
      }
    }
    
    return false;
  }

  // used by computeConfidence
  public double computeProbability() {
    // The bound is justified if both of two conditions is satisfied:
    //  1. there are at least required_samples_at_bound samples at the bound
    //  2. one of the following holds:
    //      2a. the bound has five times the expected number of samples (the
    //          number it would have if the values were uniformly distributed)
    //      2b. the bound and the two next elements all have at least half
    //          the expected number of samples.
    // The expected number of samples is the total number of samples
    // divided by the range of the samples; it is the average number
    // of samples at each point.

    // Value "1" from above.
    double bound_samples_prob = prob_is_ge(num_max1, required_samples_at_bound);

    long modulus = calc_modulus();

    double range = calc_range();
    double avg_samples_per_val = calc_avg_samples_per_val(modulus, range);

    // Value "2a" from above
    double trunc_prob = prob_is_ge(num_max1, 5*avg_samples_per_val);

    // Value "2b" from above
    double unif_prob;
    boolean unif_mod_OK = ((-(max3 - max2) == modulus)
                           && (-(max2 - max1) == modulus));
    if (unif_mod_OK) {
      double half_avg_samp = avg_samples_per_val/2;
      double unif_prob_1 = prob_is_ge(num_max1, half_avg_samp);
      double unif_prob_2 = prob_is_ge(num_max2, half_avg_samp);
      double unif_prob_3 = prob_is_ge(num_max3, half_avg_samp);
      unif_prob = Invariant.prob_and(unif_prob_1, unif_prob_2, unif_prob_3);
      // System.out.println("Unif_probs: " + unif_prob + " <-- " + unif_prob_1 + " " + unif_prob_2 + " " + unif_prob_3);
    } else {
      unif_prob = 1;
    }

    // Value "2" from above
    double bound_prob = prob_or(trunc_prob, unif_prob);

    // Final result
    return prob_and(bound_samples_prob, bound_prob);

    // System.out.println("UpperBoundCore.computeProbability(): ");
    // System.out.println("  " + repr());
    // System.out.println("  ppt=" + wrapper.ppt.name()
    //                    + ", wrapper.ppt.num_mod_samples()="
    //                    + wrapper.ppt.num_mod_samples()
    //                    // + ", values=" + values
    //                    + ", avg_samples_per_val=" + avg_samples_per_val
    //                    + ", result = " + result
    //                    + ", bound_samples_prob=" + bound_samples_prob
    //                    + ", bound_prob=" + bound_prob
    //                    + ", trunc_prob=" + trunc_prob
    //                    + ", unif_prob=" + unif_prob);
    // PptSlice pptsg = (PptSlice) ppt;
    // System.out.println("  " + ppt.name());

  }

  public boolean isSameFormula(UpperBoundCore other) {
    return max1 == other.max1;
  }

  public boolean isExact() {
    return false;
  }

  // Merge lbc into this.
  public void add (UpperBoundCore lbc) {

    // Pass each value and its count to this invariant's add_modified.  Since
    // bound is never destroyed, we don't need to check the results.
    if (lbc.num_max1 > 0)
      add_modified (lbc.max1, lbc.num_max1);
    if (lbc.num_max2 > 0)
      add_modified (lbc.max2, lbc.num_max2);
    if (lbc.num_max3 > 0)
      add_modified (lbc.max3, lbc.num_max3);
    // num_max1 will be positive if and only if we've ever seen any
    // real samples. Only then does min represent a real sample.
    if (lbc.num_max1 > 0)
      add_modified (lbc.min, 1);
    if (Debug.logDetail())
      wrapper.log ("Added vals " + lbc.num_max1 + " of " + lbc.max1 + ","
                  + lbc.num_max2 + " of " + lbc.max2 + ","
                  + lbc.num_max3 + " of " + lbc.max3 + ", "
                  + ((lbc.num_max1 > 0) ?
                     "1 of " + lbc.min : "")
                  + " from ppt " + lbc.wrapper.ppt.parent.ppt_name);
  }

}
