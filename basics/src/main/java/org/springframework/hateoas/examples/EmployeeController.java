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

import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Spring Web {@link RestController} used to generate a REST API.
 *
 * Works by injecting an {@link EmployeeRepository} and an {@link EmployeeResourceAssembler} in the constructor, both
 * of which are used to retrieve data from the database, and assemble a REST resource.
 *
 * @author Greg Turnquist
 */
@RestController
public class EmployeeController {

	private final EmployeeRepository repository;
	private final EmployeeResourceAssembler assembler;

	public EmployeeController(EmployeeRepository repository,
							  EmployeeResourceAssembler assembler) {
		
		this.repository = repository;
		this.assembler = assembler;
	}

	/**
	 * Look up all employees, and transform them into a REST collection resource using
	 * {@link EmployeeResourceAssembler#toResources(Iterable)}. Then return them through
	 * Spring Web's {@link ResponseEntity} fluent API.
	 *
	 * NOTE: cURL will fetch things as HAL JSON directly, but browsers issue a different
	 * default accept header, which allows XML to get requested first, so "produces"
	 * forces it to HAL JSON for all clients.
	 */
	@GetMapping(value = "/employees", produces = MediaTypes.HAL_JSON_VALUE)
	public ResponseEntity<Resources<Resource<Employee>>> findAll() {
		return ResponseEntity.ok(
			assembler.toResources(repository.findAll()));

	}

	/**
	 * Look up a single {@link Employee} and transform into a REST resource using
	 * {@link EmployeeResourceAssembler#toResource(Object)}. Then return it through
	 * Spring Web's {@link ResponseEntity} fluent API.
	 *
	 * See {@link #findAll()} to explain "produces".
	 * 
	 * @param id
	 */
	@GetMapping(value = "/employees/{id}", produces = MediaTypes.HAL_JSON_VALUE)
	public ResponseEntity<Resource<Employee>> findOne(@PathVariable String id) {
		return ResponseEntity.ok(
			assembler.toResource(repository.findOne(Long.valueOf(id))));
	}

}
