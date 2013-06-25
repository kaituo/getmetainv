package daikon.simplify;

import daikon.*;
import daikon.inv.*;
import static daikon.inv.Invariant.asInvClass;

/** InvariantLemmas are Lemmas created by printing a Daikon invariant
 * in Simplify format, sometimes with some hacks.
 **/

public class InvariantLemma extends Lemma {
  public String from; // A note explaining our derivation
  public Invariant invariant; // A pointer back to the invariant we
                              // were made from

  public InvariantLemma(Invariant inv) {
    super(inv.format(), inv.format_using(OutputFormat.SIMPLIFY));
    from = inv.ppt.parent.name;
    invariant = inv;
  }

  public String summarize() {
    return summary + " from " + from;
  }

  /** If this lemma came from an invariant, get its class. */
  public Class<? extends Invariant> invClass() {
    Class<? extends Invariant> c;
    if (invariant instanceof GuardingImplication) {
      c = ((Implication)invariant).consequent().getClass();
    } else {
      c = invariant.getClass();
    }

    Class<?> outer = c.getDeclaringClass();
    if (outer != null) {
      c = asInvClass(outer);
    }
    return c;
  }
  
  //Kaituo
  public boolean containsOrig() {
	  return invariant.usesOrig();
  }
  
  //Kaituo
  public boolean containsParam() {
	  return invariant.usesParam();
  }
  
  //Kaituo
  public boolean containsReturn() {
	  return invariant.usesReturn();
  }

  /**
   * Make a lemma corresponding to the given invariant, except
   * referring to the prestate versions of all the variables that inv
   * referred to.
   **/
  public static InvariantLemma makeLemmaAddOrig(Invariant inv) {
    // XXX Side-effecting the invariant to change its ppt (and then
    // to change it back afterward) isn't such a hot thing to do, but
    // it isn't that hard, and seems to work.
    InvariantLemma result;
    if (inv instanceof Implication) {
      Implication imp = (Implication)inv;
      PptSlice lhs_saved = imp.predicate().ppt;
      PptSlice rhs_saved = imp.consequent().ppt;
      imp.predicate().ppt = PptSlice0.makeFakePrestate(lhs_saved);
      imp.consequent().ppt = PptSlice0.makeFakePrestate(rhs_saved);
      result = new InvariantLemma(imp);
      imp.predicate().ppt = lhs_saved;
      imp.consequent().ppt = rhs_saved;
    } else {
      PptSlice saved = inv.ppt;
      PptSlice orig = PptSlice0.makeFakePrestate(saved);
      inv.ppt = orig;
      result = new InvariantLemma(inv);
      inv.ppt = saved;
    }
    result.from += " (orig() added)";
    return result;
  }

}