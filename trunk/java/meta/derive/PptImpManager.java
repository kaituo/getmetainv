package meta.derive;

import static daikon.tools.nullness.NullnessUtils.castNonNullDeep;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import java.util.logging.Level;

import daikon.Daikon;
import daikon.Global;
import daikon.PptMap;
import daikon.PptName;
import daikon.PptTopLevel;
import daikon.ProglangType;
import daikon.VarInfo;
import daikon.inv.Implication;
import daikon.inv.Invariant;
import daikon.inv.OutputFormat;
import daikon.inv.filter.InvariantFilters;
import daikon.simplify.InvariantLemma;
import daikon.simplify.Lemma;
import daikon.simplify.LemmaStack;
import daikon.simplify.SessionManager;
import daikon.simplify.SimplifyError;

public class PptImpManager {
//	PptMap all_ppts2;
	private static /*@LazyNonNull*/ LemmaStack proverStack2 = null;
	public static final Comparator<Invariant> icfp2 =
		    new Invariant.InvariantComparatorForPrinting();
	ArrayList<PptConfidenceImp> pcilist = null;
	ArrayList<PptConfidenceImp> pcilistafterselection = null;
	double filterEnter, filterExit;
	
	public PptImpManager(double filterEnter, double filterExit) {//PptMap pm, 
//		all_ppts2 = pm;
		pcilist = new ArrayList<PptConfidenceImp>();
		pcilistafterselection = new ArrayList<PptConfidenceImp>();
		this.filterEnter = filterEnter;
		this.filterExit = filterExit;
	}
	
//	public void markWithSimplify() {
//		for (Iterator<PptTopLevel> itor = all_ppts2.ppt_all_iterator(); itor.hasNext();) {
//		      PptTopLevel ppt = itor.next();
//		      ppt.mark_implied_via_simplify(all_ppts2);
//		}
//	}
	
	public String getClassname(PptTopLevel p) {
		String name = p.name();
		int firstIndex = name.indexOf("(");
		if(firstIndex == -1) {
//			int lastIndex = name.indexOf(":");
//			return name.substring(0, lastIndex);
			return null;
		} else {
			String subname = name.substring(0, firstIndex);
			int lastIndex = subname.lastIndexOf(".");
			return subname.substring(0, lastIndex);
		}
		
	}
	
	// callled after getClassname
	public String getMethodname(PptTopLevel p) {
		String name = p.name();
		int firstIndex = name.indexOf("(");
		if(firstIndex != -1) {
			return name.substring(0, firstIndex);
		} else
			return null;
		
	}
	
	public int endsWithEnterExit(String cn) {
		if (cn.endsWith("ENTER"))
			return 0;
		else if (cn.endsWith("EXIT"))
			return 1;
		else
			return -1;
	}
	
	public void selectPptConfidenceImp() {
		int s = pcilist.size();
		for(int i=0; i<s; i++) {
			PptConfidenceImp pci = pcilist.get(i);
			pci.computeSuccessrate();
			if(pci.getMtype() == MetaType.SUBDOMAIN || pci.getMtype() == MetaType.CANFOLLOW) {
				if (pci.getSuccessrate() >= filterEnter)
					pcilistafterselection.add(pci);
			}
			else {
				if (pci.getSuccessrate() >= filterExit)
					pcilistafterselection.add(pci);
			}
		}
	}
	
	public void transformSelectedPptConfidenceImp() {
		int s = pcilistafterselection.size();
		for(int i=0; i<s; i++) {
			PptConfidenceImp pci = pcilistafterselection.get(i);
			pci.multiplyConfidence();
			pci.averageImpConfidence();
		}
	}
	
	public PptConfidenceImp[] sortSelectedPptConfidenceImp() {
		PptConfidenceImp[] pcis = pcilistafterselection.toArray(new PptConfidenceImp[pcilistafterselection.size()]);
		Arrays.sort(pcis, new PptImpComparator());
		return pcis;
	}
	
