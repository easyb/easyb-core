package org.disco.easyb.core.delegates;

import groovy.lang.Closure;
import org.disco.easyb.core.exception.VerificationException;

/**
 * 
 * @author aglover
 * 
 */
public class EnsuringDelegate {
	/**
	 * 
	 * @param clzz
	 *            the class of the exception type expected
	 * @param closure
	 *            closure containing code to be run that should throw an
	 *            exception
	 * @throws Exception
	 */
	public void ensureThrows(final Class<?> clzz, final Closure closure)
			throws Exception {
		try {
			closure.call();
		} catch (Exception e) {
			if (!clzz.isAssignableFrom(e.getClass())) {
				throw new VerificationException(
						"the exception caught wasn't of type " + clzz);
			}
		}
	}

	/**
	 * 
	 * @param expression
	 *            to be evaluated and should resolve to a boolean result
	 * @throws Exception
	 */
	public void ensure(final boolean expression) throws Exception {
		if (!expression) {
			throw new VerificationException("the expression evaluated to false");
		}
	}

	/**
	 * 
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
	 * 
	 * @param value
	 * @return FlexibleDelegate instance
	 * @throws Exception
	 */
	private RichlyEnsurable getDelegate(final Object value) throws Exception {
		RichlyEnsurable delegate = EnsurableFactory.manufacture();
		delegate.setVerified(value);
		return delegate;
	}
	/**
	 * 
	 * @param message
	 */
	public void fail(String message) {
		throw new VerificationException(message);
	}
	/**
	 * 
	 * @param message
	 * @param e
	 */
	public void fail(String message, Exception e) {
		throw new VerificationException(message, e);
	}
	/**
	 * 
	 * @param message
	 * @param expected
	 * @param actual
	 */
	public void fail(String message, Object expected, Object actual) {
		throw new VerificationException(message, expected, actual);
	}
}
