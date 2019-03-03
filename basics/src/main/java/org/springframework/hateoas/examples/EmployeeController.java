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

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Spring Web {@link RestController} used to generate a REST API. Works by injecting an {@link EmployeeRepository} and
 * an {@link EmployeeRepresentationModelAssembler} in the constructor, both of which are used to retrieve data from the
 * database, and assemble a REST resource.
 *
 * @author Greg Turnquist
 */
@RestController
class EmployeeController {

	private final EmployeeRepository repository;
	private final EmployeeRepresentationModelAssembler assembler;

	EmployeeController(EmployeeRepository repository, EmployeeRepresentationModelAssembler assembler) {

		this.repository = repository;
		this.assembler = assembler;
	}

	/**
	 * Look up all employees, and transform them into a REST collection resource using
	 * {@link EmployeeRepresentationModelAssembler#toCollectionModel(Iterable)}. Then return them through Spring Web's
	 * {@link ResponseEntity} fluent API.
	 */
	@GetMapping("/employees")
	public ResponseEntity<CollectionModel<EntityModel<Employee>>> findAll() {

		return ResponseEntity.ok( //
				this.assembler.toCollectionModel(this.repository.findAll()));

	}

	/**
	 * Look up a single {@link Employee} and transform it into a REST resource using
	 * {@link EmployeeRepresentationModelAssembler#toModel(Object)}. Then return it through Spring Web's
	 * {@link ResponseEntity} fluent API.
	 *
	 * @param id
	 */
	@GetMapping("/employees/{id}")
	public ResponseEntity<EntityModel<Employee>> findOne(@PathVariable long id) {

		return this.repository.findById(id) //
				.map(this.assembler::toModel) //
				.map(ResponseEntity::ok) //
				.orElse(ResponseEntity.notFound().build());
	}

}