	public PptConfidenceImp[] inferWithSimplify(PptMap all_ppts) {
		mark_implied_via_simplify(all_ppts);
		selectPptConfidenceImp();
		transformSelectedPptConfidenceImp();
		return sortSelectedPptConfidenceImp();
	}
	
	
	public void printPptConfidenceImps(PptConfidenceImp[] p) {
		int s = p.length;
		for(int i=0; i < s; i++)
			System.out.println(p[i]);
	}
	
	public void clear() {
		proverStack2 = null;
		pcilist.clear();
		pcilistafterselection.clear();
	}
	
	
	@SuppressWarnings("nullness") // reinitialization if error occurs
	  public void mark_implied_via_simplify(PptMap all_ppts) {
		for (PptTopLevel ppt1 : all_ppts.all_ppts()) {
			String classname1 = getClassname(ppt1);
			for (PptTopLevel ppt2 : all_ppts.all_ppts()) {
				String classname2 = getClassname(ppt2);
				if(ppt1.name().equals("DataStructures.StackAr.StackAr(int):::ENTER") && ppt2.name().equals("DataStructures.StackAr.push(java.lang.Object):::ENTER")) 
					System.out.print("");
				int ppt1ends = endsWithEnterExit(ppt1.name());
				int ppt2ends = endsWithEnterExit(ppt2.name());
				
				
				if( ppt1ends != -1 && ppt2ends != -1  && ppt1.invariant_cnt()!= 0 && ppt2.invariant_cnt()!= 0 && !ppt1.equals(ppt2) && classname1 != null && classname2 != null && classname1.equals(classname2)) {
					String methodname1 = getMethodname(ppt1);
					String methodname2 = getMethodname(ppt2);
					assert methodname1 != null;
					assert methodname2 != null;
					// methodname1 cannot be null
					if(methodname1.equals(methodname2))
						continue;
					
					// enter1==>enter2: checking whether their parameter numbers and types are the same
					if(ppt1ends == 0 && ppt2ends == 0) {
						int ppt1ParamNumber = computeNumberofParam(ppt1.name());
						int ppt2ParamNumber = computeNumberofParam(ppt2.name());
						if(ppt1ParamNumber != ppt2ParamNumber)
							continue;
						else {
							ProglangType[] ppt1ParamTypes = paramType(ppt1, ppt1ParamNumber);
							ProglangType[] ppt2ParamTypes = paramType(ppt2, ppt1ParamNumber);
							if(!cmpParamTypes(ppt1ParamTypes, ppt2ParamTypes, ppt1ParamNumber))
								continue;
						}
					}
					// exit1==>exit2: check whether their return cannot be the same
					if(ppt1ends == 1 && ppt2ends == 1) {
						ProglangType retType1 = returnType(ppt1);
						ProglangType retType2 = returnType(ppt2);
						if (retType1 == null && retType2 == null)
							;
						else if((retType1 == null && retType2 != null) || (retType2 == null && retType1 != null))
							continue;
						else if((retType1.isPrimitive() && retType2.isObject()) || (retType2.isPrimitive() && retType1.isObject()))
							continue;
						else if((retType1.baseIsBoolean() && retType2.baseIsIntegral()) || (retType2.baseIsBoolean() && retType1.baseIsIntegral()))
							continue;	
						else if(retType1.baseIsHashcode() != retType2.baseIsHashcode())
							continue;	
					}
					try {
					      if (proverStack2 == null)
					        proverStack2 = new LemmaStack();
					      PptConfidenceImp pci = markImpliedViaSimplify_int2(ppt1, ppt2, ppt1ends, ppt2ends, new SimplifyInclusionTester2() {
					        public boolean include(Invariant inv) {
					          return InvariantFilters.defaultFilters().shouldKeep(inv)
					            == null;
					        }
					      });
					      if(pci != null)
					    	  pcilist.add(pci);
					    } catch (SimplifyError e) {
					      proverStack2 = null;
					    }
				}
			}
		}
	    
	  }
	
