package annoyaml.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Flatten the yaml into the parent.
 * 
 * This prevents round-tripping, but also prevents the generation of a hierarchy within the YAML,
 * which could be useful in some circumstances.
 * 
 * @author jsightler
 *
 */
@Target(value={ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface YAMLFlatten {

}
