package daikon.util;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.nio.CharBuffer;

// TODO:
// EntryReader has a public concept of "short entry", but I don't think that
// concept is logically part of EntryReader.  I think it would be better for
// Lookup to make this decision itself, for instance by checking whether there
// are any line separators in the entry that it gets back.
//
// Here are some useful features that EntryReader should have.
//  * It should implement some unimplemented methods from LineNumberReader (see
//    "not yet implemented" in this file).
//  * It should have constructors that take an InputStream or Reader
//    (in addition to the current BufferedReader, File, and String versions).
//  * It should have a close method.
//  * It should automatically close the underlying file/etc. when the
//    iterator gets to the end (or the end is otherwise reached).

/**
 * Class that reads "entries" from a file.  In the simplest case, entries
 * can be lines.  It supports:
 *   include files,
 *   comments, and
 *   multi-line entries (paragraphs).
 * The syntax of each of these is customizable.
 * <p>
 *
 * Example use:
 * <pre>
 *  EntryReader er;
 *  try {
 *    // args are filename, comment regexp, include regexp
 *    er = new EntryReader(filename, "^#.*", null);
 *  } catch (IOException e) {
 *    System.err.println("Unable to read " + filename);
 *    System.exit(2);
 *    throw new Error("This can't happen"); // for definite assignment check
 *  }
 *  for (String line : er) {
 *    ...
 *  }
 * </pre>
 *
 * @see #get_entry() and @see #set_entry_start_stop(String,String)
 */
public class EntryReader extends LineNumberReader implements Iterable<String>, Iterator<String> {

  ///
  /// User configuration variables
  ///

  /** Regular expression that specifies an include file. **/
  private final /*@Nullable*/ /*@Regex(1)*/ Pattern include_re;

  /** Regular expression that matches a comment **/
  private final /*@Nullable*/ Pattern comment_re;

  /**
   * Regular expression that starts a long entry.
   * <p>
   * If the first line of an entry matches this regexp, then the entry is
   * terminated by:  entry_stop_re, another line that matches
   * entry_start_re (even not following a newline), or the end of the
   * current file.
   * <p>
   * Otherwise, the first line of an entry does NOT match this regexp (or
   * the regexp is null), in which case the entry is terminated by a blank
   * line or the end of the current file.
   */
  public /*@LazyNonNull*/ /*@Regex(1)*/ Pattern entry_start_re = null;

  /**
   * @see #entry_start_re
   */
  public /*@LazyNonNull*/ Pattern entry_stop_re = null;

  ///
  /// Internal implementation variables
  ///

  /** Stack of readers.  Used to support include files */
  private final Stack<FlnReader> readers = new Stack<FlnReader>();

  /** Line that is pushed back to be reread **/
  /*@Nullable*/ String pushback_line = null;

  /** Platform-specific line separator **/
  private static final String lineSep = System.getProperty("line.separator");

  ///
  /// Helper classes
  ///

  /**
   * Like LineNumberReader, but also has a filename field.
   * "FlnReader" stands for "Filename and Line Number Reader".
   */
  private static class FlnReader extends LineNumberReader {
    public String filename;

    /**
     * Filename must be non-null.
     * If there isn't a name, clients should provide a dummy value.
     */
    public FlnReader (Reader reader, String filename) {
      super(reader);
      this.filename = filename;
    }

    public FlnReader (String filename) throws IOException {
      super(UtilMDE.fileReader(filename));
      this.filename = filename;
    }
  }

  /** Descriptor for an entry (paragraph) **/
  public static class Entry {
    /** First line  of the entry */
    public String first_line;
    /** Complete body of the entry including the first line **/
    public String body;
    /** True if this is a short entry (blank line separated) **/
    boolean short_entry;
    /** Filename in which the entry was found **/
    String filename;
    /** Line number of first line of entry **/
    long line_number;

    /** Create an entry **/
    Entry (String first_line, String body, String filename, long line_number,
           boolean short_entry) {
      this.first_line = first_line;
      this.body = body;
      this.filename = filename;
      this.line_number = line_number;
      this.short_entry = short_entry;
    }

