package org.easyb.delegates;

import groovy.lang.Closure;
import org.easyb.exception.VerificationException;

import java.util.ArrayList;
import java.util.List;

/**
 * The easy delegate handles "it", "then", and "when"
 * Currently, this delegate isn't plug and play.
 *
 * @author aglover
 */
public class EnsuringDelegate {
    /**
     * @param clzz    the class of the exception type expected
     * @param closure closure containing code to be run that should throw an
     *                exception
     */
    public void ensureThrows(final Class<?> clzz, final Closure closure) throws Exception {
        List<Class<?>> clzzList = new ArrayList<Class<?>>() {{
            add(clzz);
        }};
        ensureThrows(clzzList, closure);
    }

    /**
     * @param clzzList a list of classes of the possible exception types
     * @param closure  closure containing code to be run that should throw an
     *                 exception
     */
    public void ensureThrows(final List<Class<?>> clzzList, final Closure closure) throws Exception {
        try {
            closure.call();
        } catch (Throwable e) {
            boolean caught = false;
            for (Class<?> clzz : clzzList) {
                if (clzz.isAssignableFrom(e.getClass()) || (e.getCause() != null
                        && e.getCause().getClass() == clzz)) {
                    caught = true;
                    break;
                }
            }
            if (!caught) {
                throw new VerificationException(
                        "exception caught (" + e.getClass().getName() + ") is not of type " + clzzList +
                                " or the cause isn't " + clzzList);
            }
            return;
        }
        throw new VerificationException("expected exception of type " + clzzList + " was not thrown");
    }

    /**
     * @param clzz    the class of the exception type expected
     * @param closure closure containing code to be run that should throw an
     *                exception
     */
    public void ensureStrictThrows(final Class<?> clzz, final Closure closure) throws Exception {
        List<Class<?>> clzzList = new ArrayList<Class<?>>() {{
            add(clzz);
        }};
        ensureStrictThrows(clzzList, closure);
    }

    /**
     * @param clzz    a list of classes of the possible exception types
     * @param closure closure containing code to be run that should throw an
     *                exception
     */
    public void ensureStrictThrows(final List<Class<?>> clzzList, final Closure closure) throws Exception {
        try {
            closure.call();
        } catch (Throwable e) {
            boolean caught = false;
            for (Class<?> clzz : clzzList) {
                if (clzz == e.getClass() || (e.getCause() != null
                        && e.getCause().getClass() == clzz)) {
                    caught = true;
                    break;
                }
            }
            if (!caught) {
                throw new VerificationException(
                        e.getClass().getName() + " was caught. The cause was "
                                + e.getCause().getClass() + " not " + clzzList
                                + " as specified.");
            }
            return;
        }
        throw new VerificationException("expected exception of type " + clzzList + " was not thrown");
    }

    /**
     * @param expression to be evaluated and should resolve to a boolean result
     */
    public void ensure(final boolean expression) throws Exception {
        if (!expression) {
            throw new VerificationException("the expression evaluated to false");
        }
    }

    /**
     * @param value
     * @param closure
     * @throws Exception
     */
    public void ensure(final Object value, final Closure closure)
            throws Exception {
        RichlyEnsurable delegate = this.getDelegate(value);
        closure.setDelegate(delegate);
        closure.call();
    }

    /**
     * @param closure closure containing code to be run that should throw a VerificationException
     */
    public void ensureFails(Closure closure) throws Exception {
        ensureThrows(VerificationException.class, closure);
    }

    /**
     * @return FlexibleDelegate instance
     */
    private RichlyEnsurable getDelegate(final Object value) throws Exception {
        RichlyEnsurable delegate = EnsurableFactory.manufacture();
        delegate.setVerified(value);
        return delegate;
    }

    /**
     * @param message
     */
    public void fail(String message) {
        throw new VerificationException(message);
    }

    /**
     * @param message
     * @param e
     */
    public void fail(String message, Exception e) {
        throw new VerificationException(message, e);
    }

    /**
     * @param message
     * @param expected
     * @param actual
     */
    public void fail(String message, Object expected, Object actual) {
        throw new VerificationException(message, expected, actual);
    }
}
