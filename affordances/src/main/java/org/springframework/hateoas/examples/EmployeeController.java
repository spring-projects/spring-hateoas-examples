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

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

	@GetMapping("/employees")
	ResponseEntity<Resources<Resource<Employee>>> findAll() {
		return ResponseEntity.ok(
			assembler.toResources(repository.findAll()));
	}

	@GetMapping("/employees/{id}")
	ResponseEntity<Resource<Employee>> findOne(@PathVariable long id) {

		return repository.findById(id)
			.map(assembler::toResource)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping("/employees")
	ResponseEntity<?> newEmployee(@RequestBody Employee employee) {

		return repository.save(employee).getId()
			.map(this::findOne)
			.map(HttpEntity::getBody)
			.flatMap(ResourceSupport::getId)
			.map(Link::getHref)
			.map(href -> {
				try {
					return new URI(href);
				} catch (URISyntaxException e) {
					throw new RuntimeException(e);
				}
			})
			.map(uri -> ResponseEntity.noContent().location(uri).build())
			.orElse(ResponseEntity.badRequest().body("Unable to create " + employee));
	}

	@PutMapping("/employees/{id}")
	ResponseEntity<?> updateEmployee(@RequestBody Employee employee, @PathVariable long id) {

		Employee employeeToUpdate = employee;
		employeeToUpdate.setId(id);

		return repository.save(employeeToUpdate).getId()
			.map(this::findOne)
			.map(HttpEntity::getBody)
			.flatMap(ResourceSupport::getId)
			.map(Link::getHref)
			.map(href -> {
				try {
					return new URI(href);
				} catch (URISyntaxException e) {
					throw new RuntimeException(e);
				}
			})
			.map(uri -> ResponseEntity.noContent().location(uri).build())
			.orElse(ResponseEntity.badRequest().body("Unable to update " + employeeToUpdate));
	}
}
