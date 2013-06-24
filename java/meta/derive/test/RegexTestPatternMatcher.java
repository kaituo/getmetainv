package meta.derive.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTestPatternMatcher {
	  public static final String EXAMPLE_TEST = "DataStructures.StackAr.push(java.lang.Object):::ENTER";
	  public static final String EXAMPLE_TEST2 = "org.apache.commons.collections.FastHashMap.clear():::ENTER";
	  public static final String EXAMPLE_TEST3 = "org.apache.commons.collections.bag.HashBag.bdd(java.lang.Object):::ENTER";
	  public static final String EXAMPLE_TEST4 = "org.apache.commons.collections.bag.TestHashBag.add(java.lang.Object):::ENTER";
	  public static final String EXAMPLE_TEST5 = "org.apache.commons.collections.bag.TestHashBag$1.add(java.lang.Object):::ENTER";

	  
	  public static void main(String[] args) {
	    Pattern pattern = Pattern.compile("StackAr\\.");
	    // In case you would like to ignore case sensitivity you could use this
	    // statement
	    // Pattern pattern = Pattern.compile("\\s+", Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(EXAMPLE_TEST);
	    // Check all occurance
	    while (matcher.find()) {
	      System.out.print("Start index: " + matcher.start());
	      System.out.print(" End index: " + matcher.end() + " ");
	      System.out.println(matcher.group());
	    }
	    // Now create a new pattern and matcher to replace whitespace with tabs
	    Pattern replace = Pattern.compile("\\s+");
	    Matcher matcher2 = replace.matcher(EXAMPLE_TEST);
	    System.out.println(matcher2.replaceAll("\t"));
	    
	    String arg = "--ppt-select-pattern=StackAr.,--decl-file=stack.decls-DynComp";
	    String[] argsplit = arg.split (",");
	    for(int i=0; i<argsplit.length; i++)
	    	System.out.println(argsplit[i]);
	    
	    
	    Pattern pattern3 = Pattern.compile("org\\.apache\\.commons\\.collections\\.[A-Z]");
	    // In case you would like to ignore case sensitivity you could use this
	    // statement
	    // Pattern pattern = Pattern.compile("\\s+", Pattern.CASE_INSENSITIVE);
	    Matcher matcher3 = pattern3.matcher(EXAMPLE_TEST2);
	    // Check all occurance
	    while (matcher3.find()) {
	      System.out.print("Start index: " + matcher3.start());
	      System.out.print(" End index: " + matcher3.end() + " ");
	      System.out.println(matcher3.group());
	    }
	    
	    Matcher matcher4 = pattern3.matcher(EXAMPLE_TEST3);
	    // Check all occurance
	    while (matcher4.find()) {
	      System.out.print("Start index: " + matcher4.start());
	      System.out.print(" End index: " + matcher4.end() + " ");
	      System.out.println(matcher4.group());
	    }
	    
	    String c = "org.apache.commons.collections.DoubleOrderedMap$5";
	    if(c.contains("$"))
	    	System.out.println("$$");
	    else
	    	System.out.println("!!");
	    
	    System.out.println("Pattern5:");
	    Pattern pattern5 = Pattern.compile("AbstractLinkedListNode\\$Node");
	    // In case you would like to ignore case sensitivity you could use this
	    // statement
	    // Pattern pattern = Pattern.compile("\\s+", Pattern.CASE_INSENSITIVE);
	    Matcher matcher5 = pattern5.matcher(EXAMPLE_TEST5);
	    // Check all occurance
	    while (matcher5.find()) {
	      System.out.print("Start index: " + matcher5.start());
	      System.out.print(" End index: " + matcher5.end() + " ");
	      System.out.println(matcher5.group());
	    }
	    
	    System.out.println("Pattern6:");
	    Pattern pattern6 = Pattern.compile("TestHashBag\\.!a");
	    // In case you would like to ignore case sensitivity you could use this
	    // statement
	    // Pattern pattern = Pattern.compile("\\s+", Pattern.CASE_INSENSITIVE);
	    Matcher matcher6 = pattern6.matcher(EXAMPLE_TEST4);
	    // Check all occurance
	    while (matcher6.find()) {
	      System.out.print("Start index: " + matcher6.start());
	      System.out.print(" End index: " + matcher6.end() + " ");
	      System.out.println(matcher6.group());
	    }
	  }
	} 
