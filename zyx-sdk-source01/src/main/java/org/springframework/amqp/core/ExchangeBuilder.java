/*
 * Copyright 2016 the original author or authors.
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

import java.util.Map;

/**
 * Builder providing a fluent API for building {@link Exchange}s.
 *
 * @author Gary Russell
 * @since 1.6
 *
 */
public final class ExchangeBuilder extends AbstractBuilder {

	private final String name;

	private final String type;

	private boolean durable;

	private boolean autoDelete;

	private boolean internal;

	private boolean delayed;

	private ExchangeBuilder(String name, String type) {
		this.name = name;
		this.type = type;
	}

	/**
	 * Return a {@link DirectExchange} builder.
	 * @param name the name.
	 * @return the builder.
	 */
	public static ExchangeBuilder directExchange(String name) {
		return new ExchangeBuilder(name, ExchangeTypes.DIRECT);
	}

	/**
	 * Return a {@link TopicExchange} builder.
	 * @param name the name.
	 * @return the builder.
	 */
	public static ExchangeBuilder topicExchange(String name) {
		return new ExchangeBuilder(name, ExchangeTypes.TOPIC);
	}

	/**
	 * Return a {@link FanoutExchange} builder.
	 * @param name the name.
	 * @return the builder.
	 */
	public static ExchangeBuilder fanoutExchange(String name) {
		return new ExchangeBuilder(name, ExchangeTypes.FANOUT);
	}

	/**
	 * Return a {@link HeadersExchange} builder.
	 * @param name the name.
	 * @return the builder.
	 */
	public static ExchangeBuilder headersExchange(String name) {
		return new ExchangeBuilder(name, ExchangeTypes.HEADERS);
	}

	/**
	 * Set the auto delete flag.
	 * @return the builder.
	 */
	public ExchangeBuilder autoDelete() {
		this.autoDelete = true;
		return this;
	}

	/**
	 * Set the durable flag.
	 * @return the builder.
	 */
	public ExchangeBuilder durable() {
		this.durable = true;
		return this;
	}

	/**
	 * Add an argument.
	 * @param key the argument key.
	 * @param value the argument value.
	 * @return the builder.
	 */
	public ExchangeBuilder withArgument(String key, Object value) {
		getOrCreateArguments().put(key, value);
		return this;
	}

	/**
	 * Add the arguments.
	 * @param arguments the arguments map.
	 * @return the builder.
	 */
	public ExchangeBuilder withArguments(Map<String, Object> arguments) {
		this.getOrCreateArguments().putAll(arguments);
		return this;
	}

	/**
	 * Set the internal flag.
	 * @return the builder.
	 */
	public ExchangeBuilder internal() {
		this.internal = true;
		return this;
	}

	/**
	 * Set the delayed flag.
	 * @return the builder.
	 */
	public ExchangeBuilder delayed() {
		this.delayed = true;
		return this;
	}

	public Exchange build() {
		AbstractExchange exchange;
		if (ExchangeTypes.DIRECT.equals(this.type)) {
			exchange = new DirectExchange(this.name, this.durable, this.autoDelete, getArguments());
		}
		else if (ExchangeTypes.TOPIC.equals(this.type)) {
			exchange = new TopicExchange(this.name, this.durable, this.autoDelete, getArguments());
		}
		else if (ExchangeTypes.FANOUT.equals(this.type)) {
			exchange = new FanoutExchange(this.name, this.durable, this.autoDelete, getArguments());
		}
		else if (ExchangeTypes.HEADERS.equals(this.type)) {
			exchange = new HeadersExchange(this.name, this.durable, this.autoDelete, getArguments());
		}
		else {
			throw new IllegalStateException("Invalid type: " + this.type);
		}
		exchange.setInternal(this.internal);
		exchange.setDelayed(this.delayed);
		return exchange;
	}

}
