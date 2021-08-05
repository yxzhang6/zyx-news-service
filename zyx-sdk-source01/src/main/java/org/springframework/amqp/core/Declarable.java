/*
 * Copyright 2002-2016 the original author or authors.
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

package org.springframework.amqp.core;

import java.util.Collection;

/**
 * Classes implementing this interface can be auto-declared
 * with the broker during context initialization by an {@code AmqpAdmin}.
 * Registration can be limited to specific {@code AmqpAdmin}s.
 *
 * @author Gary Russell
 * @since 1.2
 *
 */
public interface Declarable {

	/**
	 * Whether or not this object should be automatically declared
	 * by any {@code AmqpAdmin}.
	 * @return true if the object should be declared.
	 */
	boolean shouldDeclare();

	/**
	 * The collection of {@code AmqpAdmin}s that should declare this
	 * object; if empty, all admins should declare.
	 * @return the collection.
	 */
	Collection<?> getDeclaringAdmins();

	/**
	 * Should ignore exceptions (such as mismatched args) when declaring.
	 * @return true if should ignore.
	 * @since 1.6
	 */
	boolean isIgnoreDeclarationExceptions();

}
