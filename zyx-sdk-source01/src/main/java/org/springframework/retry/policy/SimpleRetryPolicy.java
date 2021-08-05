/*
 * Copyright 2006-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.retry.policy;

import java.util.Collections;
import java.util.Map;

import org.springframework.classify.BinaryExceptionClassifier;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.context.RetryContextSupport;
import org.springframework.util.ClassUtils;

/**
 *
 * Simple retry policy that retries a fixed number of times for a set of named
 * exceptions (and subclasses). The number of attempts includes the initial try,
 * so e.g.
 *
 * <pre>
 * retryTemplate = new RetryTemplate(new SimpleRetryPolicy(3));
 * retryTemplate.execute(callback);
 * </pre>
 *
 * will execute the callback at least once, and as many as 3 times.
 *
 * @author Dave Syer
 * @author Rob Harrop
 * @author Gary Russell
 *
 */
public class SimpleRetryPolicy implements RetryPolicy {

	/**
	 * The default limit to the number of attempts for a new policy.
	 */
	public final static int DEFAULT_MAX_ATTEMPTS = 3;

	private volatile int maxAttempts;

	private BinaryExceptionClassifier retryableClassifier = new BinaryExceptionClassifier(false);

	/**
	 * Create a {@link SimpleRetryPolicy} with the default number of retry
	 * attempts.
	 */
	public SimpleRetryPolicy() {
		this(DEFAULT_MAX_ATTEMPTS, Collections
				.<Class<? extends Throwable>, Boolean> singletonMap(Exception.class, true));
	}

	/**
	 * Create a {@link SimpleRetryPolicy} with the specified number of retry
	 * attempts.
	 *
	 * @param maxAttempts the maximum number of attempts
	 * @param retryableExceptions the map of exceptions that are retryable
	 */
	public SimpleRetryPolicy(int maxAttempts, Map<Class<? extends Throwable>, Boolean> retryableExceptions) {
		this(maxAttempts, retryableExceptions, false);
	}

	/**
	 * Create a {@link SimpleRetryPolicy} with the specified number of retry
	 * attempts. If traverseCauses is true, the exception causes will be traversed until
	 * a match is found.
	 *
	 * @param maxAttempts the maximum number of attempts
	 * @param retryableExceptions the map of exceptions that are retryable
	 * @param traverseCauses is this clause traversable
	 */
	public SimpleRetryPolicy(int maxAttempts, Map<Class<? extends Throwable>, Boolean> retryableExceptions,
			boolean traverseCauses) {
		super();
		this.maxAttempts = maxAttempts;
		this.retryableClassifier = new BinaryExceptionClassifier(retryableExceptions);
		this.retryableClassifier.setTraverseCauses(traverseCauses);
	}

	/**
	 * Setter for retry attempts.
	 *
	 * @param retryAttempts the number of attempts before a retry becomes
	 * impossible.
	 */
	public void setMaxAttempts(int retryAttempts) {
		this.maxAttempts = retryAttempts;
	}

	/**
	 * The maximum number of retry attempts before failure.
	 *
	 * @return the maximum number of attempts
	 */
	public int getMaxAttempts() {
		return maxAttempts;
	}

	/**
	 * Test for retryable operation based on the status.
	 *
	 * @see org.springframework.retry.RetryPolicy#canRetry(org.springframework.retry.RetryContext)
	 *
	 * @return true if the last exception was retryable and the number of
	 * attempts so far is less than the limit.
	 */
	public boolean canRetry(RetryContext context) {
		Throwable t = context.getLastThrowable();
		return (t == null || retryForException(t)) && context.getRetryCount() < maxAttempts;
	}

	/**
	 * @see org.springframework.retry.RetryPolicy#close(RetryContext)
	 */
	public void close(RetryContext status) {
	}

	/**
	 * Update the status with another attempted retry and the latest exception.
	 *
	 * @see RetryPolicy#registerThrowable(RetryContext, Throwable)
	 */
	public void registerThrowable(RetryContext context, Throwable throwable) {
		SimpleRetryContext simpleContext = ((SimpleRetryContext) context);
		simpleContext.registerThrowable(throwable);
	}

	/**
	 * Get a status object that can be used to track the current operation
	 * according to this policy. Has to be aware of the latest exception and the
	 * number of attempts.
	 *
	 * @see org.springframework.retry.RetryPolicy#open(RetryContext)
	 */
	public RetryContext open(RetryContext parent) {
		return new SimpleRetryContext(parent);
	}

	@SuppressWarnings("serial")
	private static class SimpleRetryContext extends RetryContextSupport {
		public SimpleRetryContext(RetryContext parent) {
			super(parent);
		}
	}

	/**
	 * Delegates to an exception classifier.
	 *
	 * @param ex
	 * @return true if this exception or its ancestors have been registered as
	 * retryable.
	 */
	private boolean retryForException(Throwable ex) {
		return retryableClassifier.classify(ex);
	}

	@Override
	public String toString() {
		return ClassUtils.getShortName(getClass()) + "[maxAttempts=" + maxAttempts + "]";
	}
}
