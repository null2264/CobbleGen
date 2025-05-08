#if FABRIC || MC>=12105
package net.neoforged.neoforge.gametest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
* Does absolutely nothing, it's here just so I don't have to deal with ifdef fuckery
*/
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface PrefixGameTestTemplate {
    boolean value();
}
#endif
