// If you edit this file, you must also edit its tests.
// For tests of this and the entire plume package, see class TestPlume.

package plume;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.zip.*;
import java.lang.reflect.*;
// import Assert;

/** Utility functions that do not belong elsewhere in the plume package. */
public final class UtilMDE {
  private UtilMDE() { throw new Error("do not instantiate"); }

  private static final String lineSep = System.getProperty("line.separator");

  ///////////////////////////////////////////////////////////////////////////
  /// Array
  ///

  // For arrays, see ArraysMDE.java.

  ///////////////////////////////////////////////////////////////////////////
  /// BitSet
  ///

  /**
   * Returns true if the cardinality of the intersection of the two
   * BitSets is at least the given value.
   **/
  public static boolean intersectionCardinalityAtLeast(BitSet a, BitSet b, int i) {
    // Here are three implementation strategies to determine the
    // cardinality of the intersection:
    // 1. a.clone().and(b).cardinality()
    // 2. do the above, but copy only a subset of the bits initially -- enough
    //    that it should exceed the given number -- and if that fails, do the
    //    whole thing.  Unfortunately, bits.get(int, int) isn't optimized
    //    for the case where the indices line up, so I'm not sure at what
    //    point this approach begins to dominate #1.
    // 3. iterate through both sets with nextSetBit()

    int size = Math.min(a.length(), b.length());
    if (size > 10*i) {
      // The size is more than 10 times the limit.  So first try processing
      // just a subset of the bits (4 times the limit).
      BitSet intersection = a.get(0, 4*i);
      intersection.and(b);
      if (intersection.cardinality() >= i) {
        return true;
      }
    }
    return (intersectionCardinality(a, b) >= i);
  }

  /**
   * Returns true if the cardinality of the intersection of the three
   * BitSets is at least the given value.
   **/
  public static boolean intersectionCardinalityAtLeast(BitSet a, BitSet b, BitSet c, int i) {
    // See comments in intersectionCardinalityAtLeast(BitSet, BitSet, int).
    // This is a copy of that.

    int size = Math.min(a.length(), b.length());
    size = Math.min(size, c.length());
    if (size > 10*i) {
      // The size is more than 10 times the limit.  So first try processing
      // just a subset of the bits (4 times the limit).
      BitSet intersection = a.get(0, 4*i);
      intersection.and(b);
      intersection.and(c);
      if (intersection.cardinality() >= i) {
        return true;
      }
    }
    return (intersectionCardinality(a, b, c) >= i);
  }

  /** Returns the cardinality of the intersection of the two BitSets. **/
  public static int intersectionCardinality(BitSet a, BitSet b) {
    BitSet intersection = (BitSet) a.clone();
    intersection.and(b);
    return intersection.cardinality();
  }

  /** Returns the cardinality of the intersection of the three BitSets. **/
  public static int intersectionCardinality(BitSet a, BitSet b, BitSet c) {
    BitSet intersection = (BitSet) a.clone();
    intersection.and(b);
    intersection.and(c);
    return intersection.cardinality();
  }


  ///////////////////////////////////////////////////////////////////////////
  /// BufferedFileReader
  ///


  // Convenience methods for creating InputStreams, Readers, BufferedReaders, and LineNumberReaders.

  /**
   * Returns an InputStream for the file, accounting for the possibility
   * that the file is compressed.
   * (A file whose name ends with ".gz" is treated as compressed.)
   * <p>
   * Warning: The "gzip" program writes and reads files containing
   * concatenated gzip files.  As of Java 1.4, Java reads
   * just the first one:  it silently discards all characters (including
   * gzipped files) after the first gzipped file.
   */
  public static InputStream fileInputStream(File file) throws IOException {
    InputStream in;
    if (file.getName().endsWith(".gz")) {
      try {
        in = new GZIPInputStream(new FileInputStream(file));
      } catch (IOException e) {
        throw new IOException("Problem while reading " + file, e);
      }
    } else {
      in = new FileInputStream(file);
    }
    return in;
  }

  /**
   * Returns a Reader for the file, accounting for the possibility
   * that the file is compressed.
   * (A file whose name ends with ".gz" is treated as compressed.)
   * <p>
   * Warning: The "gzip" program writes and reads files containing
   * concatenated gzip files.  As of Java 1.4, Java reads
   * just the first one:  it silently discards all characters (including
   * gzipped files) after the first gzipped file.
   **/
  public static InputStreamReader fileReader(String filename) throws FileNotFoundException, IOException {
    // return fileReader(filename, "ISO-8859-1");
    return fileReader(new File(filename), null);
  }

  /**
   * Returns a Reader for the file, accounting for the possibility
   * that the file is compressed.
   * (A file whose name ends with ".gz" is treated as compressed.)
   * <p>
   * Warning: The "gzip" program writes and reads files containing
   * concatenated gzip files.  As of Java 1.4, Java reads
   * just the first one:  it silently discards all characters (including
   * gzipped files) after the first gzipped file.
   **/
  public static InputStreamReader fileReader(File file) throws FileNotFoundException, IOException {
    return fileReader(file, null);
  }


  /**
   * Returns a Reader for the file, accounting for the possibility
   * that the file is compressed.
   * (A file whose name ends with ".gz" is treated as compressed.)
   * @param charsetName may be null, or the name of a Charset
   * <p>
   * Warning: The "gzip" program writes and reads files containing
   * concatenated gzip files.  As of Java 1.4, Java reads
   * just the first one:  it silently discards all characters (including
   * gzipped files) after the first gzipped file.
   **/
  public static InputStreamReader fileReader(File file, /*@Nullable*/ String charsetName) throws FileNotFoundException, IOException {
    InputStream in = new FileInputStream(file);
    InputStreamReader file_reader;
    if (charsetName == null) {
      file_reader = new InputStreamReader(in);
    } else {
      file_reader = new InputStreamReader(in, charsetName);
    }
    return file_reader;
  }

  /**
   * Returns a BufferedReader for the file, accounting for the possibility
   * that the file is compressed.
   * (A file whose name ends with ".gz" is treated as compressed.)
   * <p>
   * Warning: The "gzip" program writes and reads files containing
   * concatenated gzip files.  As of Java 1.4, Java reads
   * just the first one:  it silently discards all characters (including
   * gzipped files) after the first gzipped file.
   **/
  public static BufferedReader bufferedFileReader(String filename) throws FileNotFoundException, IOException {
    return bufferedFileReader(new File(filename));
  }

  /**
   * Returns a BufferedReader for the file, accounting for the possibility
   * that the file is compressed.
   * (A file whose name ends with ".gz" is treated as compressed.)
   * <p>
   * Warning: The "gzip" program writes and reads files containing
   * concatenated gzip files.  As of Java 1.4, Java reads
   * just the first one:  it silently discards all characters (including
   * gzipped files) after the first gzipped file.
   **/
  public static BufferedReader bufferedFileReader(File file) throws FileNotFoundException, IOException {
    return(bufferedFileReader(file, null));
  }

  /**
   * Returns a BufferedReader for the file, accounting for the possibility
   * that the file is compressed.
   * (A file whose name ends with ".gz" is treated as compressed.)
   * <p>
   * Warning: The "gzip" program writes and reads files containing
   * concatenated gzip files.  As of Java 1.4, Java reads
   * just the first one:  it silently discards all characters (including
   * gzipped files) after the first gzipped file.
   **/
  public static BufferedReader bufferedFileReader(String filename, /*@Nullable*/ String charsetName) throws FileNotFoundException, IOException {
    return bufferedFileReader(new File(filename), charsetName);
  }

  /**
   * Returns a BufferedReader for the file, accounting for the possibility
   * that the file is compressed.
   * (A file whose name ends with ".gz" is treated as compressed.)
   * <p>
   * Warning: The "gzip" program writes and reads files containing
   * concatenated gzip files.  As of Java 1.4, Java reads
   * just the first one:  it silently discards all characters (including
   * gzipped files) after the first gzipped file.
   **/
  public static BufferedReader bufferedFileReader(File file, /*@Nullable*/ String charsetName) throws FileNotFoundException, IOException {
    Reader file_reader = fileReader(file, charsetName);
    return new BufferedReader(file_reader);
  }


  /**
   * Returns a LineNumberReader for the file, accounting for the possibility
   * that the file is compressed.
   * (A file whose name ends with ".gz" is treated as compressed.)
   * <p>
   * Warning: The "gzip" program writes and reads files containing
   * concatenated gzip files.  As of Java 1.4, Java reads
   * just the first one:  it silently discards all characters (including
   * gzipped files) after the first gzipped file.
   **/
  public static LineNumberReader lineNumberFileReader(String filename) throws FileNotFoundException, IOException {
    return lineNumberFileReader(new File(filename));
  }

  /**
   * Returns a LineNumberReader for the file, accounting for the possibility
   * that the file is compressed.
   * (A file whose name ends with ".gz" is treated as compressed.)
   * <p>
   * Warning: The "gzip" program writes and reads files containing
   * concatenated gzip files.  As of Java 1.4, Java reads
   * just the first one:  it silently discards all characters (including
   * gzipped files) after the first gzipped file.
   **/
  public static LineNumberReader lineNumberFileReader(File file) throws FileNotFoundException, IOException {
    Reader file_reader;
    if (file.getName().endsWith(".gz")) {
      try {
        file_reader = new InputStreamReader(new GZIPInputStream(new FileInputStream(file)),
                                            "ISO-8859-1");
      } catch (IOException e) {
        throw new IOException("Problem while reading " + file, e);
      }
    } else {
      file_reader = new InputStreamReader(new FileInputStream(file),
                                          "ISO-8859-1");
    }
    return new LineNumberReader(file_reader);
  }

  /**
   * Returns a BufferedWriter for the file, accounting for the possibility
   * that the file is compressed.
   * (A file whose name ends with ".gz" is treated as compressed.)
   * <p>
   * Warning: The "gzip" program writes and reads files containing
   * concatenated gzip files.  As of Java 1.4, Java reads
   * just the first one:  it silently discards all characters (including
   * gzipped files) after the first gzipped file.
   **/
  public static BufferedWriter bufferedFileWriter(String filename) throws IOException {
    return bufferedFileWriter (filename, false);
  }

