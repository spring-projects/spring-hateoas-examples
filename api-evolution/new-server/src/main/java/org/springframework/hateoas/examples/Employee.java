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

import java.util.Arrays;
import java.util.Optional;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.springframework.util.StringUtils;

/**
 * An updated domain object where {@literal name} has been replaced by {@literal firstName} and {@literal} lastName. To
 * easy migration, we need to support the old {@literal name} field with a getter and a setter.
 * 
 * @author Greg Turnquist
 */
@Data
@NoArgsConstructor
@Entity
class Employee {

	@Id @GeneratedValue private Long id;
	private String firstName;
	private String lastName;
	private String role;

	Employee(String firstName, String lastName, String role) {

		this.firstName = firstName;
		this.lastName = lastName;
		this.role = role;
	}

	public Optional<Long> getId() {
		return Optional.ofNullable(this.id);
	}

	/**
	 * Just merge {@literal firstName} and {@literal lastName} together.
	 *
	 * @return
	 */
	public String getName() {
		return this.firstName + " " + this.lastName;
	}

	/**
	 * Split things up, and assign the first token to {@literal firstName} with everything else to {@literal lastName}.
	 *
	 * @param wholeName
	 */
	public void setName(String wholeName) {

		String[] parts = wholeName.split(" ");
		this.firstName = parts[0];
		if (parts.length > 1) {
			this.lastName = StringUtils.arrayToDelimitedString(Arrays.copyOfRange(parts, 1, parts.length), " ");
		} else {
			this.lastName = "";
		}
	}
}
