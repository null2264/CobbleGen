#if MC<=11605 || MC>=12105
package net.minecraft.gametest.framework;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
* Does absolutely nothing, it's here just so I don't have to deal with ifdef fuckery
*/
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface GameTest {
    #if FORGE
    String templateNamespace();
    #endif

    String template();

    int timeoutTicks();
}
#endif
