# Installation #
The following installation instructions work for Unix/Linux/MacOS system.  To perform an installation in Windows, please use Cygwin.
  1. Choose the directory where you want to install our tool for inferring meta-invariants; we'll call this the GetMetaInvDIR directory. In this directory, download and unpack getmetainv-binary-0.1.tar.gz.
> > `tar zxf getmetainv-binary-0.1.tar.gz`
  1. Download the Simplfy theorem prover version for your platform ([Simplify-1.5.4.exe for Windows](https://docs.google.com/file/d/0B2Usw02WBShXajMwNTBYRFNic2M/edit?usp=sharing),[Simplify-1.5.4.linux](https://docs.google.com/file/d/0B2Usw02WBShXeV9fMGUtTnhyTU0/edit?usp=sharing),[Simplify-1.5.4.macosx](https://docs.google.com/file/d/0B2Usw02WBShXdk56ZXdIR0J5ZWs/edit?usp=sharing), [Simplify-1.5.4.solaris](https://docs.google.com/file/d/0B2Usw02WBShXUlFfWGtYNmlQWEk/edit?usp=sharing), or [Simplify-1.5.4.tru64](https://docs.google.com/file/d/0B2Usw02WBShXcUY1bl9hbUJHOEk/edit?usp=sharing)). Change the executable's name to Simplify and put it into the directory $SimplifyDIR.  Make sure that execute permissions are set via this command:
> > `chmod a+x Simplify`
  1. You must install the JDK in your operating system. The Java 6 is recommended.
  1. Place the following five commands in your shell initialization file: set three environment variables, and update the environment variable CLASSPATH and PATH .  For example,  edit ~/.bashrc or ~/.bash\_profile if you use the sh or bash shell or their variants:
```
          # The full pathname of the directory that contains GetMetaInv
          export GetMetaInvDIR=...
	  # The full pathname of the directory that contains Simplify
	  export SimplifyDIR=...
          # The full pathname of the directory that contains the Java JDK
          export JAVA_HOME=...
          
          export CLASSPATH=$GetMetaInvDIR/getmetainv.jar:$GetMetaInvDIR/lib:$GetMetaInvDIR/lib/bcel.jar:$GetMetaInvDIR/lib/java-getopt.jar:$GetMetaInvDIR/lib/checkers-quals.jar:$GetMetaInvDIR/lib/commons-io.jar:$GetMetaInvDIR/lib/plume.jar:$GetMetaInvDIR/lib/typequals.jar:$GetMetaInvDIR/lib/utilMDE-20091207.jar:$JAVA_HOME/lib/tools.jar:.:$CLASSPATH 
	  export PATH=$SimplifyDIR:$JAVA_HOME/bin:$PATH
```
  1. After editing your shell initialization file, either execute the commands you placed in it, or else log out and log back in to achieve the same effect.

# Usage #

Now you can use our tool to generate meta-invariants.  Suppose you want to generate meta-invariants for a test subject program ABC.
  1. Compile ABC with the -g switch to enable debugging symbols.
> > `javac -g ...`
  1. Run the unit tests on ABC using the Chicory front end:
> > `java daikon.Chicory [chicory-options] MyTestClass`
> > Make sure all the required library and class files to run ABC are included in the classpath.  Chicory can be configured as described [here](http://groups.csail.mit.edu/pag/daikon/download/doc/daikon.html#Chicory-options).
> > It is worth noting Chicory tries to record every variable values in a data file.  When a large number of test cases need to be run, Chicory can easily consume a large chunk of harddisk space in compressed format.  It is helpful to use the option –ppt-select-pattern and –ppt-omit-pattern here to only record variables values for the methods you are interested in.
> > If you use a test framework (such as JUnit and Maven Surefire plug-in), you may not be able to directly invoke daikon.Chicory because you need to invoke the test driver first. You can then load the "Java agent" to JVM which provides instrumentation access to Chicory (it's all inside $GetMetaInvDIR/lib/ChicoryPremain.jar). Specifically:
      * When using JUnit command line driver org.junit.runner.JUnitCore, run Chicory via the command
> > > `java  -javaagent:$GetMetaInvDIR/lib/ChicoryPremain.jar=[chicory-options]  org.junit.runner.JUnitCore testclasses`
      * When using the `<junit>` element in a build.xml script, it is important to make fork “on” to run the tests in a separate VM. Also, you may need to specify the locations of jars required by ChicoryPremain.jar using a `<classpath>` element. The following is a sample:
```
<junit fork="on" forkmode="perTest" > 
    <classpath> 
        <pathelement location="${jdk.tools.jar}"/> 
        <pathelement location="${junit.dir}/junit.jar"/> 
        <pathelement location="${getmetainv.basedir}/getmetainv.jar"/> 
        <fileset dir="${getmetainv.lib.dir}" includes="ChicoryPremain.jar,bcel.jar,java-getopt.jar,checkers-quals.jar,commons-io.jar,plume.jar,typequals.jar,utilMDE-20091207.jar"/>
        <!-- other required libraries .. -->
        <pathelement ... 
    </classpath> 
    <jvmarg value="-javaagent:${getmetainv.lib.dir}/ChicoryPremain.jar=[chicory-options]"/> 
     <batchtest ...> 
         <!-- Define a number of tests based on pattern matching. -->
        ...
    </batchtest> 
</junit> 
```
      * When using Maven, you can use the following sample  pom.xml file:
```
<plugin>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <argLine>
        -javaagent:${getmetainv.basedir}/lib/ChicoryPremain.jar=[chicory-options]
        </argLine>
        <additionalClasspathElements>
        <additionalClasspathElement>
	  ${getmetainv.lib.dir}/bcel.jar
        </additionalClasspathElement>
        <additionalClasspathElement>
	  ${getmetainv.lib.dir}/java-getopt.jar
        </additionalClasspathElement>
                <additionalClasspathElement>
	  ${getmetainv.lib.dir}/checkers-quals.jar
        </additionalClasspathElement>
        <additionalClasspathElement>
	  ${getmetainv.lib.dir}/commons-io.jar
        </additionalClasspathElement>
        <additionalClasspathElement>
	  ${getmetainv.lib.dir}/plume.jar
        </additionalClasspathElement>
        <additionalClasspathElement>
	  ${getmetainv.lib.dir}/typequals.jar
        </additionalClasspathElement>
        <additionalClasspathElement>
	  ${getmetainv.lib.dir}/utilMDE-20091207.jar
        </additionalClasspathElement>
                <additionalClasspathElement>
	  ${jdk.tools.jar}
        </additionalClasspathElement>
        <additionalClasspathElement>
	  <pathelement location="${getmetainv.basedir}/getmetainv.jar"/> 
        </additionalClasspathElement>
                <additionalClasspathElement>
	  <pathelement location="$${getmetainv.lib.dir}/ChicoryPremain.jar"/> 
        </additionalClasspathElement>
        </additionalClasspathElements>
    </configuration>
</plugin>
```
  1. Normalise parameters so that all parameters's name in the dtrace file are based on their position (1st, 2nd, ...) in the parameter list.   You must have awk installed to run our awk script.

> > `zcat MyTestClass.dtrace.gz | gawk -f $GetMetaInvDIR/normalise-parameters.awk | gzip > NMyTestClass.dtrace.gz`
  1. Pass the normalised dtrace file to our modified Daikon and use –suppress\_redundant to invoke Simplify automatic theorem prover.  You can supply multiple dtrace files to our tool as well. See [Daikon's configuration options](http://groups.csail.mit.edu/pag/daikon/download/doc/daikon.html#Configuration-options).  Our modified Daikon prints the inferred meta-invariants to the standard output. We filter out meta-invariants whose success rate is below a threshold (by default 0.75) and iterate the unfiltered meta-invariants in ascending order of average implication confidence (See the FSE 2013 paper for definition). You can change the threshold from the command line by setting the successrate.threshold property of the Java runtime. The original invariants are also printed.
> > `java [-Dsuccessrate.threshold=value] daikon.Daikon  --suppress_redundant [daikon-options] NMyTestClass.dtrace.gz`