package typequals;

import checkers.basetype.BaseTypeChecker;
import checkers.quals.*;

/**
 * VIndexChecker ensures that the programmer does not accidentally mix indices that are intended for use into VarInfo lists vs. value lists.
 * <p>
 *
 * A word about defaults:  It is never necessary to write @NonPrototype,
 * because every use of every type defaults to NonPrototype (and in fact
 * this default applies to every class declaration as well).  The
 * declaration of Invariant (and its subclasses) are explicitly marked
 * as @Prototype, so that it is possible for instances to be marked either
 * with @Prototype or @NonPrototype.
 **/

@TypeQualifiers({ VIndexTop.class, ValueIndex.class, VarIndex.class, VIndexUnqualified.class, VIndexBottom.class })
public final class VIndexChecker extends BaseTypeChecker { }