	public ProglangType returnType(PptTopLevel receiver) {
		ProglangType retType = null;
		for (Iterator<VarInfo> i = receiver.var_info_iterator(); i.hasNext();) {
	        VarInfo var = i.next();
	        if(var.name().equals("return")) {
	        	retType = var.type; //file_rep_type;
	        	break;
	        } 
		}
		return retType; 
	}
	
	public ProglangType[] paramType(PptTopLevel receiver, int num) {
		assert num < 10; // number of parameters is less than 10
		ProglangType retType[] = new ProglangType[num];
		for (Iterator<VarInfo> i = receiver.var_info_iterator(); i.hasNext();) {
	        VarInfo var = i.next();
	        String varName = var.name();
	        if(varName.startsWith("param") && varName.length() == 6) {
	        	char index = varName.charAt(5);
	        	int indexInt = Character.getNumericValue(index);
	        	retType[indexInt-1] = var.type;
	        } 
		}
		return retType; 
	}
	
	public boolean cmpParamTypes(ProglangType[] params1, ProglangType[] params2, int size) {
		boolean isComparable = true;
		for(int i=0; i<size; i++) {
			if((params1[i].baseIsString() &&  !params2[i].baseIsString()) || (!params1[i].baseIsString() &&  params2[i].baseIsString()) )
				isComparable = false;
			else if((params1[i].isPrimitive() && params2[i].isObject()) || (params2[i].isPrimitive() && params1[i].isObject()))
				isComparable = false;
			else if((params1[i].baseIsBoolean() && params2[i].baseIsIntegral()) || (params2[i].baseIsBoolean() && params1[i].baseIsIntegral()))
				isComparable = false;	
			else if(params1[i].baseIsHashcode() != params2[i].baseIsHashcode())
				isComparable = false;	
		}
		return isComparable;
	}
	
	int computeNumberofParam(String methodSig) {
		int leftP = methodSig.indexOf("(");
		int rightP = methodSig.indexOf(")");
		if(rightP == leftP + 1)
			return 0;
		String subMethodSig = methodSig.substring(leftP+1, rightP);
		int len = subMethodSig.length();
		int count = 0;
		for(int i=0; i < len; i++)
			if(subMethodSig.charAt(i) == ',')
				count++;
		return count+1;
	}
	
	/**
	   * Interface used by mark_implied_via_simplify to determine what
	   * invariants should be considered during the logical redundancy
	   * tests.
	   **/
	  public static interface SimplifyInclusionTester2 {
	    public boolean include(Invariant inv);
	  }
	  
	  /**
	   * Returns true if there was a problem with Simplify formatting (such as
	   * the invariant not having a Simplify representation).
	   **/
	  private static boolean format_simplify_problem(String s) {
	    return (
	      (s.indexOf("Simplify") >= 0)
	        || (s.indexOf("format(OutputFormat:Simplify)") >= 0)
	        || (s.indexOf("format_simplify") >= 0));
	  }
	  
	  private Invariant[] getInvariants_vector(PptTopLevel p, SimplifyInclusionTester2 test) {
		  Invariant[] invs;
		// Replace parwise equality with an equivalence set
	      Vector<Invariant> all_noeq = p.invariants_vector();
	      Collections.sort(all_noeq, icfp2);
	      List<Invariant> all = InvariantFilters.addEqualityInvariants(all_noeq);
	      Collections.sort(all, icfp2);
	      Vector<Invariant> printing = new Vector<Invariant>();
	      for (Iterator<Invariant> _invs = all.iterator(); _invs.hasNext();) {
	        Invariant inv = _invs.next();
	        if (test.include(inv)) { // think: inv.isWorthPrinting()
	          String fmt = inv.format_using(OutputFormat.SIMPLIFY);
	          if (!format_simplify_problem(fmt)) {
	            // If format_simplify is not defined for this invariant, don't
	            // confuse Simplify with the error message
	            printing.add(inv);
	          }
	        }
	      }
	      invs = printing.toArray(new Invariant[printing.size()]);
	      return invs;
	  }
	  