    /**
     * Return a substring of the entry body that matches the specified
     * regular expression.  If no match is found, returns the first_line.
     */
    String get_description (/*@Nullable*/ Pattern re) {

      if (re == null)
        return first_line;

      Matcher descr = re.matcher (body);
      if (descr.find()) {
        return descr.group();
      } else {
        return first_line;
      }
    }
  }

  ///
  /// Constructors
  ///

  /// Inputstream and charset constructors

  /** Create a EntryReader that uses the given character set.
   * @see #EntryReader(InputStream,String,String,String) **/
  public EntryReader (InputStream in,
                      String charsetName,
                      String filename,
                      /*@Nullable*/ /*@Regex*/ String comment_re_string,
                      /*@Nullable*/ /*@Regex(1)*/ String include_re_string) throws UnsupportedEncodingException {
    this(new InputStreamReader(in, charsetName),
         filename, comment_re_string, include_re_string);
  }

  /** Create a EntryReader that does not support comments or include directives.
   * @see #EntryReader(InputStream,String,String,String) **/
  public EntryReader (InputStream in, String charsetName, String filename) throws UnsupportedEncodingException {
    this (in, charsetName, filename, null, null);
  }

  /// Inputstream (no charset) constructors

  /**
   * Create a EntryReader
   *
   *    @param in Initial source
   *    @param filename Non-null file name for stream being read
   *    @param comment_re_string Regular expression that matches comments.
   *                      Any text that matches comment_re is removed.
   *                      A line that is entirely a comment is ignored
   *    @param include_re_string Regular expression that matches include directives.
   *                      The expression should define one group that contains
   *                      the include file name
   */
  public EntryReader (InputStream in, String filename,
                      /*@Nullable*/ /*@Regex*/ String comment_re_string,
                      /*@Nullable*/ /*@Regex(1)*/ String include_re_string) {
    this(new InputStreamReader(in),
         filename, comment_re_string, include_re_string);
  }

  /**
   * Create a EntryReader that uses the default character set and does not
   * support comments or include directives.
   * @see #EntryReader(InputStream,String,String,String,String) **/
  public EntryReader (InputStream in, String filename) {
    this (in, filename, null, null);
  }

  /** Create a EntryReader that does not support comments or include directives.
   * @see #EntryReader(InputStream,String,String,String) **/
  public EntryReader (InputStream in) {
    this (in, "(InputStream)", null, null);
  }


  private static class DummyReader extends Reader {
    @Override
    public void close() { throw new Error("DummyReader"); }
    @Override
    public void mark(int readAheadLimit) { throw new Error("DummyReader"); }
    @Override
    public boolean markSupported() { throw new Error("DummyReader"); }
    @Override
    public int read() { throw new Error("DummyReader"); }
    @Override
    public int read(char[] cbuf) { throw new Error("DummyReader"); }
    @Override
    public int read(char[] cbuf, int off, int len) { throw new Error("DummyReader"); }
    @Override
    public int read(CharBuffer target) { throw new Error("DummyReader"); }
    @Override
    public boolean ready() { throw new Error("DummyReader"); }
    @Override
    public void reset() { throw new Error("DummyReader"); }
    @Override
    public long skip(long n) { throw new Error("DummyReader"); }
  }



  /**
   * Create a EntryReader
   *
   *    @param reader Initial source
   *    @param comment_re_string Regular expression that matches comments.
   *                      Any text that matches comment_re is removed.
   *                      A line that is entirely a comment is ignored
   *    @param include_re_string Regular expression that matches include directives.
   *                      The expression should define one group that contains
   *                      the include file name
   */
  public EntryReader (Reader reader, String filename,
                      /*@Nullable*/ /*@Regex*/ String comment_re_string,
                      /*@Nullable*/ /*@Regex(1)*/ String include_re_string) {
    // we won't use superclass methods, but passing null as an argument
    // leads to a NullPointerException.
    super(new DummyReader());
    readers.push (new FlnReader (reader, filename));
    if (comment_re_string == null)
      comment_re = null;
    else
      comment_re = Pattern.compile (comment_re_string);
    if (include_re_string == null)
      include_re = null;
    else
      include_re = Pattern.compile (include_re_string);
  }

