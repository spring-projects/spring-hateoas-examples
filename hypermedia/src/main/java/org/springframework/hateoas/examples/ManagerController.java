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
 * @author Greg Turnquist
 */
@RestController
class ManagerController {

	private final ManagerRepository repository;
	private final ManagerResourceAssembler assembler;

	ManagerController(ManagerRepository repository, ManagerResourceAssembler assembler) {

		this.repository = repository;
		this.assembler = assembler;
	}

	/**
	 * Look up all managers, and transform them into a REST collection resource using
	 * {@link ManagerResourceAssembler#toResources(Iterable)}. Then return them through
	 * Spring Web's {@link ResponseEntity} fluent API.
	 *
	 * NOTE: cURL will fetch things as HAL JSON directly, but browsers issue a different
	 * default accept header, which allows XML to get requested first, so "produces"
	 * forces it to HAL JSON for all clients.
	 */
	@GetMapping(value = "/managers", produces = MediaTypes.HAL_JSON_VALUE)
	ResponseEntity<Resources<Resource<Manager>>> findAll() {
		return ResponseEntity.ok(
			assembler.toResources(repository.findAll()));

	}

	/**
	 * Look up a single {@link Manager} and transform it into a REST resource using
	 * {@link ManagerResourceAssembler#toResource(Object)}. Then return it through
	 * Spring Web's {@link ResponseEntity} fluent API.
	 *
	 * See {@link #findAll()} to explain {@link GetMapping}'s "produces" argument.
	 *
	 * @param id
	 */
	@GetMapping(value = "/managers/{id}", produces = MediaTypes.HAL_JSON_VALUE)
	ResponseEntity<Resource<Manager>> findOne(@PathVariable long id) {

		return repository.findById(id)
			.map(assembler::toResource)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	/**
	 * Find an {@link Employee}'s {@link Manager} based upon employee id. Turn it into a context-based link.
	 *
	 * @param id
	 * @return
	 */
	@GetMapping(value = "/employees/{id}/manager", produces = MediaTypes.HAL_JSON_VALUE)
	ResponseEntity<Resource<Manager>> findManager(@PathVariable long id) {
		return ResponseEntity.ok(
			assembler.toResource(repository.findByEmployeesId(id)));
	}
}
