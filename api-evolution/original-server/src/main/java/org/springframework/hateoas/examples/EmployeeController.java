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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
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

	@GetMapping("/")
	public RepresentationModel root() {

		RepresentationModel rootResource = new RepresentationModel();

		rootResource.add( //
				linkTo(methodOn(EmployeeController.class).root()).withSelfRel(), //
				linkTo(methodOn(EmployeeController.class).findAll()).withRel("employees"));

		return rootResource;
	}

	@GetMapping("/employees")
	public CollectionModel<EntityModel<Employee>> findAll() {
		return assembler.toCollectionModel(repository.findAll());
	}

	@PostMapping("/employees")
	public ResponseEntity<EntityModel<Employee>> newEmployee(@RequestBody Employee employee) {

		Employee savedEmployee = repository.save(employee);

		return ResponseEntity //
				.created(savedEmployee.getId() //
						.map(id -> linkTo(methodOn(EmployeeController.class).findOne(id)).toUri()) //
						.orElseThrow(() -> new RuntimeException("Failed to create for some reason"))) //
				.body(assembler.toModel(savedEmployee));
	}

	@GetMapping("/employees/{id}")
	public EntityModel<Employee> findOne(@PathVariable Long id) {
		return repository.findById(id) //
				.map(assembler::toModel) //
				.orElseThrow(() -> new RuntimeException("No employee '" + id + "' found"));
	}

}
