package daikon.chicory;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 *  This is a subtype of DaikonVariableInfo and is used as a
 *  "placeholder" for the root of the tree.  It contains no variable
 *  information other than what is stored in its children.
 */
@SuppressWarnings("nullness")   // to do
public class RootInfo extends DaikonVariableInfo
{
    private RootInfo()
    {
        //the root needs no name, etc. but set them to preserve nullness property
        super(" RootInfo Object ", " RootInfo Object type ", " RootInfo Object reptype ");
    }

    @Override
    public Object getMyValFromParentVal(Object value)
    {
        throw new RuntimeException ("No val for RootInfo");
    }

    /**
     * Creates a RootInfo object for a method entry program point.
     */
    public static RootInfo enter_process (MethodInfo mi, int depth)
    {
        RootInfo root = new RootInfo();

        // System.out.println ("Entering method " + mi);
        // debug_vars.enabled = mi.toString().contains ("Job.<init>");

        debug_vars.clear ("building enter tree for %s%n", mi);

        // Don't build a tree for class initializers.
        if (mi.is_class_init())
            return (root);

        // Clear the set of static variables
        ppt_statics.clear();

        // Print class variables.   Print class variables first because
        // the depth goes deeper there ('this' is not counted).  This
        // guarantees that any static variables in the class are found here
        // and not below.
        if (!(mi.member instanceof Constructor<?>)) {
            root.addClassVars(mi.class_info,
                              Modifier.isStatic(mi.member.getModifiers()),
                              mi.member.getDeclaringClass(), /*offset = */ "",
                              depth);
        }

        // Print each parameter
        root.addParameters(mi.class_info, mi.member,
                           Arrays.<String>asList(mi.arg_names), /*offset = */ "",
                           depth);

        return root;
    }

    /**
     * Creates a RootInfo object for a method exit program point.
     */
    public static RootInfo exit_process(MethodInfo mi, int depth)
    {
        RootInfo root = new RootInfo();

        // debug_vars.enabled = mi.toString().contains ("Job.<init>");

        debug_vars.clear ("building exit tree for %s%n", mi);

        // Don't build a tree for class initializers.
        if (mi.is_class_init())
            return (root);

        // Clear the set of static variables
        ppt_statics.clear();

        // Print class variables.   Print class variables first because
        // the depth goes deeper there ('this' is not counted).  This
        // guarantees that any static variables in the class are found here
        // and not below.
        root.addClassVars(mi.class_info,
                          Modifier.isStatic(mi.member.getModifiers()),
                          mi.member.getDeclaringClass(),
                          "",
                          depth);

        // Print arguments
        root.addParameters(mi.class_info,
                           mi.member,
                           Arrays.<String>asList(mi.arg_names),
                           /*offset = */ "",
                           depth);

        // Print return type information for methods only and not constructors
        if (mi.member instanceof Method)
        {
            Class<?> returnType = ((Method) mi.member).getReturnType();
            if (!(returnType.equals(Void.TYPE)))
            {
                // add a new ReturnInfo object to the traversal tree
                DaikonVariableInfo retInfo = new ReturnInfo(returnType);

                root.addChild(retInfo);

                retInfo.checkForDerivedVariables(returnType, "return", "");

                retInfo.addChildNodes(mi.class_info, returnType, "return", "",
                        depth);
            }
        }

        return root;
    }

    /**
     * Creates a RootInfo object for an object program point.
     * This will include the class' fields and the "this" object.
     */
    public static RootInfo getObjectPpt(ClassInfo cinfo, int depth)
    {
        RootInfo root = new RootInfo();

        // Clear the set of static variables
        ppt_statics.clear();

        root.addClassVars(cinfo, /*dontPrintInstanceVars = */ false,
                cinfo.clazz, /*offset = */ "", depth);

        return root;
    }

    /**
     * Creates a RootInfo object for a class program point.
     * This will just include static fields.
     */
    public static RootInfo getClassPpt(ClassInfo cinfo, int depth)
    {
        RootInfo root = new RootInfo();

        // Clear the set of static variables
        ppt_statics.clear();

        root.addClassVars(cinfo, /*dontPrintInstanceVars = */ true,
                cinfo.clazz, /*offset = */ "", depth);

        return root;
    }

    public VarKind get_var_kind() {
        throw new RuntimeException ("No var-kind for RootInfo");
    }

}