	  private void logSimplify4Debug(Invariant[] invs, PptName name) {
		  Global.debugSimplify.fine("Sorted invs for: " + name);
	      for (int i = 0; i < invs.length; i++) {
	        Global.debugSimplify.fine("    " + invs[i].format());
	      }
	      for (int i = 0; i < invs.length - 1; i++) {
	        int cmp = icfp2.compare(invs[i], invs[i + 1]);
	        Global.debugSimplify.fine("cmp(" + i + "," + (i + 1) + ") = " + cmp);
	        int rev_cmp = icfp2.compare(invs[i + 1], invs[i]);
	        Global.debugSimplify.fine(
	          "cmp(" + (i + 1) + "," + i + ") = " + rev_cmp);
	        assert rev_cmp >= 0;
	      }
	  }
	  
	  InvariantLemma[] createLemmas(Invariant[] invs) {
		  InvariantLemma[] lemmas1 = new InvariantLemma[invs.length];
		  for (int i = 0; i < invs.length; i++)
		     lemmas1[i] = new InvariantLemma(invs[i]);
		  lemmas1 = castNonNullDeep(lemmas1); // issue 154
		  return lemmas1;
	  }
	  
	void checkConflict(Invariant[] invs, InvariantLemma[] lemmas, PptName ppt_name, int backgroundMark,
			boolean[] present) {
		for (int i = 0; i < invs.length; i++)
			proverStack2.pushLemma(lemmas[i]);

		// If the background is necessarily false, we are in big trouble
		if (proverStack2.checkForContradiction() == 'T') {
			// kaituo: We don't try to recover here if code reaches here; we
			// trust what Daikon previously does;
			// if there is some error, then it's our fault by some unexpected
			// error.
			// System.err.println("We are in big trouble");
			// Uncomment to punt on contradictions
			if (!LemmaStack.dkconfig_remove_contradictions) {
				System.err.println("Warning: " + ppt_name
						+ " invariants are contradictory, giving up");
				if (LemmaStack.dkconfig_print_contradictions) {
					LemmaStack.printLemmas(System.err,
							proverStack2.minimizeContradiction());
				}
			}
			System.err.println("Warning: " + ppt_name
					+ " invariants are contradictory, axing some");
			Map<Lemma, Integer> demerits = new TreeMap<Lemma, Integer>();
			int worstWheel = 0;
			do {
				// But try to recover anyway
				Vector<Lemma> problems = proverStack2.minimizeContradiction();
				if (LemmaStack.dkconfig_print_contradictions) {
					System.err.println("Minimal set:");
					LemmaStack.printLemmas(System.err,
							proverStack2.minimizeContradiction());
					System.err.println();
				}
				if (problems.size() == 0) {
					System.err.println("Warning: removal failed, punting");
					return;
				}
				for (Lemma problem : problems) {
					if (demerits.containsKey(problem))
						demerits.put(problem, new Integer(demerits.get(problem)
								.intValue() + 1));
					else
						demerits.put(problem, new Integer(1));
				}
				int max_demerits = -1;
				Vector<Lemma> worst = new Vector<Lemma>();
				for (Map.Entry</* @KeyFor("demerits") */Lemma, Integer> ent : demerits
						.entrySet()) {
					int value = ent.getValue().intValue();
					if (value == max_demerits) {
						worst.add(ent.getKey());
					} else if (value > max_demerits) {
						max_demerits = value;
						worst = new Vector<Lemma>();
						worst.add(ent.getKey());
					}
				}
				int offsetFromEnd = worstWheel % worst.size();
				worstWheel = (3 * worstWheel + 1) % 10000019;
				int index = worst.size() - 1 - offsetFromEnd;
				Lemma bad = worst.elementAt(index);
				demerits.remove(bad);
				proverStack2.popToMark(backgroundMark);
				boolean isInvariant = false;
				for (int i = 0; i < lemmas.length; i++) {
					@SuppressWarnings("interning")
					// list membership
					boolean isBad = (lemmas[i] == bad);
					if (isBad) {
						present[i] = false;
						isInvariant = true;
					} else if (present[i]) {
						proverStack2.pushLemma(lemmas[i]);
					}
				}
				if (!isInvariant)
					proverStack2.removeLemma(bad);
				if (LemmaStack.dkconfig_print_contradictions) {
					System.err.println("Removing " + bad.summarize());
				} else if (Daikon.no_text_output && Daikon.show_progress) {
					System.err.print("x");
				}
			} while (proverStack2.checkForContradiction() == 'T');
		}

		proverStack2.popToMark(backgroundMark);
	}
	  
