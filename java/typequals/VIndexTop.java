package typequals;

import java.lang.annotation.*;
import checkers.quals.*;

@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@TypeQualifier
@SubtypeOf({})
public @interface VIndexTop {}
