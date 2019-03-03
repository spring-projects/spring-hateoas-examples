/*
 * Copyright 2017 the original author or authors.
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
package org.springframework.hateoas.examples;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Greg Turnquist
 */
@Data
@Entity
@NoArgsConstructor
class Employee {

	@Id @GeneratedValue private Long id;
	private String name;
	private String role;

	/**
	 * To break the recursive, bi-directional relationship, don't serialize {@literal manager}.
	 */
	@JsonIgnore @OneToOne private Manager manager;

	Employee(String name, String role, Manager manager) {

		this.name = name;
		this.role = role;
		this.manager = manager;
	}

	public Optional<Long> getId() {
		return Optional.ofNullable(this.id);
	}
}
