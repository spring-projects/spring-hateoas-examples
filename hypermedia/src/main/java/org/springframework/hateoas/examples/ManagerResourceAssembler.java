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

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.SimpleIdentifiableResourceAssembler;
import org.springframework.stereotype.Component;

/**
 * @author Greg Turnquist
 */
@Component
class ManagerResourceAssembler extends SimpleIdentifiableResourceAssembler<Manager> {

	ManagerResourceAssembler() {
		super(ManagerController.class);
	}

	/**
	 * Retain default links provided by {@link SimpleIdentifiableResourceAssembler}, but add extra ones to each {@link Manager}.
	 *
	 * @param resource
	 */
	@Override
	protected void addLinks(Resource<Manager> resource) {
		/**
		 * Retain default links.
		 */
		super.addLinks(resource);

		// Add custom link to find all managed employees
		resource.add(linkTo(methodOn(EmployeeController.class).findEmployees(resource.getContent().getId())).withRel("employees"));
	}

	/**
	 * Retain default links for the entire collection, but add extra custom links for the {@link Manager} collection.
	 *
	 * @param resources
	 */
	@Override
	protected void addLinks(Resources<Resource<Manager>> resources) {

		super.addLinks(resources);

		resources.add(linkTo(methodOn(EmployeeController.class).findAll()).withRel("employees"));
		resources.add(linkTo(methodOn(EmployeeController.class).findAllDetailedEmployees()).withRel("detailedEmployees"));
		resources.add(linkTo(methodOn(RootController.class).root()).withRel("root"));
	}
}