  /** Create a EntryReader that does not support comments or include directives.
   * @see #EntryReader(Reader,String,String,String) **/
  public EntryReader (Reader reader) {
    this (reader, reader.toString(), null, null);
  }

  /// File Constructors

  /**
   * Create a EntryReader
   *
   *    @param file       Initial file to read.
   *    @param comment_re Regular expression that matches comments.
   *                      Any text that matches comment_re is removed.
   *                      A line that is entirely a comment is ignored.
   *    @param include_re Regular expression that matches include directives.
   *                      The expression should define one group that contains
   *                      the include file name.
   */
  public EntryReader (File file, /*@Nullable*/ /*@Regex*/ String comment_re,
                      /*@Nullable*/ /*@Regex(1)*/ String include_re) throws IOException {
    this (UtilMDE.fileReader (file),
          file.toString(), comment_re, include_re);
  }

  /** Create a EntryReader that does not support comments or include directives.
   * @see #EntryReader(File,String,String) **/
  public EntryReader (File file) throws IOException {
    this (file, null, null);
  }

  /** Create a EntryReader that does not support comments or include directives.
   * @see #EntryReader(File,String,String) **/
  public EntryReader (File file, String charsetName) throws IOException {
    this (UtilMDE.fileInputStream (file),
          charsetName, file.toString(), null, null);
  }

  /// Filename constructors

  /**
   * Create a new EntryReader starting with the specified file.
   * @see #EntryReader(File,String,String)
   */
  public EntryReader (String filename, /*@Nullable*/ /*@Regex*/ String comment_re,
                      /*@Nullable*/ /*@Regex(1)*/ String include_re) throws IOException {
    this (new File(filename), comment_re, include_re);
  }

  /** Create a EntryReader that does not support comments or include directives.
   * @see #EntryReader(String,String,String) **/
  public EntryReader (String filename) throws IOException {
    this (filename, null, null);
  }

  /** Create a EntryReader that does not support comments or include directives.
   * @see #EntryReader(String,String,String) **/
  public EntryReader (String filename, String charsetName) throws IOException {
    this (new FileInputStream(filename), charsetName, filename, null, null);
  }

  ///
  /// Methods
  ///

  /**
   * Read a line, ignoring comments and processing includes.  Note that
   * a line that is completely a comment is completely ignored (and
   * not returned as a blank line).  Returns null at end of file.
   */
  @Override
  public /*@Nullable*/ String readLine() throws IOException {

    // System.out.printf ("Entering size = %d%n", readers.size());

    // If a line has been pushed back, return it instead
    if (pushback_line != null) {
      String line = pushback_line;
      pushback_line = null;
      return line;
    }

    String line = get_next_line();
    if (comment_re != null) {
      while (line != null) {
        Matcher cmatch = comment_re.matcher (line);
        if (cmatch.find()) {
          line = cmatch.replaceFirst ("");
          if (line.length() > 0)
            break;
        } else {
          break;
        }
        line = get_next_line();
      // System.out.printf ("get_next_line = %s%n", line);
      }
    }

    if (line == null)
      return null;

    // Handle include files.  Non-absolute pathnames are relative
    // to the including file (the current file)
    if (include_re != null) {
      Matcher m = include_re.matcher (line);
      if (m.matches()) {
        String filename_string = m.group (1);
        if (filename_string == null) {
          throw new Error(String.format("include_re (%s) does not capture group 1 in %s",
                                        include_re, line));
        }
        File filename = new File (UtilMDE.expandFilename(filename_string));
        // System.out.printf ("Trying to include filename %s%n", filename);
        if (!filename.isAbsolute()) {
          FlnReader reader = readers.peek();
          File current_filename = new File (reader.filename);
          File current_parent = current_filename.getParentFile();
          filename = new File (current_parent, filename.toString());
          // System.out.printf ("absolute filename = %s %s %s%n",
          //                     current_filename, current_parent, filename);
        }
        readers.push (new FlnReader (filename.getAbsolutePath()));
        return readLine();
      }
    }

    // System.out.printf ("Returning [%d] '%s'%n", readers.size(), line);
    return (line);
  }