  /**
   * Returns a BufferedWriter for the file, accounting for the possibility
   * that the file is compressed.
   * (A file whose name ends with ".gz" is treated as compressed.)
   * <p>
   * Warning: The "gzip" program writes and reads files containing
   * concatenated gzip files.  As of Java 1.4, Java reads
   * just the first one:  it silently discards all characters (including
   * gzipped files) after the first gzipped file.
   * @param append if true, the resulting BufferedWriter appends to the end
   * of the file instead of the beginning.
   **/
  // Question:  should this be rewritten as a wrapper around bufferedFileOutputStream?
  public static BufferedWriter bufferedFileWriter(String filename, boolean append) throws IOException {
    Writer file_writer;
    if (filename.endsWith(".gz")) {
      file_writer = new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(filename, append)));
    } else {
      file_writer = new FileWriter(filename, append);
    }
    return new BufferedWriter(file_writer);
  }

  /**
   * Returns a BufferedOutputStream for the file, accounting for the possibility
   * that the file is compressed.
   * (A file whose name ends with ".gz" is treated as compressed.)
   * <p>
   * Warning: The "gzip" program writes and reads files containing
   * concatenated gzip files.  As of Java 1.4, Java reads
   * just the first one:  it silently discards all characters (including
   * gzipped files) after the first gzipped file.
   * @param append if true, the resulting BufferedOutputStream appends to the end
   * of the file instead of the beginning.
   **/
  public static BufferedOutputStream bufferedFileOutputStream(String filename, boolean append) throws IOException {
    OutputStream os = new FileOutputStream(filename, append);
    if (filename.endsWith(".gz")) {
      os = new GZIPOutputStream(os);
    }
    return new BufferedOutputStream(os);
  }


  /** @deprecated use bufferedFileReader (note lowercase first letter) */
  @Deprecated // since June 2005
  public static BufferedReader BufferedFileReader(String filename) throws FileNotFoundException, IOException {
    return bufferedFileReader(filename);
  }
  /** @deprecated use lineNumberFileReader (note lowercase first letter) */
  @Deprecated // since June 2005
  public static LineNumberReader LineNumberFileReader(String filename) throws FileNotFoundException, IOException {
    return lineNumberFileReader(filename);
  }
  /** @deprecated use lineNumberFileReader (note lowercase first letter) */
  @Deprecated // since June 2005
  public static LineNumberReader LineNumberFileReader(File file) throws FileNotFoundException, IOException {
    return lineNumberFileReader(file);
  }
  /** @deprecated use bufferedFileWriter (note lowercase first letter) */
  @Deprecated // since June 2005
  public static BufferedWriter BufferedFileWriter(String filename) throws IOException {
    return bufferedFileWriter(filename);
  }
  /** @deprecated use bufferedFileWriter (note lowercase first letter) */
  @Deprecated // since June 2005
  public static BufferedWriter BufferedFileWriter(String filename, boolean append) throws IOException {
    return bufferedFileWriter(filename, append);
  }


  ///////////////////////////////////////////////////////////////////////////
  /// Class
  ///

  /**
   * Returns true iff sub is a subtype of sup.
   * If sub == sup, then sub is considered a subtype of sub and this method
   * returns true.
   */
  public static boolean isSubtype(Class<?> sub, Class<?> sup) {
    if (sub == sup) {
      return true;
    }

    // Handle superclasses
    Class<?> parent = sub.getSuperclass();
    // If parent == null, sub == Object
    if ((parent != null)
        && (parent == sup || isSubtype(parent, sup))) {
      return true;
    }
       
    // Handle interfaces
    for (Class<?> ifc : sub.getInterfaces()) {
      if (ifc == sup || isSubtype(ifc, sup)) {
        return true;
      }
    }

    return false;
  }


  private static HashMap<String,Class<?>> primitiveClasses = new HashMap<String,Class<?>>(8);
  static {
    primitiveClasses.put("boolean", Boolean.TYPE);
    primitiveClasses.put("byte", Byte.TYPE);
    primitiveClasses.put("char", Character.TYPE);
    primitiveClasses.put("double", Double.TYPE);
    primitiveClasses.put("float", Float.TYPE);
    primitiveClasses.put("int", Integer.TYPE);
    primitiveClasses.put("long", Long.TYPE);
    primitiveClasses.put("short", Short.TYPE);
  }

  /**
   * Like {@link Class#forName(String)}, but also works when the string
   * represents a primitive type or a fully-qualified name (as opposed to a binary name).
   * <p>
   * If the given name can't be found, this method changes the last '.'  to
   * a dollar sign ($) and tries again.  This accounts for inner classes
   * that are incorrectly passed in in fully-qualified format instead of
   * binary format.
   * <p>
   * Recall the rather odd specification for {@link Class#forName(String)}:
   * the argument is a binary name for non-arrays, but a field descriptor
   * for arrays.  This method uses the same rules, but additionally handles
   * primitive types and, for non-arrays, fully-qualified names.
   **/
  // The annotation encourages proper use, even though this can take a
  // fully-qualified name (only for a non-array).
  public static Class<?> classForName(/*@ClassGetName*/ String className) throws ClassNotFoundException {
    Class<?> result = primitiveClasses.get(className);
    if (result != null) {
      return result;
    } else {
      try {
        return Class.forName(className);
      } catch (ClassNotFoundException e) {
        int pos = className.lastIndexOf('.');
        if (pos < 0) {
          throw e;
        }
        @SuppressWarnings("signature") // checked below & exception is handled
        /*@ClassGetName*/ String inner_name = className.substring (0, pos) + "$"
          + className.substring (pos+1);
        try {
          return Class.forName (inner_name);
        } catch (ClassNotFoundException ee) {
          throw e;
        }
      }
    }
  }

  private static HashMap</*@SourceNameForNonArray*/ String,/*@FieldDescriptor*/ String> primitiveClassesJvm = new HashMap</*@SourceNameForNonArray*/ String,/*@FieldDescriptor*/ String>(8);
  static {
    primitiveClassesJvm.put("boolean", "Z");
    primitiveClassesJvm.put("byte", "B");
    primitiveClassesJvm.put("char", "C");
    primitiveClassesJvm.put("double", "D");
    primitiveClassesJvm.put("float", "F");
    primitiveClassesJvm.put("int", "I");
    primitiveClassesJvm.put("long", "J");
    primitiveClassesJvm.put("short", "S");
  }

  /**
   * Convert a binary class name to a field descriptor.
   * For example, convert "java.lang.Object[]" to "[Ljava/lang/Object;".
   * @deprecated use binaryNameToFieldDescriptor
   **/
  @Deprecated
  public static String classnameToJvm(/*@BinaryName*/ String classname) {
    return binaryNameToFieldDescriptor(classname);
  }

  /**
   * Convert a binary name to a field descriptor.
   * For example, convert "java.lang.Object[]" to "[Ljava/lang/Object;"
   * or "int" to "I".
   **/
  @SuppressWarnings("signature") // conversion routine
  public static /*@FieldDescriptor*/ String binaryNameToFieldDescriptor(/*@BinaryName*/ String classname) {
    int dims = 0;
    String sans_array = classname;
    while (sans_array.endsWith("[]")) {
      dims++;
      sans_array = sans_array.substring(0, sans_array.length()-2);
    }
    String result = primitiveClassesJvm.get(sans_array);
    if (result == null) {
      result = "L" + sans_array + ";";
    }
    for (int i=0; i<dims; i++) {
      result = "[" + result;
    }
    return result.replace('.', '/');
  }

  /**
   * Convert a primitive java type name (e.g., "int", "double", etc.) to
   * a field descriptor (e.g., "I", "D", etc.).
   * @deprecated use primitiveTypeNameToFieldDescriptor
   * @throws IllegalArgumentException if primitive_name is not a valid primitive type name.
   */
  @Deprecated
  public static String primitive_name_to_jvm (String primitive_name) {
    return primitiveTypeNameToFieldDescriptor(primitive_name);
  }

  /**
   * Convert a primitive java type name (e.g., "int", "double", etc.) to
   * a field descriptor (e.g., "I", "D", etc.).
   * @throws IllegalArgumentException if primitive_name is not a valid primitive type name.
   */
  public static /*@FieldDescriptor*/ String primitiveTypeNameToFieldDescriptor (String primitive_name) {
    String result = primitiveClassesJvm.get (primitive_name);
    if (result == null) {
      throw new IllegalArgumentException("Not the name of a primitive type: " + primitive_name);
    }
    return result;
  }

  /** Convert from a BinaryName to the format of {@link Class#getName()}. */
  @SuppressWarnings("signature") // conversion routine
  public static /*@ClassGetName*/ String binaryNameToClassGetName(/*BinaryName*/ String bn) {
    if (bn.endsWith("[]")) {
      return binaryNameToFieldDescriptor(bn).replace('/', '.');
    } else {
      return bn;
    }
  }

  /** Convert from a FieldDescriptor to the format of {@link Class#getName()}. */
  @SuppressWarnings("signature") // conversion routine
  public static /*@ClassGetName*/ String fieldDescriptorToClassGetName(/*FieldDescriptor*/ String fd) {
    if (fd.startsWith("[")) {
      return fd.replace('/', '.');
    } else {
      return fieldDescriptorToBinaryName(fd);
    }
  }


  /**
   * Convert a fully-qualified argument list from Java format to JVML format.
   * For example, convert "(java.lang.Integer[], int, java.lang.Integer[][])"
   * to "([Ljava/lang/Integer;I[[Ljava/lang/Integer;)".
   **/
  public static String arglistToJvm(String arglist) {
    if (! (arglist.startsWith("(") && arglist.endsWith(")"))) {
      throw new Error("Malformed arglist: " + arglist);
    }
    String result = "(";
    String comma_sep_args = arglist.substring(1, arglist.length()-1);
    StringTokenizer args_tokenizer
      = new StringTokenizer(comma_sep_args, ",", false);
    for ( ; args_tokenizer.hasMoreTokens(); ) {
      @SuppressWarnings("signature") // substring 
      /*@BinaryName*/ String arg = args_tokenizer.nextToken().trim();
      result += binaryNameToFieldDescriptor(arg);
    }
    result += ")";
    // System.out.println("arglistToJvm: " + arglist + " => " + result);
    return result;
  }

  private static HashMap<String,String> primitiveClassesFromJvm = new HashMap<String,String>(8);
  static {
    primitiveClassesFromJvm.put("Z", "boolean");
    primitiveClassesFromJvm.put("B", "byte");
    primitiveClassesFromJvm.put("C", "char");
    primitiveClassesFromJvm.put("D", "double");
    primitiveClassesFromJvm.put("F", "float");
    primitiveClassesFromJvm.put("I", "int");
    primitiveClassesFromJvm.put("J", "long");
    primitiveClassesFromJvm.put("S", "short");
  }

  /**
   * Convert a classname from JVML format to Java format.
   * For example, convert "[Ljava/lang/Object;" to "java.lang.Object[]"
   * or "I" to "int".
   * @deprecated use fieldDescriptorToBinaryName
   **/
  @Deprecated
  public static /*@BinaryName*/ String classnameFromJvm(String classname) {
    return fieldDescriptorToBinaryName(classname);
  }

  // does not convert "V" to "void".  Should it?
  /**
   * Convert a field descriptor to a binary name.
   * For example, convert "[Ljava/lang/Object;" to "java.lang.Object[]"
   * or "I" to "int".
   **/
  @SuppressWarnings("signature") // conversion routine
  public static /*@BinaryName*/ String fieldDescriptorToBinaryName(String classname) {
    if (classname.equals("")) {
      throw new Error("Empty string passed to fieldDescriptorToBinaryName");
    }
    int dims = 0;
    while (classname.startsWith("[")) {
      dims++;
      classname = classname.substring(1);
    }
    String result;
    if (classname.startsWith("L") && classname.endsWith(";")) {
      result = classname.substring(1, classname.length() - 1);
    } else {
      result = primitiveClassesFromJvm.get(classname);
      if (result == null) {
        throw new Error("Malformed base class: " + classname);
      }
    }
    for (int i=0; i<dims; i++) {
      result += "[]";
    }
    return result.replace('/', '.');
  }

  /**
   * Convert an argument list from JVML format to Java format.
   * For example, convert "([Ljava/lang/Integer;I[[Ljava/lang/Integer;)"
   * to "(java.lang.Integer[], int, java.lang.Integer[][])".
   **/
  public static String arglistFromJvm(String arglist) {
    if (! (arglist.startsWith("(") && arglist.endsWith(")"))) {
      throw new Error("Malformed arglist: " + arglist);
    }
    String result = "(";
    int pos = 1;
    while (pos < arglist.length()-1) {
      if (pos > 1)
        result += ", ";
      int nonarray_pos = pos;
      while (arglist.charAt(nonarray_pos) == '[') {
        nonarray_pos++;
      }
      char c = arglist.charAt(nonarray_pos);
      if (c == 'L') {
        int semi_pos = arglist.indexOf(";", nonarray_pos);
        result += fieldDescriptorToBinaryName(arglist.substring(pos, semi_pos+1));
        pos = semi_pos + 1;
      } else {
        String maybe = fieldDescriptorToBinaryName(arglist.substring(pos, nonarray_pos+1));
        if (maybe == null) {
          // return null;
          throw new Error("Malformed arglist: " + arglist);
        }
        result += maybe;
        pos = nonarray_pos+1;
      }
    }
    return result + ")";
  }


  ///////////////////////////////////////////////////////////////////////////
  /// ClassLoader
  ///

  /**
   * This static nested class has no purpose but to define loadClassFromFile.
   * ClassLoader.defineClass is protected, so I subclass ClassLoader in
   * order to call defineClass.
   **/
  private static class PromiscuousLoader extends ClassLoader {
    /** Load a class from a .class file, and return it. */
    public Class<?> loadClassFromFile(/*@BinaryName*/ String className, String pathname) throws FileNotFoundException, IOException {
      FileInputStream fi = new FileInputStream(pathname);
      int numbytes = fi.available();
      byte[] classBytes = new byte[numbytes];
      fi.read(classBytes);
      fi.close();
      Class<?> return_class = defineClass(className, classBytes, 0, numbytes);
      resolveClass(return_class);
      return return_class;
    }
  }

  private static PromiscuousLoader thePromiscuousLoader = new PromiscuousLoader();

  /**
   * @param pathname the pathname of a .class file
   * @return a Java Object corresponding to the Class defined in the .class file
   **/
  public static Class<?> loadClassFromFile(/*@BinaryName*/ String className, String pathname) throws FileNotFoundException, IOException {
    return thePromiscuousLoader.loadClassFromFile(className, pathname);
  }


  ///////////////////////////////////////////////////////////////////////////
  /// Classpath
  ///

  // Perhaps abstract out the simpler addToPath from this
  /** Add the directory to the system classpath. */
  public static void addToClasspath(String dir) {
    // If the dir isn't on CLASSPATH, add it.
    String pathSep = System.getProperty("path.separator");
    // what is the point of the "replace()" call?
    String cp = System.getProperty("java.class.path",".").replace('\\', '/');
    StringTokenizer tokenizer = new StringTokenizer(cp, pathSep, false);
    boolean found = false;
    while (tokenizer.hasMoreTokens() && !found) {
      found = tokenizer.nextToken().equals(dir);
    }
    if (!found) {
      System.setProperty("java.class.path", dir + pathSep + cp);
    }
  }


  ///////////////////////////////////////////////////////////////////////////
  /// File
  ///


  /** Count the number of lines in the specified file **/
  public static long count_lines(String filename) throws IOException {
    LineNumberReader reader = UtilMDE.lineNumberFileReader(filename);
    long count = 0;
    while (reader.readLine() != null)
      count++;
    return count;
  }

  /** Tries to infer the line separator used in a file. **/
  public static String inferLineSeparator(String filename) throws IOException {
    return inferLineSeparator(new File(filename));
  }

  /** Tries to infer the line separator used in a file. **/
  public static String inferLineSeparator(File filename) throws IOException {
    BufferedReader r = UtilMDE.bufferedFileReader(filename);
    int unix = 0;
    int dos = 0;
    int mac = 0;
    while (true) {
      String s = r.readLine();
      if (s == null) {
        break;
      }
      if (s.endsWith("\r\n")) {
        dos++;
      } else if (s.endsWith("\r")) {
        mac++;
      } else if (s.endsWith("\n")) {
        unix++;
      } else {
        // This can happen only if the last line is not terminated.
      }
    }
    if ((dos > mac && dos > unix)
        || (lineSep.equals("\r\n") && dos >= unix && dos >= mac)) {
      return "\r\n";
    }
    if ((mac > dos && mac > unix)
        || (lineSep.equals("\r") && mac >= dos && mac >= unix)) {
      return "\r";
    }
    if ((unix > dos && unix > mac)
        || (lineSep.equals("\n") && unix >= dos && unix >= mac)) {
      return "\n";
    }
    // The two non-preferred line endings are tied and have more votes than
    // the preferred line ending.  Give up and return the line separator
    // for the system on which Java is currently running.
    return lineSep;
  }



  /**
   * Returns true iff files have the same contents.
   */
  public static boolean equalFiles(String file1, String file2) {
    return equalFiles(file1, file2, false);
  }

  /**
   * Returns true iff files have the same contents.
   * @param trimLines if true, call String.trim on each line before comparing
   */
  public static boolean equalFiles(String file1, String file2, boolean trimLines) {
    try {
      LineNumberReader reader1 = UtilMDE.lineNumberFileReader(file1);
      LineNumberReader reader2 = UtilMDE.lineNumberFileReader(file2);
      String line1 = reader1.readLine();
      String line2 = reader2.readLine();
      while (line1 != null && line2 != null) {
        if (trimLines) {
          line1 = line1.trim();
          line2 = line2.trim();
        }
        if (! (line1.equals(line2))) {
          return false;
        }
        line1 = reader1.readLine();
        line2 = reader2.readLine();
      }
      if (line1 == null && line2 == null) {
        return true;
      }
      return false;
    } catch (IOException e) {
        throw new RuntimeException(e);
      }
  }


  /**
   * Returns true
   *  if the file exists and is writable, or
   *  if the file can be created.
   **/
  public static boolean canCreateAndWrite(File file) {
    if (file.exists()) {
      return file.canWrite();
    } else {
      File directory = file.getParentFile();
      if (directory == null) {
        directory = new File(".");
      }
      // Does this test need "directory.canRead()" also?
      return directory.canWrite();
    }

    /// Old implementation; is this equivalent to the new one, above??
    // try {
    //   if (file.exists()) {
    //     return file.canWrite();
    //   } else {
    //     file.createNewFile();
    //     file.delete();
    //     return true;
    //   }
    // } catch (IOException e) {
    //   return false;
    // }
  }


  ///
  /// Directories
  ///

  /**
   * Creates an empty directory in the default temporary-file directory,
   * using the given prefix and suffix to generate its name. For example
   * calling createTempDir("myPrefix", "mySuffix") will create the following
   * directory: temporaryFileDirectory/myUserName/myPrefix_someString_suffix.
   * someString is internally generated to ensure no temporary files of the
   * same name are generated.
   * @param prefix The prefix string to be used in generating the file's
   *  name; must be at least three characters long
   * @param suffix The suffix string to be used in generating the file's
   *  name; may be null, in which case the suffix ".tmp" will be used Returns:
   *  An abstract pathname denoting a newly-created empty file
   * @throws IllegalArgumentException If the prefix argument contains fewer
   *  than three characters
   * @throws IOException If a file could not be created
   * @throws SecurityException If a security manager exists and its
   *  SecurityManager.checkWrite(java.lang.String) method does not allow a
   *  file to be created
   * @see java.io.File#createTempFile(String, String, File)
   **/
  public static File createTempDir(String prefix, String suffix)
    throws IOException {
    String fs = File.separator;
    String path = System.getProperty("java.io.tmpdir") + fs +
      System.getProperty("user.name") + fs;
    File pathFile =  new File(path);
    pathFile.mkdirs();
    File tmpfile = File.createTempFile(prefix + "_", "_", pathFile);
    String tmpDirPath = tmpfile.getPath() + suffix;
    tmpfile.delete();
    File tmpDir = new File(tmpDirPath);
    tmpDir.mkdirs();
    return tmpDir;
  }


  /**
   * Deletes the directory at dirName and all its files.
   * Fails if dirName has any subdirectories.
   */
  public static void deleteDir(String dirName) {
    deleteDir(new File(dirName));
  }

  /**
   * Deletes the directory at dirName and all its files.
   * Fails if dirName has any subdirectories.
   */
  public static void deleteDir(File dir) {
    File[] files = dir.listFiles();
    if (files == null) {
      return;
    }
    for (int i = 0; i < files.length; i++) {
      files[i].delete();
    }
    dir.delete();
  }


  ///
  /// File names (aka filenames)
  ///

  // Someone must have already written this.  Right?

  /**
   * A FilenameFilter that accepts files whose name matches the given wildcard.
   * The wildcard may contain exactly one "*".
   */
  public static final class WildcardFilter implements FilenameFilter {
    String prefix;
    String suffix;
    public WildcardFilter(String filename) {
      int astloc = filename.indexOf("*");
      if (astloc == -1)
        throw new Error("No asterisk in wildcard argument: " + filename);
      prefix = filename.substring(0, astloc);
      suffix = filename.substring(astloc+1);
      if (filename.indexOf("*") != -1)
        throw new Error("Multiple asterisks in wildcard argument: " + filename);
    }
    public boolean accept(File dir, String name) {
      return name.startsWith(prefix) && name.endsWith(suffix);
    }
  }

  static final String userHome = System.getProperty ("user.home");

  /**
   * Does tilde expansion on a file name (to the user's home directory).
   */
  public static File expandFilename (File name) {
    String path = name.getPath();
    String newname = expandFilename (path);
    @SuppressWarnings("interning")
    boolean changed = (newname != path);
    if (changed)
      return new File (newname);
    else
      return name;
  }

  /**
   * Does tilde expansion on a file name (to the user's home directory).
   */
  public static String expandFilename (String name) {
    if (name.contains ("~"))
      return (name.replace ("~", userHome));
    else
      return name;
  }


  /**
   * Returns a string version of the name that can be used in Java source.
   * On Windows, the file will return a backslash separated string.  Since
   * backslash is an escape character, it must be quoted itself inside
   * the string.
   *
   * The current implementation presumes that backslashes don't appear
   * in filenames except as windows path separators.  That seems like a
   * reasonable assumption.
   */
  public static String java_source (File name) {

    return name.getPath().replace ("\\", "\\\\");
  }

  ///
  /// Reading and writing
  ///

  /**
   * Writes an Object to a File.
   **/
  public static void writeObject(Object o, File file) throws IOException {
    // 8192 is the buffer size in BufferedReader
    OutputStream bytes =
      new BufferedOutputStream(new FileOutputStream(file), 8192);
    if (file.getName().endsWith(".gz")) {
      bytes = new GZIPOutputStream(bytes);
    }
    ObjectOutputStream objs = new ObjectOutputStream(bytes);
    objs.writeObject(o);
    objs.close();
  }


  /**
   * Reads an Object from a File.
   **/
  public static Object readObject(File file) throws
  IOException, ClassNotFoundException {
    // 8192 is the buffer size in BufferedReader
    InputStream istream =
      new BufferedInputStream(new FileInputStream(file), 8192);
    if (file.getName().endsWith(".gz")) {
      try {
        istream = new GZIPInputStream(istream);
      } catch (IOException e) {
        throw new IOException("Problem while reading " + file, e);
      }
    }
    ObjectInputStream objs = new ObjectInputStream(istream);
    return objs.readObject();
  }

  /**
   * Reads the entire contents of the reader and returns it as a string.
   * Any IOException encountered will be turned into an Error.
   */
  public static String readerContents(Reader r) {
    try {
      StringBuilder contents = new StringBuilder();
      int ch;
      while ((ch = r.read()) != -1) {
        contents.append((char) ch);
      }
      r.close();
      return contents.toString();
    } catch (Exception e) {
      throw new Error ("Unexpected error in readerContents(" + r + ")", e);
    }
  }

  // an alternate name would be "fileContents".
  /**
   * Reads the entire contents of the file and returns it as a string.
   * Any IOException encountered will be turned into an Error.
   */
  public static String readFile (File file) {

    try {
      BufferedReader reader = UtilMDE.bufferedFileReader (file);
      StringBuilder contents = new StringBuilder();
      String line = reader.readLine();
      while (line != null) {
        contents.append (line);
        // Note that this converts line terminators!
        contents.append (lineSep);
        line = reader.readLine();
      }
      reader.close();
      return contents.toString();
    } catch (Exception e) {
      throw new Error ("Unexpected error in readFile(" + file + ")", e);
    }
  }

  /**
   * Creates a file with the given name and writes the specified string
   * to it.  If the file currently exists (and is writable) it is overwritten
   * Any IOException encountered will be turned into an Error.
   */
  public static void writeFile (File file, String contents) {

    try {
      FileWriter writer = new FileWriter (file);
      writer.write (contents, 0, contents.length());
      writer.close();
    } catch (Exception e) {
      throw new Error ("Unexpected error in writeFile(" + file + ")", e);
    }
  }


  ///////////////////////////////////////////////////////////////////////////
  /// Hashing
  ///

  // In hashing, there are two separate issues.  First, one must convert
  // the input datum into an integer.  Then, one must transform the
  // resulting integer in a pseudorandom way so as to result in a number
  // that is far separated from other values that may have been near it to
  // begin with.  Often these two steps are combined, particularly if
  // one wishes to avoid creating too large an integer (losing information
  // off the top bits).

  // http://burtleburtle.net/bob/hash/hashfaq.html says (of combined methods):
  //  * for (h=0, i=0; i<len; ++i) { h += key[i]; h += (h<<10); h ^= (h>>6); }
  //    h += (h<<3); h ^= (h>>11); h += (h<<15);
  //    is good.
  //  * for (h=0, i=0; i<len; ++i) h = tab[(h^key[i])&0xff]; may be good.
  //  * for (h=0, i=0; i<len; ++i) h = (h>>8)^tab[(key[i]+h)&0xff]; may be good.

  // In this part of the file, perhaps I will eventually write good hash
  // functions.  For now, write cheesy ones that primarily deal with the
  // first issue, transforming a data structure into a single number.  This
  // is also known as fingerprinting.

  /**
   * Return a hash of the arguments.
   * Note that this differs from the result of {@link Double#hashCode()}.
   */
  public static final int hash(double x) {
    return hash(Double.doubleToLongBits(x));
  }

  /** Return a hash of the arguments. */
  public static final int hash(double a, double b) {
    double result = 17;
    result = result * 37 + a;
    result = result * 37 + b;
    return hash(result);
  }

  /** Return a hash of the arguments. */
  public static final int hash(double a, double b, double c) {
    double result = 17;
    result = result * 37 + a;
    result = result * 37 + b;
    result = result * 37 + c;
    return hash(result);
  }

  /** Return a hash of the arguments. */
  public static final int hash(double /*@Nullable*/ [] a) {
    double result = 17;
    if (a != null) {
      result = result * 37 + a.length;
      for (int i = 0; i < a.length; i++) {
        result = result * 37 + a[i];
      }
    }
    return hash(result);
  }

  /** Return a hash of the arguments. */
  public static final int hash(double /*@Nullable*/ [] a, double /*@Nullable*/ [] b) {
    return hash(hash(a), hash(b));
  }


  /// Don't define hash with int args; use the long versions instead.

  /**
   * Return a hash of the arguments.
   * Note that this differs from the result of {@link Long#hashCode()}.
   * But it doesn't map -1 and 0 to the same value.
   */
  public static final int hash(long l) {
    // If possible, use the value itself.
    if (l >= Integer.MIN_VALUE && l <= Integer.MAX_VALUE) {
      return (int) l;
    }

    int result = 17;
    int hibits = (int) (l >> 32);
    int lobits = (int) l;
    result = result * 37 + hibits;
    result = result * 37 + lobits;
    return result;
  }

  /** Return a hash of the arguments. */
  public static final int hash(long a, long b) {
    long result = 17;
    result = result * 37 + a;
    result = result * 37 + b;
    return hash(result);
  }

  /** Return a hash of the arguments. */
  public static final int hash(long a, long b, long c) {
    long result = 17;
    result = result * 37 + a;
    result = result * 37 + b;
    result = result * 37 + c;
    return hash(result);
  }

  /** Return a hash of the arguments. */
  public static final int hash(long /*@Nullable*/ [] a) {
    long result = 17;
    if (a != null) {
      result = result * 37 + a.length;
      for (int i = 0; i < a.length; i++) {
        result = result * 37 + a[i];
      }
    }
    return hash(result);
  }

  /** Return a hash of the arguments. */
  public static final int hash(long /*@Nullable*/ [] a, long /*@Nullable*/ [] b) {
    return hash(hash(a), hash(b));
  }

  /** Return a hash of the arguments. */
  public static final int hash(/*@Nullable*/ String a) {
    return (a == null) ? 0 : a.hashCode();
  }

  /** Return a hash of the arguments. */
  public static final int hash(/*@Nullable*/ String a, /*@Nullable*/ String b) {
    long result = 17;
    result = result * 37 + hash(a);
    result = result * 37 + hash(b);
    return hash(result);
  }

  /** Return a hash of the arguments. */
  public static final int hash(/*@Nullable*/ String a, /*@Nullable*/ String b, /*@Nullable*/ String c) {
    long result = 17;
    result = result * 37 + hash(a);
    result = result * 37 + hash(b);
    result = result * 37 + hash(c);
    return hash(result);
  }

  /** Return a hash of the arguments. */
  public static final int hash(/*@Nullable*/ String /*@Nullable*/ [] a) {
    long result = 17;
    if (a != null) {
      result = result * 37 + a.length;
      for (int i = 0; i < a.length; i++) {
        result = result * 37 + hash(a[i]);
      }
    }
    return hash(result);
  }


  ///////////////////////////////////////////////////////////////////////////
  /// Iterator
  ///

  // Making these classes into functions didn't work because I couldn't get
  // their arguments into a scope that Java was happy with.

  /** Converts an Enumeration into an Iterator. */
  public static final class EnumerationIterator<T> implements Iterator<T> {
    Enumeration<T> e;
    public EnumerationIterator(Enumeration<T> e) { this.e = e; }
    public boolean hasNext() { return e.hasMoreElements(); }
    public T next() { return e.nextElement(); }
    public void remove() { throw new UnsupportedOperationException(); }
  }

  /** Converts an Iterator into an Enumeration. */
  public static final class IteratorEnumeration<T> implements Enumeration<T> {
    Iterator<T> itor;
    public IteratorEnumeration(Iterator<T> itor) { this.itor = itor; }
    public boolean hasMoreElements() { return itor.hasNext(); }
    public T nextElement() { return itor.next(); }
  }

  // This must already be implemented someplace else.  Right??
  /**
   * An Iterator that returns first the elements returned by its first
   * argument, then the elements returned by its second argument.
   * Like MergedIterator, but specialized for the case of two arguments.
   **/
  public static final class MergedIterator2<T> implements Iterator<T> {
    Iterator<T> itor1, itor2;
    public MergedIterator2(Iterator<T> itor1_, Iterator<T> itor2_) {
      this.itor1 = itor1_; this.itor2 = itor2_;
    }
    public boolean hasNext() {
      return (itor1.hasNext() || itor2.hasNext());
    }
    public T next() {
      if (itor1.hasNext())
        return itor1.next();
      else if (itor2.hasNext())
        return itor2.next();
      else
        throw new NoSuchElementException();
    }
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  // This must already be implemented someplace else.  Right??
  /**
   * An Iterator that returns the elements in each of its argument
   * Iterators, in turn.  The argument is an Iterator of Iterators.
   * Like MergedIterator2, but generalized to arbitrary number of iterators.
   **/
  public static final class MergedIterator<T> implements Iterator<T> {
    Iterator<Iterator<T>> itorOfItors;
    public MergedIterator(Iterator<Iterator<T>> itorOfItors) { this.itorOfItors = itorOfItors; }

    // an empty iterator to prime the pump
    Iterator<T> current = new ArrayList<T>().iterator();

    public boolean hasNext() {
      while ((!current.hasNext()) && (itorOfItors.hasNext())) {
        current = itorOfItors.next();
      }
      return current.hasNext();
    }

    public T next() {
      hasNext();                // for side effect
      return current.next();
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  /** An iterator that only returns elements that match the given Filter. */
  public static final class FilteredIterator<T> implements Iterator<T> {
    Iterator<T> itor;
    Filter<T> filter;

    public FilteredIterator(Iterator<T> itor, Filter<T> filter) {
      this.itor = itor; this.filter = filter;
    }

    @SuppressWarnings("unchecked")
    T invalid_t = (T) new Object();

    T current = invalid_t;
    boolean current_valid = false;

    public boolean hasNext() {
      while ((!current_valid) && itor.hasNext()) {
        current = itor.next();
        current_valid = filter.accept(current);
      }
      return current_valid;
    }

    public T next() {
      if (hasNext()) {
        current_valid = false;
        @SuppressWarnings("interning")
        boolean ok = (current != invalid_t);
        assert ok;
        return current;
      } else {
        throw new NoSuchElementException();
      }
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  /**
   * Returns an iterator just like its argument, except that the first and
   * last elements are removed.  They can be accessed via the getFirst and
   * getLast methods.
   **/
  public static final class RemoveFirstAndLastIterator<T> implements Iterator<T> {
    Iterator<T> itor;
    // I don't think this works, because the iterator might itself return null
    // /*@Nullable*/ T nothing = (/*@Nullable*/ T) null;
    @SuppressWarnings("unchecked")
    T nothing = (T) new Object();
    T first = nothing;
    T current = nothing;

    public RemoveFirstAndLastIterator(Iterator<T> itor) {
      this.itor = itor;
      if (itor.hasNext()) {
        first = itor.next();
      }
      if (itor.hasNext()) {
        current = itor.next();
      }
    }

    public boolean hasNext() {
      return itor.hasNext();
    }

    public T next() {
      if (! itor.hasNext()) {
        throw new NoSuchElementException();
      }
      T tmp = current;
      current = itor.next();
      return tmp;
    }

    public T getFirst() {
      @SuppressWarnings("interning")
      boolean invalid = (first == nothing);
      if (invalid) {
        throw new NoSuchElementException();
      }
      return first;
    }

    // Throws an error unless the RemoveFirstAndLastIterator has already
    // been iterated all the way to its end (so the delegate is pointing to
    // the last element).  Also, this is buggy when the delegate is empty.
    public T getLast() {
      if (itor.hasNext()) {
        throw new Error();
      }
      return current;
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }
  }


  /**
   * Return an List containing num_elts randomly chosen
   * elements from the iterator, or all the elements of the iterator if
   * there are fewer.  It examines every element of the iterator, but does
   * not keep them all in memory.
   **/
  public static <T> List<T> randomElements(Iterator<T> itor, int num_elts) {
    return randomElements(itor, num_elts, r);
  }
  private static Random r = new Random();

  /**
   * Return an List containing num_elts randomly chosen
   * elements from the iterator, or all the elements of the iterator if
   * there are fewer.  It examines every element of the iterator, but does
   * not keep them all in memory.
   **/
  public static <T> List<T> randomElements(Iterator<T> itor, int num_elts, Random random) {
    // The elements are chosen with the following probabilities,
    // where n == num_elts:
    //   n n/2 n/3 n/4 n/5 ...

    RandomSelector<T> rs = new RandomSelector<T> (num_elts, random);

    while (itor.hasNext()) {
      rs.accept (itor.next());
    }
    return rs.getValues();


    /*
    ArrayList<T> result = new ArrayList<T>(num_elts);
    int i=1;
    for (int n=0; n<num_elts && itor.hasNext(); n++, i++) {
      result.add(itor.next());
    }
    for (; itor.hasNext(); i++) {
      T o = itor.next();
      // test random < num_elts/i
      if (random.nextDouble() * i < num_elts) {
        // This element will replace one of the existing elements.
        result.set(random.nextInt(num_elts), o);
      }
    }
    return result;
    */
  }


  ///////////////////////////////////////////////////////////////////////////
  /// Map
  ///

  // In Python, inlining this gave a 10x speed improvement.
  // Will the same be true for Java?
  /**
   * Increment the Integer which is indexed by key in the Map.
   * If the key isn't in the Map, it is added.
   * Throws an error if the key is in the Map but maps to a non-Integer.
   **/
  public static <T> /*@Nullable*/ Integer incrementMap(Map<T,Integer> m, T key, int count) {
    Integer old = m.get(key);
    int new_total;
    if (old == null) {
      new_total = count;
    } else {
      new_total = old.intValue() + count;
    }
    return m.put(key, new Integer(new_total));
  }

  /** Returns a multi-line string representation of a map. */
  public static <K,V> String mapToString(Map<K,V> m) {
    StringBuilder sb = new StringBuilder();
    mapToString(sb, m, "");
    return sb.toString();
  }

  /**
   * Write a multi-line representation of the map into the given Appendable
   * (e.g., a StringBuilder).
   */
  public static <K,V> void mapToString(Appendable sb, Map<K,V> m, String linePrefix) {
    try {
      for (Map.Entry<K, V> entry : m.entrySet()) {
        sb.append(linePrefix);
        sb.append(entry.getKey().toString());
        sb.append(" => ");
        sb.append(entry.getValue().toString());
        sb.append(lineSep);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /** Returns a sorted version of m.keySet(). */
  public static <K extends Comparable<? super K>,V> Collection</*@KeyFor("#1")*/ K> sortedKeySet(Map<K,V> m) {
    ArrayList</*@KeyFor("#1")*/ K> theKeys = new ArrayList</*@KeyFor("#1")*/ K> (m.keySet());
    Collections.sort (theKeys);
    return theKeys;
  }

  /** Returns a sorted version of m.keySet(). */
  public static <K,V> Collection</*@KeyFor("#1")*/ K> sortedKeySet(Map<K,V> m, Comparator<K> comparator) {
    ArrayList</*@KeyFor("#1")*/ K> theKeys = new ArrayList</*@KeyFor("#1")*/ K> (m.keySet());
    Collections.sort (theKeys, comparator);
    return theKeys;
  }


  ///////////////////////////////////////////////////////////////////////////
  /// Method
  ///

  /**
   * Maps from a comma-delimited string of arg types, such as appears in a
   * method signature, to an array of Class objects, one for each arg
   * type. Example keys include: "java.lang.String, java.lang.String,
   * java.lang.Class[]" and "int,int".
   */
  static HashMap<String,Class<?>[]> args_seen = new HashMap<String,Class<?>[]>();

  /**
   * Given a method signature, return the method.
   * Example calls are:
   * <pre>
   * UtilMDE.methodForName("plume.UtilMDE.methodForName(java.lang.String, java.lang.String, java.lang.Class[])")
   * UtilMDE.methodForName("plume.UtilMDE.methodForName(java.lang.String,java.lang.String,java.lang.Class[])")
   * UtilMDE.methodForName("java.lang.Math.min(int,int)")
   * </pre>
   */
  public static Method methodForName(String method)
    throws ClassNotFoundException, NoSuchMethodException, SecurityException {

    int oparenpos = method.indexOf('(');
    int dotpos = method.lastIndexOf('.', oparenpos);
    int cparenpos = method.indexOf(')', oparenpos);
    if ((dotpos == -1) || (oparenpos == -1) || (cparenpos == -1)) {
      throw new Error("malformed method name should contain a period, open paren, and close paren: " + method + " <<" + dotpos + "," + oparenpos + "," + cparenpos + ">>");
    }
    for (int i=cparenpos+1; i<method.length(); i++) {
      if (! Character.isWhitespace(method.charAt(i))) {
        throw new Error("malformed method name should contain only whitespace following close paren");
      }
    }

    @SuppressWarnings("signature") // throws exception if class does not exist
    /*@BinaryNameForNonArray*/ String classname = method.substring(0,dotpos);
    String methodname = method.substring(dotpos+1, oparenpos);
    String all_argnames = method.substring(oparenpos+1, cparenpos).trim();
    Class<?>[] argclasses = args_seen.get(all_argnames);
    if (argclasses == null) {
      String[] argnames;
      if (all_argnames.equals("")) {
        argnames = new String[0];
      } else {
        argnames = split(all_argnames, ',');
      }

      /*@LazyNonNull*/ Class<?>[] argclasses_tmp = new Class<?>[argnames.length];
      for (int i=0; i<argnames.length; i++) {
        String bnArgname = argnames[i].trim();
        /*@ClassGetName*/ String cgnArgname = binaryNameToClassGetName(bnArgname);
        argclasses_tmp[i] = classForName(cgnArgname);
      }
      @SuppressWarnings("cast")
      Class<?>[] argclasses_res = (/*@NonNull*/ Class<?>[]) argclasses_tmp;
      argclasses = argclasses_res;
      args_seen.put(all_argnames, argclasses_res);
    }
    return methodForName(classname, methodname, argclasses);
  }

  /** Given a class name and a method name in that class, return the method. */
  public static Method methodForName(/*@BinaryNameForNonArray*/ String classname, String methodname, Class<?>[] params)
    throws ClassNotFoundException, NoSuchMethodException, SecurityException {

    Class<?> c = Class.forName(classname);
    Method m = c.getDeclaredMethod(methodname, params);
    return m;
  }



  ///////////////////////////////////////////////////////////////////////////
  /// ProcessBuilder
  ///

  /**
   * Execute the given command, and return all its output as a string.
   */
  public static String backticks(String... command) {
    return backticks(Arrays.asList(command));
  }

  /**
   * Execute the given command, and return all its output as a string.
   */
  public static String backticks(List<String> command) {
    ProcessBuilder pb = new ProcessBuilder(command);
    pb.redirectErrorStream(true);
    // TimeLimitProcess p = new TimeLimitProcess(pb.start(), TIMEOUT_SEC * 1000);
    try {
      Process p = pb.start();
      String output = UtilMDE.streamString(p.getInputStream());
      return output;
    } catch (IOException e) {
      return "IOException: " + e.getMessage();
    }
  }


  ///////////////////////////////////////////////////////////////////////////
  /// Properties
  ///

  /**
   * Determines whether a property has value "true", "yes", or "1".
   * @see Properties#getProperty
   **/
  public static boolean propertyIsTrue(Properties p, String key) {
    String pvalue = p.getProperty(key);
    if (pvalue == null) {
      return false;
    }
    pvalue = pvalue.toLowerCase();
    return (pvalue.equals("true") || pvalue.equals("yes") || pvalue.equals("1"));
  }

  /**
   * Set the property to its previous value concatenated to the given value.
   * Returns the previous value.
   * @see Properties#getProperty
   * @see Properties#setProperty
   **/
  public static /*@Nullable*/ String appendProperty(Properties p, String key, String value) {
    return (String)p.setProperty(key, p.getProperty(key, "") + value);
  }

  /**
   * Set the property only if it was not previously set.
   * @deprecated use setDefaultMaybe
   * @see Properties#getProperty
   * @see Properties#setProperty
   **/
  @Deprecated
  public static /*@Nullable*/ String setDefault(Properties p, String key, String value) {
    String currentValue = p.getProperty(key);
    if (currentValue == null) {
      p.setProperty(key, value);
    }
    return currentValue;
  }

  /**
   * Set the property only if it was not previously set.
   * @see Properties#getProperty
   * @see Properties#setProperty
   **/
  public static /*@Nullable*/ String setDefaultMaybe(Properties p, String key, String value) {
    String currentValue = p.getProperty(key);
    if (currentValue == null) {
      p.setProperty(key, value);
    }
    return currentValue;
  }


  ///////////////////////////////////////////////////////////////////////////
  /// Regexp (regular expression)
  ///

  /**
   * @deprecated Use Pattern.quote instead
   * @see Pattern#quote(String)
   */
  @Deprecated
  /*@Pure*/
  public static /*@Regex*/ String patternQuote(String s) {
    return Pattern.quote(s);
  }

  /**
   * @deprecated Use RegexUtil.isRegex instead
   * @see RegexUtil#isRegex(String)
   */
  @Deprecated
  /*@Pure*/
  public static boolean isRegex(String s) {
    return RegexUtil.isRegex(s);
  }

  /**
   * @deprecated Use RegexUtil.regexError instead
   * @see RegexUtil#regexError(String)
   */
  @Deprecated
  /*@Pure*/
  public static /*@Nullable*/ String regexError(String s) {
    return RegexUtil.regexError(s);
  }

  /**
   * @deprecated Use RegexUtil.asRegex instead
   * @see RegexUtil#asRegex(String)
   */
  @Deprecated
  /*@Pure*/
  public static /*@Regex*/ String asRegex(String s) {
    return RegexUtil.asRegex(s);
  }


  ///////////////////////////////////////////////////////////////////////////
  /// Reflection
  ///

  // TODO: make these leave the access the same as it was before?

  // TODO: add method invokeMethod; see
  // java/Translation/src/graph/tests/Reflect.java (but handle returning a
  // value).

  /**
   * Sets the given field, which may be final and/or private.
   * Leaves the field accessible.
   * Intended for use in readObject and nowhere else!
   */
  public static void setFinalField(Object o, String fieldName, /*@Nullable*/ Object value)
    throws NoSuchFieldException {
    Class<?> c = o.getClass();
    while (c != Object.class) { // Class is interned
      // System.out.printf ("Setting field %s in %s%n", fieldName, c);
      try {
        Field f = c.getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(o, value);
        return;
      } catch (NoSuchFieldException e) {
        if (c.getSuperclass() == Object.class) // Class is interned
          throw e;
      } catch (IllegalAccessException e) {
        throw new Error("This can't happen: " + e);
      }
      c = c.getSuperclass();
      assert c != null : "@SuppressWarnings(nullness): c was not Object, so is not null now";
    }
    throw new NoSuchFieldException (fieldName);
  }

  /**
   * Reads the given field, which may be private.
   * Leaves the field accessible.
   * Use with care!
   */
  public static /*@Nullable*/ Object getPrivateField(Object o, String fieldName)
    throws NoSuchFieldException {
    Class<?> c = o.getClass();
    while (c != Object.class) { // Class is interned
      // System.out.printf ("Setting field %s in %s%n", fieldName, c);
      try {
        Field f = c.getDeclaredField(fieldName);
        f.setAccessible(true);
        return f.get(o);
      } catch (IllegalAccessException e) {
        System.out.println("in getPrivateField, IllegalAccessException: " + e);
        throw new Error("This can't happen: " + e);
      } catch (NoSuchFieldException e) {
        if (c.getSuperclass() == Object.class) // Class is interned
          throw e;
        // nothing to do; will now examine superclass
      }
      c = c.getSuperclass();
      assert c != null : "@SuppressWarnings(nullness): c was not Object, so is not null now";
    }
    throw new NoSuchFieldException (fieldName);
  }


  ///////////////////////////////////////////////////////////////////////////
  /// Set
  ///

  /**
   * Returns the object in this set that is equal to key.
   * The Set abstraction doesn't provide this; it only provides "contains".
   * Returns null if the argument is null, or if it isn't in the set.
   **/
  public static /*@Nullable*/ Object getFromSet(Set<?> set, Object key) {
    if (key == null) {
      return null;
    }
    for (Object elt : set) {
      if (key.equals(elt)) {
        return elt;
      }
    }
    return null;
  }


  ///////////////////////////////////////////////////////////////////////////
  /// Stream
  ///

  /** Copy the contents of the input stream to the output stream. */
  public static void streamCopy(java.io.InputStream from, java.io.OutputStream to) {
    byte[] buffer = new byte[1024];
    int bytes;
    try {
      while (true) {
        bytes = from.read(buffer);
        if (bytes == -1) {
          return;
        }
        to.write(buffer, 0, bytes);
      }
    } catch (java.io.IOException e) {
      e.printStackTrace();
      throw new Error(e);
    }
  }

  /** Return a String containing all the characters from the input stream. **/
  public static String streamString(java.io.InputStream is) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    streamCopy(is, baos);
    return baos.toString();
  }


  ///////////////////////////////////////////////////////////////////////////
  /// String
  ///

  /**
   * Return a new string which is the text of target with all instances of
   * oldStr replaced by newStr.
   **/
  public static String replaceString(String target, String oldStr, String newStr) {
    if (oldStr.equals("")) throw new IllegalArgumentException();

    StringBuffer result = new StringBuffer();
    int lastend = 0;
    int pos;
    while ((pos = target.indexOf(oldStr, lastend)) != -1) {
      result.append(target.substring(lastend, pos));
      result.append(newStr);
      lastend = pos + oldStr.length();
    }
    result.append(target.substring(lastend));
    return result.toString();
  }

  /**
   * Return an array of Strings representing the characters between
   * successive instances of the delimiter character.
   * Always returns an array of length at least 1 (it might contain only the
   * empty string).
   * @see #split(String s, String delim)
   **/
  public static String[] split(String s, char delim) {
    ArrayList<String> result_list = new ArrayList<String>();
    for (int delimpos = s.indexOf(delim); delimpos != -1; delimpos = s.indexOf(delim)) {
      result_list.add(s.substring(0, delimpos));
      s = s.substring(delimpos+1);
    }
    result_list.add(s);
    @SuppressWarnings("nullness:new.array.type.invalid") // Checker Framework bug: issue 153 (also @NonNull annotation on next line)
    String[] result = result_list.toArray(new /*@NonNull*/ String[result_list.size()]);
    return result;
  }

  /**
   * Return an array of Strings representing the characters between
   * successive instances of the delimiter String.
   * Always returns an array of length at least 1 (it might contain only the
   * empty string).
   * @see #split(String s, char delim)
   **/
  public static String[] split(String s, String delim) {
    int delimlen = delim.length();
    if (delimlen == 0) {
      throw new Error("Second argument to split was empty.");
    }
    Vector<String> result_list = new Vector<String>();
    for (int delimpos = s.indexOf(delim); delimpos != -1; delimpos = s.indexOf(delim)) {
      result_list.add(s.substring(0, delimpos));
      s = s.substring(delimpos+delimlen);
    }
    result_list.add(s);
    @SuppressWarnings("nullness:new.array.type.invalid") // Checker Framework bug: issue 153 (also @NonNull annotation on next line)
    String[] result = result_list.toArray(new /*@NonNull*/ String[result_list.size()]);
    return result;
  }

  /**
   * Return an array of Strings, one for each line in the argument.
   * Always returns an array of length at least 1 (it might contain only the
   * empty string).  All common line separators (cr, lf, cr-lf, or lf-cr)
   * are supported.  Note that a string that ends with a line separator
   * will return an empty string as the last element of the array.
   * @see #split(String s, char delim)
   **/
  public static String[] splitLines(String s) {
    return s.split ("\r\n?|\n\r?", -1);
  }

  /**
   * Concatenate the string representations of the objects, placing the
   * delimiter between them.
   * @see plume.ArraysMDE#toString(int[])
   **/
  public static String join(Object[] a, String delim) {
    if (a.length == 0) return "";
    if (a.length == 1) return String.valueOf(a[0]);
    StringBuffer sb = new StringBuffer(String.valueOf(a[0]));
    for (int i=1; i<a.length; i++)
      sb.append(delim).append(a[i]);
    return sb.toString();
  }

  /**
   * Concatenate the string representations of the objects, placing the
   * system-specific line separator between them.
   * @see plume.ArraysMDE#toString(int[])
   **/
  public static String joinLines(Object... a) {
    return join(a, lineSep);
  }

  /**
   * Concatenate the string representations of the objects, placing the
   * delimiter between them.
   * @see java.util.AbstractCollection#toString()
   **/
  public static String join(List<?> v, String delim) {
    if (v.size() == 0) return "";
    if (v.size() == 1) return v.get(0).toString();
    // This should perhaps use an iterator rather than get(), for efficiency.
    StringBuffer sb = new StringBuffer(v.get(0).toString());
    for (int i=1; i<v.size(); i++)
      sb.append(delim).append(v.get(i));
    return sb.toString();
  }

  /**
   * Concatenate the string representations of the objects, placing the
   * system-specific line separator between them.
   * @see java.util.AbstractCollection#toString()
   **/
  public static String joinLines(List<String> v, String delim) {
    return join(v, lineSep);
  }

  /**
   * Escape \, ", newline, and carriage-return characters in the
   * target as \\, \", \n, and \r; return a new string if any
   * modifications were necessary.  The intent is that by surrounding
   * the return value with double quote marks, the result will be a
   * Java string literal denoting the original string.
   **/
  public static String escapeNonJava(String orig) {
    StringBuffer sb = new StringBuffer();
    // The previous escape character was seen right before this position.
    int post_esc = 0;
    int orig_len = orig.length();
    for (int i=0; i<orig_len; i++) {
      char c = orig.charAt(i);
      switch (c) {
      case '\"':
      case '\\':
        if (post_esc < i) {
          sb.append(orig.substring(post_esc, i));
        }
        sb.append('\\');
        post_esc = i;
        break;
      case '\n':                // not lineSep
        if (post_esc < i) {
          sb.append(orig.substring(post_esc, i));
        }
        sb.append("\\n");       // not lineSep
        post_esc = i+1;
        break;
      case '\r':
        if (post_esc < i) {
          sb.append(orig.substring(post_esc, i));
        }
        sb.append("\\r");
        post_esc = i+1;
        break;
      default:
        // Nothing to do: i gets incremented
      }
    }
    if (sb.length() == 0)
      return orig;
    sb.append(orig.substring(post_esc));
    return sb.toString();
  }

  // The overhead of this is too high to call in escapeNonJava(String), so
  // it is inlined there.
  /** Like {@link #escapeNonJava(String)}, but for a single character. */
  public static String escapeNonJava(Character ch) {
    char c = ch.charValue();
    switch (c) {
    case '\"':
      return "\\\"";
    case '\\':
      return "\\\\";
    case '\n':                  // not lineSep
      return "\\n";             // not lineSep
    case '\r':
      return "\\r";
    default:
      return new String(new char[] { c });
    }
  }

  /**
   * Escape unprintable characters in the target, following the usual
   * Java backslash conventions, so that the result is sure to be
   * printable ASCII.  Returns a new string.
   **/
  public static String escapeNonASCII(String orig) {
    StringBuffer sb = new StringBuffer();
    int orig_len = orig.length();
    for (int i=0; i<orig_len; i++) {
      char c = orig.charAt(i);
      sb.append(escapeNonASCII(c));
    }
    return sb.toString();
  }

  /**
   * Like escapeNonJava(), but quote more characters so that the
   * result is sure to be printable ASCII. Not particularly optimized.
   **/
  private static String escapeNonASCII(char c) {
    if (c == '"') {
      return "\\\"";
    } else if (c == '\\') {
      return "\\\\";
    } else if (c == '\n') {     // not lineSep
      return "\\n";             // not lineSep
    } else if (c == '\r') {
      return "\\r";
    } else if (c == '\t') {
      return "\\t";
    } else if (c >= ' ' && c <= '~') {
      return new String(new char[] { c });
    } else if (c < 256) {
      String octal = Integer.toOctalString(c);
      while (octal.length() < 3)
        octal = '0' + octal;
      return "\\" + octal;
    } else {
      String hex = Integer.toHexString(c);
      while (hex.length() < 4)
        hex = "0" + hex;
      return "\\u" + hex;
    }
  }

  /**
   * Replace "\\", "\"", "\n", and "\r" sequences by their
   * one-character equivalents.  All other backslashes are removed
   * (for instance, octal/hex escape sequences are not turned into
   * their respective characters). This is the inverse operation of
   * escapeNonJava(). Previously known as unquote().
   **/
  public static String unescapeNonJava(String orig) {
    StringBuffer sb = new StringBuffer();
    // The previous escape character was seen just before this position.
    int post_esc = 0;
    int this_esc = orig.indexOf('\\');
    while (this_esc != -1) {
      if (this_esc == orig.length()-1) {
        sb.append(orig.substring(post_esc, this_esc+1));
        post_esc = this_esc+1;
        break;
      }
      switch (orig.charAt(this_esc+1)) {
      case 'n':
        sb.append(orig.substring(post_esc, this_esc));
        sb.append('\n');        // not lineSep
        post_esc = this_esc+2;
        break;
      case 'r':
        sb.append(orig.substring(post_esc, this_esc));
        sb.append('\r');
        post_esc = this_esc+2;
        break;
      case '\\':
        // This is not in the default case because the search would find
        // the quoted backslash.  Here we incluce the first backslash in
        // the output, but not the first.
        sb.append(orig.substring(post_esc, this_esc+1));
        post_esc = this_esc+2;
        break;

      case '0': case '1': case '2': case '3': case '4':
      case '5': case '6': case '7': case '8': case '9':
        sb.append(orig.substring(post_esc, this_esc));
        char octal_char = 0;
        int ii = this_esc+1;
        while (ii < orig.length()) {
          char ch = orig.charAt(ii++);
          if ((ch < '0') || (ch > '8'))
            break;
          octal_char = (char) ((octal_char * 8)+ Character.digit (ch, 8));
        }
        sb.append (octal_char);
        post_esc = ii-1;
        break;

      default:
        // In the default case, retain the character following the backslash,
        // but discard the backslash itself.  "\*" is just a one-character string.
        sb.append(orig.substring(post_esc, this_esc));
        post_esc = this_esc+1;
        break;
      }
      this_esc = orig.indexOf('\\', post_esc);
    }
    if (post_esc == 0)
      return orig;
    sb.append(orig.substring(post_esc));
    return sb.toString();
  }

  // Use the built-in String.trim()!
  // /** Return the string with all leading and trailing whitespace stripped. */
  // public static String trimWhitespace(String s) {
  //   int len = s.length();
  //   if (len == 0)
  //     return s;
  //   int first_non_ws = 0;
  //   int last_non_ws = len-1;
  //   while ((first_non_ws < len) && Character.isWhitespace(s.charAt(first_non_ws)))
  //     first_non_ws++;
  //   if (first_non_ws == len)
  //     return "";
  //   while (Character.isWhitespace(s.charAt(last_non_ws)))
  //     last_non_ws--;
  //   if ((first_non_ws == 0) && (last_non_ws == len))
  //     return s;
  //   else
  //     return s.substring(first_non_ws, last_non_ws+1);
  // }
  // // // Testing:
  // // assert(UtilMDE.trimWhitespace("foo").equals("foo"));
  // // assert(UtilMDE.trimWhitespace(" foo").equals("foo"));
  // // assert(UtilMDE.trimWhitespace("    foo").equals("foo"));
  // // assert(UtilMDE.trimWhitespace("foo ").equals("foo"));
  // // assert(UtilMDE.trimWhitespace("foo    ").equals("foo"));
  // // assert(UtilMDE.trimWhitespace("  foo   ").equals("foo"));
  // // assert(UtilMDE.trimWhitespace("  foo  bar   ").equals("foo  bar"));
  // // assert(UtilMDE.trimWhitespace("").equals(""));
  // // assert(UtilMDE.trimWhitespace("   ").equals(""));


  /** Remove all whitespace before or after instances of delimiter. **/
  public static String removeWhitespaceAround(String arg, String delimiter) {
    arg = removeWhitespaceBefore(arg, delimiter);
    arg = removeWhitespaceAfter(arg, delimiter);
    return arg;
  }

  /** Remove all whitespace after instances of delimiter. **/
  public static String removeWhitespaceAfter(String arg, String delimiter) {
    // String orig = arg;
    int delim_len = delimiter.length();
    int delim_index = arg.indexOf(delimiter);
    while (delim_index > -1) {
      int non_ws_index = delim_index+delim_len;
      while ((non_ws_index < arg.length())
             && (Character.isWhitespace(arg.charAt(non_ws_index)))) {
        non_ws_index++;
      }
      // if (non_ws_index == arg.length()) {
      //   System.out.println("No nonspace character at end of: " + arg);
      // } else {
      //   System.out.println("'" + arg.charAt(non_ws_index) + "' not a space character at " + non_ws_index + " in: " + arg);
      // }
      if (non_ws_index != delim_index+delim_len) {
        arg = arg.substring(0, delim_index + delim_len) + arg.substring(non_ws_index);
      }
      delim_index = arg.indexOf(delimiter, delim_index+1);
    }
    return arg;
  }

  /** Remove all whitespace before instances of delimiter. **/
  public static String removeWhitespaceBefore(String arg, String delimiter) {
    // System.out.println("removeWhitespaceBefore(\"" + arg + "\", \"" + delimiter + "\")");
    // String orig = arg;
    int delim_len = delimiter.length();
    int delim_index = arg.indexOf(delimiter);
    while (delim_index > -1) {
      int non_ws_index = delim_index-1;
      while ((non_ws_index >= 0)
             && (Character.isWhitespace(arg.charAt(non_ws_index)))) {
        non_ws_index--;
      }
      // if (non_ws_index == -1) {
      //   System.out.println("No nonspace character at front of: " + arg);
      // } else {
      //   System.out.println("'" + arg.charAt(non_ws_index) + "' not a space character at " + non_ws_index + " in: " + arg);
      // }
      if (non_ws_index != delim_index-1) {
        arg = arg.substring(0, non_ws_index + 1) + arg.substring(delim_index);
      }
      delim_index = arg.indexOf(delimiter, non_ws_index+2);
    }
    return arg;
  }


  /**
   * Returns either "n <em>noun</em>" or "n <em>noun</em>s" depending on n.
   * Adds "es" to words ending with "ch", "s", "sh", or "x".
   */
  public static String nplural(int n, String noun) {
    if (n == 1)
      return n + " " + noun;
    else if (noun.endsWith("ch") || noun.endsWith("s") ||
             noun.endsWith("sh") || noun.endsWith("x"))
      return n + " " + noun + "es";
    else
      return n + " " + noun + "s";
  }


  /**
   * Returns a string of the specified length, truncated if necessary,
   * and padded with spaces to the left if necessary.
   */
  public static String lpad(String s, int length) {
    if (s.length() < length) {
      StringBuffer buf = new StringBuffer();
      for (int i = s.length(); i < length; i++) {
        buf.append(' ');
      }
      return buf.toString() + s;
    } else {
      return s.substring(0, length);
    }
  }

  /**
   * Returns a string of the specified length, truncated if necessary,
   * and padded with spaces to the right if necessary.
   */
  public static String rpad(String s, int length) {
    if (s.length() < length) {
      StringBuffer buf = new StringBuffer(s);
      for (int i = s.length(); i < length; i++) {
        buf.append(' ');
      }
      return buf.toString();
    } else {
      return s.substring(0, length);
    }
  }

  /** Converts the int to a String, then formats it using {@link #rpad(String,int)}. */
  public static String rpad(int num, int length) {
    return rpad(String.valueOf(num), length);
  }

  /** Converts the double to a String, then formats it using {@link #rpad(String,int)}. */
  public static String rpad(double num, int length) {
    return rpad(String.valueOf(num), length);
  }

  /**
   * Same as built-in String comparison, but accept null arguments,
   * and place them at the beginning.
   */
  public static class NullableStringComparator
    implements Comparator<String>
  {
    public int compare(String s1, String s2) {
      if (s1 == null && s2 == null) return 0;
      if (s1 == null && s2 != null) return 1;
      if (s1 != null && s2 == null) return -1;
      return s1.compareTo(s2);
    }
  }

  /** Return the number of times the character appears in the string. **/
  public static int count(String s, int ch) {
    int result = 0;
    int pos = s.indexOf(ch);
    while (pos > -1) {
      result++;
      pos = s.indexOf(ch, pos+1);
    }
    return result;
  }

  /** Return the number of times the second string appears in the first. **/
  public static int count(String s, String sub) {
    int result = 0;
    int pos = s.indexOf(sub);
    while (pos > -1) {
      result++;
      pos = s.indexOf(sub, pos+1);
    }
    return result;
  }


  ///////////////////////////////////////////////////////////////////////////
  /// StringTokenizer
  ///

  /**
   * Return a Vector of the Strings returned by
   * {@link java.util.StringTokenizer#StringTokenizer(String,String,boolean)} with the given arguments.
   * <p>
   * The static type is Vector&lt;Object&gt; because StringTokenizer extends
   * Enumeration&lt;Object&gt; instead of Enumeration&lt;String&gt; as it should
   * (probably due to backward-compatibility).
   **/
  public static Vector<Object> tokens(String str, String delim, boolean returnTokens) {
    return makeVector(new StringTokenizer(str, delim, returnTokens));
  }

  /**
   * Return a Vector of the Strings returned by
   * {@link java.util.StringTokenizer#StringTokenizer(String,String)} with the given arguments.
   **/
  public static Vector<Object> tokens(String str, String delim) {
    return makeVector(new StringTokenizer(str, delim));
  }

  /**
   * Return a Vector of the Strings returned by
   * {@link java.util.StringTokenizer#StringTokenizer(String)} with the given arguments.
   **/
  public static Vector<Object> tokens(String str) {
    return makeVector(new StringTokenizer(str));
  }



  ///////////////////////////////////////////////////////////////////////////
  /// Throwable
  ///

  /** For the current backtrace, do "backtrace(new Throwable())". **/
  public static String backTrace(Throwable t) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    t.printStackTrace(pw);
    pw.close();
    String result = sw.toString();
    return result;
  }

  // Deprecated as of 2/1/2004.
  /**
   * @deprecated use "backtrace(new Throwable())" instead, to avoid
   * spurious "at plume.UtilMDE.backTrace(UtilMDE.java:1491)" in output.
   * @see #backTrace(Throwable)
   **/
  @Deprecated
  public static String backTrace() {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    new Throwable().printStackTrace(pw);
    pw.close();
    String result = sw.toString();
    // TODO: should remove "at plume.UtilMDE.backTrace(UtilMDE.java:1491)"
    return result;
  }


  ///////////////////////////////////////////////////////////////////////////
  /// Collections
  ///

  /**
   * Returns the sorted version of the list.  Does not alter the list.
   * Simply calls Collections.sort(List&lt;T&gt;, Comparator&lt;? super T&gt;).
   **/
  public static <T> List<T> sortList (List<T> l, Comparator<? super T> c) {
    List<T> result = new ArrayList<T>(l);
    Collections.sort(result, c);
    return result;
  }


  /**
   * Returns a copy of the list with duplicates removed.
   * Retains the original order.
   **/
  public static <T> List<T> removeDuplicates(List<T> l) {
    // There are shorter solutions that do not maintain order.
    HashSet<T> hs = new HashSet<T>(l.size());
    List<T> result = new ArrayList<T>();
    for (T t : l) {
      if (hs.add(t)) {
        result.add(t);
      }
    }
    return result;
  }


  /**
   * All calls to deepEquals that are currently underway.
   */
  private static HashSet<WeakIdentityPair<Object, Object>> deepEqualsUnderway
    = new HashSet<WeakIdentityPair<Object, Object>>();

  /**
   * Determines deep equality for the elements.
   * <ul>
   * <li>If both are primitive arrays, uses java.util.Arrays.equals.
   * <li>If both are Object[], uses java.util.Arrays.deepEquals and does not recursively call this method.
   * <li>If both are lists, uses deepEquals recursively on each element.
   * <li>For other types, just uses equals() and does not recursively call this method.
   * </ul>
   */
  public static boolean deepEquals(Object o1, Object o2) {
    @SuppressWarnings("interning")
    boolean sameObject = (o1 == o2);
    if (sameObject)
      return true;
    if (o1 == null || o2 == null)
      return false;

    if (o1 instanceof boolean[] && o2 instanceof boolean[]) {
      return Arrays.equals((boolean[]) o1, (boolean[]) o2);
    }
    if (o1 instanceof byte[] && o2 instanceof byte[]) {
      return Arrays.equals((byte[]) o1, (byte[]) o2);
    }
    if (o1 instanceof char[] && o2 instanceof char[]) {
      return Arrays.equals((char[]) o1, (char[]) o2);
    }
    if (o1 instanceof double[] && o2 instanceof double[]) {
      return Arrays.equals((double[]) o1, (double[]) o2);
    }
    if (o1 instanceof float[] && o2 instanceof float[]) {
      return Arrays.equals((float[]) o1, (float[]) o2);
    }
    if (o1 instanceof int[] && o2 instanceof int[]) {
      return Arrays.equals((int[]) o1, (int[]) o2);
    }
    if (o1 instanceof long[] && o2 instanceof long[]) {
      return Arrays.equals((long[]) o1, (long[]) o2);
    }
    if (o1 instanceof short[] && o2 instanceof short[]) {
      return Arrays.equals((short[]) o1, (short[]) o2);
    }

    WeakIdentityPair<Object, Object> mypair
      = new WeakIdentityPair<Object, Object>(o1, o2);
    if (deepEqualsUnderway.contains(mypair)) {
      return true;
    }

    if (o1 instanceof Object[] && o2 instanceof Object[]) {
      return Arrays.deepEquals((Object[]) o1, (Object[]) o2);
    }

    if (o1 instanceof List<?> && o2 instanceof List<?>) {
      List<?> l1 = (List<?>) o1;
      List<?> l2 = (List<?>) o2;
      if (l1.size() != l2.size()) {
        return false;
      }
      try {
        deepEqualsUnderway.add(mypair);
        for (int i=0; i<l1.size(); i++) {
          Object e1 = l1.get(i);
          Object e2 = l2.get(i);
          if (! deepEquals(e1, e2)) {
            return false;
          }
        }
      } finally {
        deepEqualsUnderway.remove(mypair);
      }

      return true;
    }

    return o1.equals(o2);
  }



  ///////////////////////////////////////////////////////////////////////////
  /// Vector
  ///

  /** Returns a vector containing the elements of the enumeration. */
  public static <T> Vector<T> makeVector(Enumeration<T> e) {
    Vector<T> result = new Vector<T>();
    while (e.hasMoreElements()) {
      result.addElement(e.nextElement());
    }
    return result;
  }

  // Rather than writing something like VectorToStringArray, use
  //   v.toArray(new String[0])


  /**
   * Returns a list of lists of each combination (with repetition, but
   * not permutations) of the specified objects starting at index
   * start over dims dimensions, for dims &gt; 0.
   *
   * For example, create_combinations (1, 0, {a, b, c}) returns:
   *    {a}, {b}, {c}
   *
   * And create_combinations (2, 0, {a, b, c}) returns:
   *
   *    {a, a}, {a, b}, {a, c}
   *    {b, b}, {b, c},
   *    {c, c}
   */
  public static <T> List<List<T>> create_combinations (int dims, int start, List<T> objs) {

    if (dims < 1) throw new IllegalArgumentException();

    List<List<T>> results = new ArrayList<List<T>>();

    for (int i = start; i < objs.size(); i++) {
      if (dims == 1) {
        List<T> simple = new ArrayList<T>();
        simple.add (objs.get(i));
        results.add (simple);
      } else {
        List<List<T>> combos = create_combinations (dims-1, i, objs);
        for (Iterator<List<T>> j = combos.iterator(); j.hasNext(); ) {
          List<T> simple = new ArrayList<T>();
          simple.add (objs.get(i));
          simple.addAll (j.next());
          results.add (simple);
        }
      }
    }

    return (results);
  }

  /**
   * Returns a list of lists of each combination (with repetition, but
   * not permutations) of integers from start to cnt (inclusive) over
   * arity dimensions.
   *
   * For example, create_combinations (1, 0, 2) returns:
   *    {0}, {1}, {2}
   *
   * And create_combinations (2, 0, 2) returns:
   *
   *    {0, 0}, {0, 1}, {0, 2}
   *    {1, 1}  {1, 2},
   *    {2, 2}
   */
  public static ArrayList<ArrayList<Integer>> create_combinations (int arity, int start, int cnt) {

    ArrayList<ArrayList<Integer>> results = new ArrayList<ArrayList<Integer>>();

    // Return a list with one zero length element if arity is zero
    if (arity == 0) {
      results.add (new ArrayList<Integer>());
      return (results);
    }

    for (int i = start; i <= cnt; i++) {
      ArrayList<ArrayList<Integer>> combos = create_combinations (arity-1, i, cnt);
      for (Iterator<ArrayList<Integer>> j = combos.iterator(); j.hasNext(); ) {
        ArrayList<Integer> simple = new ArrayList<Integer>();
        simple.add (new Integer(i));
        simple.addAll (j.next());
        results.add (simple);
      }
    }

    return (results);

  }

  /**
   * Returns the simple unqualified class name that corresponds to the
   * specified fully qualified name.  For example if qualified name
   * is java.lang.String, String will be returned.
   **/
  public static String unqualified_name (String qualified_name) {

    int offset = qualified_name.lastIndexOf ('.');
    if (offset == -1)
      return (qualified_name);
    return (qualified_name.substring (offset+1));
  }

  /**
   * Returns the simple unqualified class name that corresponds to the
   * specified class.  For example if qualified name of the class
   * is java.lang.String, String will be returned.
   **/
  public static String unqualified_name (Class<?> cls) {

    return (unqualified_name (cls.getName()));
  }


  // This name "human_readable" is terrible.
  /**
   * Convert a number into an abbreviation such as "5.00K" for 5000 or
   * "65.0M" for 65000000.  K stands for 1000, not 1024; M stands for
   * 1000000, not 1048576, etc.  There are always exactly 3 decimal digits
   * of precision in the result (counting both sides of the decimal point).
   */
  public static String human_readable (long val) {

    double dval = (double) val;
    String mag = "";

    if (val < 1000)
      ;
    else if (val < 1000000) {
      dval = val / 1000.0;
      mag = "K";
    } else if (val < 1000000000) {
      dval = val / 1000000.0;
      mag = "M";
    } else {
      dval = val / 1000000000.0;
      mag = "G";
    }

    String precision = "0";
    if (dval < 10)
      precision = "2";
    else if (dval < 100)
      precision = "1";

    return String.format ("%,1." + precision + "f" + mag, dval);

  }

}
