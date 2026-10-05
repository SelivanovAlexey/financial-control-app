package app.core.errorhandling.utils;

import lombok.experimental.UtilityClass;
import org.hibernate.exception.ConstraintViolationException;

@UtilityClass
public class ConstraintViolations {
    public static String getConstraint(Throwable ex) {
        while (ex != null) {
            if (ex instanceof ConstraintViolationException cve) {
                return cve.getConstraintName();
            }
            ex = ex.getCause();
        }
        return null;
    }
}