  /**
   * Returns a line-by-line interator for this file.
   * <p>
   *
   * <b>Warning:</b> This does not return a fresh iterator each time.  The
   * iterator is a singleton, the same one is returned each time, and a new
   * one can never be created after it is exhausted.
   **/
  @Override
  public Iterator<String> iterator() {
    return this;
  }

  /**
   * Returns whether or not there is another line to read.  Any IOExceptions
   * are turned into errors (because the definition of hasNext() in Iterator
   * doesn't throw any exceptions).
   **/
  @Override
  public boolean hasNext() {
    if (pushback_line != null)
      return true;

    String line = null;
    try {
      line = readLine();
    } catch (IOException e) {
      throw new Error ("unexpected IOException: ", e);
    }

    if (line == null)
      return false;

    putback (line);
    return true;
  }

  /**
   * Returns the next line in the multi-file.
   * Throws NoSuchElementException at end of file.
   **/
  @Override
  public String next() {
    try {
      String result = readLine();
      if (result != null) {
        return result;
      } else {
        throw new NoSuchElementException();
      }
    } catch (IOException e) {
      throw new Error ("unexpected IOException", e);
    }
  }

  /** remove() is not supported **/
  @Override
  public void remove() {
    throw new UnsupportedOperationException ("can't remove lines from file");
  }

  /**
   * Returns the next entry (paragraph) in the file.  Entries are separated
   * by blank lines unless the entry started with {@link #entry_start_re}
   * (see {@link #set_entry_start_stop}).  If no more entries are
   * available, returns null.
   */
  public /*@Nullable*/ Entry get_entry() throws IOException {

    // Skip any preceding blank lines
    String line = readLine();
    while ((line != null) && (line.trim().length() == 0))
      line = readLine();
    if (line == null)
      return (null);

    StringBuilder body = new StringBuilder(10000);
    Entry entry = null;
    String filename = get_filename();
    long line_number = get_line_number();

    // If first line matches entry_start_re, this is a long entry.
    /*@Regex(1)*/ Matcher entry_match = null;
    if (entry_start_re != null)
      entry_match = entry_start_re.matcher (line);
    if ((entry_match != null) && entry_match.find()) {
      assert entry_start_re != null : "@SuppressWarnings(nullness): dependent: entry_match != null";
      assert entry_stop_re != null : "@SuppressWarnings(nullness): dependent: entry_start_re != null";

      // Remove entry match from the line
      if (entry_match.groupCount() > 0) {
        @SuppressWarnings("nullness") // dependent: just checked that group 1 exists in match
        /*@NonNull*/ String match_group_1 = entry_match.group(1);
        line = entry_match.replaceFirst (match_group_1);
      }

      // Description is the first line
      String description = line;

      // Read until we find the termination of the entry
      Matcher end_entry_match = entry_stop_re.matcher(line);
      while ((line != null) && !entry_match.find() &&
             !end_entry_match.find() && filename.equals (get_filename())) {
        body.append (line);
        body.append (lineSep);
        line = readLine();
        if (line == null) {
          break; // end of file serves as entry terminator
        }
        entry_match = entry_start_re.matcher(line);
        end_entry_match = entry_stop_re.matcher(line);
      }

      // If this entry was terminated by the start of the next one,
      // put that line back
      if ((line != null) && (entry_match.find(0)
                             || !filename.equals (get_filename())))
        putback (line);

      entry = new Entry (description, body.toString(), filename,
                                     line_number, false);

    } else { // blank-separated entry

      String description = line;

      // Read until we find another blank line
      while ((line != null) && (line.trim().length() != 0)
             && filename.equals (get_filename())) {
        body.append (line);
        body.append (lineSep);
        line = readLine();
      }

      // If this entry was terminated by the start of a new input file
      // put that line back
      if ((line != null) && !filename.equals (get_filename()))
        putback (line);

      entry = new Entry (description, body.toString(), filename, line_number,
                         true);
    }

    return (entry);

  }



  /**
   * Reads the next line from the current reader.  If EOF is encountered
   * pop out to the next reader.  Returns null if there is no more input.
   */
  private /*@Nullable*/ String get_next_line() throws IOException {

    if (readers.size() == 0)
      return (null);

    FlnReader ri1 = readers.peek();
    String line = ri1.readLine();
    while (line == null) {
      readers.pop();
      if (readers.empty())
        return (null);
      FlnReader ri2 = readers.peek();
      line = ri2.readLine();
    }
    return (line);
  }

