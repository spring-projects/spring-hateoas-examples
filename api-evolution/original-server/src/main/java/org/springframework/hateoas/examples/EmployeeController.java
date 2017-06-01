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

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
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
	private final EmployeeResourceAssembler assembler;

	EmployeeController(EmployeeRepository repository, EmployeeResourceAssembler assembler) {

		this.repository = repository;
		this.assembler = assembler;
	}

	@GetMapping(value = "/", produces = MediaTypes.HAL_JSON_VALUE)
	public ResourceSupport root() {

		ResourceSupport rootResource = new ResourceSupport();

		rootResource.add(
			linkTo(methodOn(EmployeeController.class).root()).withSelfRel(),
			linkTo(methodOn(EmployeeController.class).findAll()).withRel("employees"));

		return rootResource;
	}

	@GetMapping(value = "/employees", produces = MediaTypes.HAL_JSON_VALUE)
	public Resources<Resource<Employee>> findAll() {
		return assembler.toResources(repository.findAll());
	}

	@PostMapping("/employees")
	public ResponseEntity<Resource<Employee>> newEmployee(@RequestBody Employee employee) {

		Employee savedEmployee = repository.save(employee);

		return ResponseEntity
			.created(linkTo(methodOn(EmployeeController.class).findOne(savedEmployee.getId())).toUri())
			.body(assembler.toResource(savedEmployee));
	}

	@GetMapping(value = "/employees/{id}", produces = MediaTypes.HAL_JSON_VALUE)
	public Resource<Employee> findOne(@PathVariable Long id) {
		return assembler.toResource(repository.findOne(id));
	}

}