	  /**
	   * Use the Simplify theorem prover to flag invariants that are
	   * logically implied by others.  Uses the provided test interface to
	   * determine if an invariant is within the domain of inspection.
	   **/
	  /*@NonNullOnEntry("proverStack")*/
	  private PptConfidenceImp markImpliedViaSimplify_int2(
	    PptTopLevel p1,
	    PptTopLevel p2,
	    int ppt1ends,
	    int ppt2ends,
	    SimplifyInclusionTester2 test)
	    throws SimplifyError {
	    SessionManager.debugln("Simplify checking " + p1.ppt_name + "==>" + p2.ppt_name);

	    // Create the list of invariants from this ppt which are
	    // expressible in Simplify
	    Invariant[] invs1, invs2;
	    invs1 = getInvariants_vector(p1, test);
	    invs2 = getInvariants_vector(p2, test);
	    
	    // For efficiency, bail if we don't have any invariants to mark as implied
	    if (invs1.length == 0 || invs2.length == 0) {
	      return null;
	    }

	    // Come up with a "desirability" ordering of the printing and
	    // expressible invariants, so that we can remove the least
	    // desirable first.  For now just use the ICFP.
	    Arrays.sort(invs1, icfp2);
	    Arrays.sort(invs2, icfp2);

	    // Debugging
	    if (Global.debugSimplify.isLoggable(Level.FINE)) {
	    	logSimplify4Debug(invs1, p1.ppt_name);
	    	logSimplify4Debug(invs2, p2.ppt_name);
	    }

//	    // The below two paragraphs of code (whose end result is to
//	    // compute "background") should be changed to use the VarInfo
//	    // partial ordering to determine background invariants, instead of
//	    // the (now deprecated) controlling_ppts relationship.
//
//	    // Form the closure of the controllers; each element is a Ppt
//	    Set<PptTopLevel> closure = new LinkedHashSet<PptTopLevel>();
//	    {
//	      Set<PptTopLevel> working = new LinkedHashSet<PptTopLevel>();
//	      while (!working.isEmpty()) {
//	        PptTopLevel ppt = working.iterator().next();
//	        working.remove(ppt);
//	        if (!closure.contains(ppt)) {
//	          closure.add(ppt);
//	        }
//	      }
//	    }
//
//	    // Create the conjunction of the closures' invariants to form a
//	    // background environment for the prover.  Ignore implications,
//	    // since in the current scheme, implications came from controlled
//	    // program points, and we don't necessarily want to lose the
//	    // unconditional version of the invariant at the conditional ppt.
//	    for (PptTopLevel ppt : closure) {
//	      Vector<Invariant> invs_vec = ppt.invariants_vector();
//	      Collections.sort(invs_vec, icfp);
//	      for (Invariant inv : InvariantFilters.addEqualityInvariants(invs_vec)) {
//	        if (inv instanceof Implication) {
//	          continue;
//	        }
//	        if (!test.include(inv)) { // think: !inv.isWorthPrinting()
//	          continue;
//	        }
//	        String fmt = inv.format_using(OutputFormat.SIMPLIFY);
//	        if (format_simplify_problem(fmt)) {
//	          // If format_simplify is not defined for this invariant, don't
//	          // confuse Simplify with the error message
//	          continue;
//	        }
//	        // We could also consider testing if the controlling invariant
//	        // was removed by Simplify, but what would the point be?  Also,
//	        // these "intermediate goals" might help out Simplify.
//	        proverStack.pushLemma(new InvariantLemma(inv));
//
//	        // If this is the :::OBJECT ppt, also restate all of them in
//	        // orig terms, since the conditions also held upon entry.
//	        if (ppt.ppt_name.isObjectInstanceSynthetic())
//	          proverStack.pushLemma(InvariantLemma.makeLemmaAddOrig(inv));
//	      }
//	    }


	    if (proverStack2.checkForContradiction() == 'T') {
	      if (LemmaStack.dkconfig_remove_contradictions) {
	        System.err.println(
	          "Warning: "
	            + p1.ppt_name + "==>" + p2.ppt_name
	            + " background is contradictory, "
	            + "removing some parts");
	        proverStack2.removeContradiction();
	      } else {
	        System.err.println(
	          "Warning: " + p1.ppt_name + "==>" + p2.ppt_name + " background is contradictory, giving up");
	        return null;
	      }
	    }

	    int backgroundMark = proverStack2.markLevel();

	    /*NNC:@LazyNonNull*/ InvariantLemma[] lemmas1 = createLemmas(invs1);
	    InvariantLemma[] lemmas2 = createLemmas(invs2);
	    
	    boolean[] present1 = new boolean[lemmas1.length];
	    Arrays.fill(present1, 0, present1.length, true);
	    
	    boolean[] present2 = new boolean[lemmas2.length];
	    Arrays.fill(present2, 0, present2.length, true);
//	    for (int checking = invs1.length - 1; checking >= 0; checking--) {
//	      Invariant inv = invs1[checking];
//	      StringBuffer bg = new StringBuffer("(AND ");
//	      for (int i = 0; i < present.length; i++) {
//	        if (present[i] && (i != checking)) {
//	          bg.append(" ");
//	          // format_using(OutputFormat.SIMPLIFY) is guaranteed to return
//	          // a sensible result xfor invariants in invs[].
//	          bg.append(invs1[i].format_using(OutputFormat.SIMPLIFY));
//	        }
//	      }
//	      bg.append(")");
//
//	      // Debugging
//	      if (Global.debugSimplify.isLoggable(Level.FINE)) {
//	        SessionManager.debugln("Background:");
//	        for (int i = 0; i < present.length; i++) {
//	          if (present[i] && (i != checking)) {
//	            SessionManager.debugln("    " + invs1[i].format());
//	          }
//	        }
//	      }
//	    }

	    checkConflict(invs1, lemmas1, p1.ppt_name, backgroundMark,
				present1);
	    checkConflict(invs2, lemmas2, p2.ppt_name, backgroundMark,
				present2);

	    PptConfidenceImp pci = flagRedundantnonRecursive(p1, lemmas1, invs1, present1, 0, lemmas1.length - 1, p2, lemmas2, invs2, present2, 0, lemmas2.length - 1, ppt1ends, ppt2ends);

	    proverStack2.clear();
	    
	    return pci;
	  }
	  
	  
	  
