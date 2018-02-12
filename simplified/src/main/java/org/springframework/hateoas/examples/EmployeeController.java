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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Spring Web {@link RestController} used to generate a REST API.
 *
 * @author Greg Turnquist
 */
@RestController
class EmployeeController {

	private final EmployeeRepository repository;

	EmployeeController(EmployeeRepository repository) {
		this.repository = repository;
	}

	/**
	 * Look up all employees, and transform them into a REST collection resource.
	 * Then return them through Spring Web's {@link ResponseEntity} fluent API.
	 *
	 * NOTE: cURL will fetch things as HAL JSON directly, but browsers issue a different
	 * default accept header, which allows XML to get requested first, so "produces"
	 * forces it to HAL JSON for all clients.
	 */
	@GetMapping(value = "/employees", produces = MediaTypes.HAL_JSON_VALUE)
	ResponseEntity<Resources<Resource<Employee>>> findAll() {

		List<Resource<Employee>> employees = StreamSupport.stream(repository.findAll().spliterator(), false)
			.map(employee -> new Resource<>(employee,
				linkTo(methodOn(EmployeeController.class).findOne(employee.getId())).withSelfRel(),
				linkTo(methodOn(EmployeeController.class).findAll()).withRel("employees")))
			.collect(Collectors.toList());

		return ResponseEntity.ok(
			new Resources<>(employees,
				linkTo(methodOn(EmployeeController.class).findAll()).withSelfRel()));
	}

	@PostMapping("/employees")
	ResponseEntity<?> newEmployee(@RequestBody Employee employee) {

		try {
			Employee savedEmployee = repository.save(employee);

			Resource<Employee> employeeResource = new Resource<>(savedEmployee,
				linkTo(methodOn(EmployeeController.class).findOne(savedEmployee.getId())).withSelfRel());

			return ResponseEntity
				.created(new URI(employeeResource.getRequiredLink(Link.REL_SELF).getHref()))
				.body(employeeResource);
		} catch (URISyntaxException e) {
			return ResponseEntity.badRequest().body("Unable to create " + employee);
		}
	}

	/**
	 * Look up a single {@link Employee} and transform it into a REST resource. Then return it through
	 * Spring Web's {@link ResponseEntity} fluent API.
	 *
	 * See {@link #findAll()} to explain {@link GetMapping}'s "produces" argument.
	 *
	 * @param id
	 */
	@GetMapping(value = "/employees/{id}", produces = MediaTypes.HAL_JSON_VALUE)
	ResponseEntity<Resource<Employee>> findOne(@PathVariable long id) {

		return repository.findById(id)
			.map(employee -> new Resource<>(employee,
				linkTo(methodOn(EmployeeController.class).findOne(employee.getId())).withSelfRel(),
				linkTo(methodOn(EmployeeController.class).findAll()).withRel("employees")))
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	/**
	 * Update existing employee then return a Location header.
	 * 
	 * @param employee
	 * @param id
	 * @return
	 */
	@PutMapping("/employees/{id}")
	ResponseEntity<?> updateEmployee(@RequestBody Employee employee, @PathVariable long id) {

		Employee employeeToUpdate = employee;
		employeeToUpdate.setId(id);
		repository.save(employeeToUpdate);

		Link newlyCreatedLink = linkTo(methodOn(EmployeeController.class).findOne(id)).withSelfRel();

		try {
			return ResponseEntity.noContent()
				.location(new URI(newlyCreatedLink.getHref()))
				.build();
		} catch (URISyntaxException e) {
			return ResponseEntity.badRequest().body("Unable to update " + employeeToUpdate);
		}
	}

}