  /**
   * @deprecated
   * @see #getFileName
   */
  @Deprecated
  public String get_filename() {
    return getFileName();
  }


  /** Returns the current filename **/
  public String getFileName() {
    FlnReader ri = readers.peek();
    if (ri == null)
      throw new Error("Past end of input");
    return ri.filename;
  }

  /**
   * @deprecated
   * @see #getLineNumber
   */
  @Deprecated
  public long get_line_number() {
    return getLineNumber();
  }

  /** Return the current line number in the current file. **/
  @Override
  public int getLineNumber() {
    FlnReader ri = readers.peek();
    if (ri == null)
      throw new Error("Past end of input");
    return ri.getLineNumber();
  }

  /** Set the current line number in the current file. **/
  @Override
  public void setLineNumber(int lineNumber) {
    FlnReader ri = readers.peek();
    if (ri == null)
      throw new Error("Past end of input");
    ri.setLineNumber(lineNumber);
  }


  /**
   * Set the regular expressions for the start and stop of long
   * entries (multiple lines that are read as a group by get_entry()).
   */
  public void set_entry_start_stop (/*@Regex(1)*/ String entry_start_re,
                                    /*@Regex*/ String entry_stop_re) {
    this.entry_start_re = Pattern.compile (entry_start_re);
    this.entry_stop_re = Pattern.compile (entry_stop_re);
  }

  /**
   * Set the regular expressions for the start and stop of long
   * entries (multiple lines that are read as a group by get_entry()).
   */
  public void set_entry_start_stop (/*@Regex(1)*/ Pattern entry_start_re,
                                    Pattern entry_stop_re) {
    this.entry_start_re = entry_start_re;
    this.entry_stop_re = entry_stop_re;
  }

  /**
   * Puts the specified line back in the input.  Only one line can be
   * put back.
   */
  // TODO:  This would probably be better implemented with the "mark"
  // mechanism of BufferedReader (which is also in LineNumberReader and FlnReader).
  public void putback (String line) {
    assert pushback_line == null : "push back '" + line + "' when '"
      + pushback_line + "' already back";
    pushback_line = line;
  }

  /** Mark the present position in the stream. */
  @Override
  public void mark(int readAheadLimit) {
    throw new Error("not yet implemented");
  }
  /** Read a single character. */
  @Override
  public int read() {
    throw new Error("not yet implemented");
  }
  /** Read characters into a portion of an array. */
  @Override
  public int read(char[] cbuf, int off, int len) {
    throw new Error("not yet implemented");
  }
  /** Reset the stream to the most recent mark. */
  @Override
  public void reset() {
    throw new Error("not yet implemented");
  }
  /** Skip characters. */
  @Override
  public long skip(long n) {
    throw new Error("not yet implemented");
  }


  /** Simple example **/
  public static void main (String[] args) throws IOException {

    if (args.length < 1 || args.length > 3) {
      System.err.println("EntryReader sample program requires 1-3 args: filename [comment_re [include_re]]");
      System.exit(1);
    }
    String filename = args[0];
    String comment_re = null;
    String include_re = null;
    if (args.length >= 2) {
      comment_re = args[1];
      if (! RegexUtil.isRegex(comment_re)) {
        System.err.println("Error parsing comment regex \"" + comment_re + "\": " + RegexUtil.regexError(comment_re));
        System.exit(1);
      }
    }
    if (args.length >= 3) {
      include_re = args[2];
      if (! RegexUtil.isRegex(include_re, 1)) {
        System.err.println("Error parsing include regex \"" + include_re + "\": " + RegexUtil.regexError(include_re));
        System.exit(1);
      }
    }
    EntryReader reader = new EntryReader (filename, comment_re, include_re);

    String line = reader.readLine();
    while (line != null) {
      System.out.printf ("%s: %d: %s%n", reader.get_filename(),
                         reader.get_line_number(), line);
      line = reader.readLine();
    }
  }
}