	  /** Go though an array of invariants, marking those that can be
	   * proved as consequences of others as redundant. */
	  /** Kaituo: a clever resursion to visit every element of an array
	   * so that each element can be implied by others together exactly once;
	   * I don't need to do this; I need a simple loop to visit element once
	   * using: all axioms of P1 => each element of P2
	   */
	  /*@NonNullOnEntry("proverStack")*/
	  private PptConfidenceImp flagRedundantnonRecursive(
	    PptTopLevel p1,
	    InvariantLemma[] lemmas1,
	    Invariant[] invs1,
	    boolean[] present1,
	    int start1,
	    int end1,
	    PptTopLevel p2,
	    InvariantLemma[] lemmas2,
	    Invariant[] invs2,
	    boolean[] present2,
	    int start2,
	    int end2,
	    int ppt1ends,
	    int ppt2ends)
	    throws SimplifyError {
	    assert start1 <= end1;
	    assert start2 <= end2;
	    
	    boolean checkingParam = false;
	    boolean checkingReturn = false;
//	    boolean checkingReturnEquals = false;
//	    boolean checkingfailureParam = false;
	    
	    if(ppt1ends == 1 && ppt2ends == 1) {
	    	checkingParam = true;
	    	// exit1==>exit2: checking whether their returns are incompatible (e.g. exit2 has primitive returns while exit1 does not;)
	    	//checkingReturnEquals = true;
	    }
	    else if(ppt1ends == 1 && ppt2ends == 0)
	    	checkingParam = true;
	    else if(ppt1ends == 0 && ppt2ends == 1) {
	    	checkingReturn = true;
	    	checkingParam = true;
	    }
	    
	    
	    PptConfidenceImp pci = new PptConfidenceImp(p1, p2, getMetaType(ppt1ends, ppt2ends)); 
	    Set<String> uniqVarsSet = new HashSet<String>();
	    
	    for (int i = start1; i <= end1; i++) {
	    	double conf1 = invs1[i].getConfidence();
	    	if(invs1[i].numVars() == 1)
	    		addToVarSet(uniqVarsSet, invs1[i].ppt.var_infos);
	        if (conf1> 0 && present1[i]) {
	          proverStack2.pushLemma(lemmas1[i]);
	          pci.putConfidence_p1(conf1);
	        }
	    }
	    
	    for (int checking = start2; checking <= end2; checking++) {
	    	double conf2 = invs2[checking].getConfidence();
	    	// don't check an invariant if it contains Orig variable
	    	if(lemmas2[checking].containsOrig() || ((invs2[checking] instanceof Implication) && invs2[checking].toString().contains("orig(") ))
	    		continue;
	    	
	    	// don't check an dinvariant if both ppts are EXIT and the invariant contains param
	    	if(checkingParam && lemmas2[checking].containsParam())
	    		continue;
	    	
	    	// don't check an invariant in a follows relationship if the invariant contains a return
	    	if(checkingReturn && lemmas2[checking].containsReturn())
	    		continue;
	    	
	        if (conf2> 0 && present2[checking]) { 
	        	char checkingResult = proverStack2.checkLemma(lemmas2[checking]);
	        	if (checkingResult == 'T') {
		    		pci.incrementSuccess();
		    		pci.putConfidence_p2(conf2);
		    	} else if (checkingResult == 'F') {
		    		if(invs2[checking].toString().contains("==") && invs2[checking].numVars() == 1 && uniqVarsSet.contains(invs2[checking].ppt.var_infos[0].name()))//checkingReturnEquals && lemmas2[checking].containsReturn() && 
		    			return null;
		    	}
		    	pci.incrementTotal();
	        }
	    	
	    }
	    return pci;
	  }
	  
	  public void addToVarSet(Set<String> uniqVarsSet, VarInfo[] vars) {
		  for (int i = 0; i < vars.length; i++) {
		      uniqVarsSet.add(vars[i].name());
		  }
	  }
	  
	private MetaType getMetaType(int ppt1ends, int ppt2ends) {
		MetaType mt = MetaType.UNDEFINED;
		if (ppt1ends == 1 && ppt2ends == 1) {
			mt = MetaType.SUBRANGE;
		} else if (ppt1ends == 1 && ppt2ends == 0)
			mt = MetaType.CANFOLLOW;
		else if (ppt1ends == 0 && ppt2ends == 1) {
			mt = MetaType.FOLLOWS;
		} else if (ppt1ends == 0 && ppt2ends == 0) {
			mt = MetaType.SUBDOMAIN;
		}

		return mt;
	}
}
