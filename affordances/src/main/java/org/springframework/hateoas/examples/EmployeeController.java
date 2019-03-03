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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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

	EmployeeController(EmployeeRepository repository) {
		this.repository = repository;
	}

	@GetMapping("/employees")
	ResponseEntity<CollectionModel<EntityModel<Employee>>> findAll() {

		List<EntityModel<Employee>> employeeResources = StreamSupport.stream(repository.findAll().spliterator(), false)
				.map(employee -> new EntityModel<>(employee,
						linkTo(methodOn(EmployeeController.class).findOne(employee.getId())).withSelfRel()
								.andAffordance(afford(methodOn(EmployeeController.class).updateEmployee(null, employee.getId())))
								.andAffordance(afford(methodOn(EmployeeController.class).deleteEmployee(employee.getId()))),
						linkTo(methodOn(EmployeeController.class).findAll()).withRel("employees")))
				.collect(Collectors.toList());

		return ResponseEntity.ok(new CollectionModel<>( //
				employeeResources, //
				linkTo(methodOn(EmployeeController.class).findAll()).withSelfRel()
						.andAffordance(afford(methodOn(EmployeeController.class).newEmployee(null)))));
	}

	@PostMapping("/employees")
	ResponseEntity<?> newEmployee(@RequestBody Employee employee) {

		Employee savedEmployee = repository.save(employee);

		return new EntityModel<>(savedEmployee,
				linkTo(methodOn(EmployeeController.class).findOne(savedEmployee.getId())).withSelfRel()
						.andAffordance(afford(methodOn(EmployeeController.class).updateEmployee(null, savedEmployee.getId())))
						.andAffordance(afford(methodOn(EmployeeController.class).deleteEmployee(savedEmployee.getId()))),
				linkTo(methodOn(EmployeeController.class).findAll()).withRel("employees")).getLink(IanaLinkRelations.SELF)
						.map(Link::getHref) //
						.map(href -> {
							try {
								return new URI(href);
							} catch (URISyntaxException e) {
								throw new RuntimeException(e);
							}
						}) //
						.map(uri -> ResponseEntity.noContent().location(uri).build())
						.orElse(ResponseEntity.badRequest().body("Unable to create " + employee));
	}

	@GetMapping("/employees/{id}")
	ResponseEntity<EntityModel<Employee>> findOne(@PathVariable long id) {

		return repository.findById(id)
				.map(employee -> new EntityModel<>(employee,
						linkTo(methodOn(EmployeeController.class).findOne(employee.getId())).withSelfRel()
								.andAffordance(afford(methodOn(EmployeeController.class).updateEmployee(null, employee.getId())))
								.andAffordance(afford(methodOn(EmployeeController.class).deleteEmployee(employee.getId()))),
						linkTo(methodOn(EmployeeController.class).findAll()).withRel("employees")))
				.map(ResponseEntity::ok) //
				.orElse(ResponseEntity.notFound().build());
	}

	@PutMapping("/employees/{id}")
	ResponseEntity<?> updateEmployee(@RequestBody Employee employee, @PathVariable long id) {

		Employee employeeToUpdate = employee;
		employeeToUpdate.setId(id);

		Employee updatedEmployee = repository.save(employeeToUpdate);

		return new EntityModel<>(updatedEmployee,
				linkTo(methodOn(EmployeeController.class).findOne(updatedEmployee.getId())).withSelfRel()
						.andAffordance(afford(methodOn(EmployeeController.class).updateEmployee(null, updatedEmployee.getId())))
						.andAffordance(afford(methodOn(EmployeeController.class).deleteEmployee(updatedEmployee.getId()))),
				linkTo(methodOn(EmployeeController.class).findAll()).withRel("employees")).getLink(IanaLinkRelations.SELF)
						.map(Link::getHref).map(href -> {
							try {
								return new URI(href);
							} catch (URISyntaxException e) {
								throw new RuntimeException(e);
							}
						}) //
						.map(uri -> ResponseEntity.noContent().location(uri).build()) //
						.orElse(ResponseEntity.badRequest().body("Unable to update " + employeeToUpdate));
	}

	@DeleteMapping("/employees/{id}")
	ResponseEntity<?> deleteEmployee(@PathVariable long id) {

		repository.deleteById(id);

		return ResponseEntity.noContent().build();
	}
}
