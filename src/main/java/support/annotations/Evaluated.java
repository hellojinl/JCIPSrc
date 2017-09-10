package support.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 代码评价
 * <ul>
 * <li>^_^ - good</li>
 * <li>>_< - bad</li>
 * <li>-_- - not bad</li>
 * </ul>
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
@Retention(SOURCE)
@Target({ TYPE, METHOD })
public @interface Evaluated {

    String value();
}
