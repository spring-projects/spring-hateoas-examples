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
import org.springframework.hateoas.SimpleResourceAssembler;
import org.springframework.stereotype.Component;

/**
 * @author Greg Turnquist
 */
@Component
class EmployeeWithManagerResourceAssembler implements SimpleResourceAssembler<EmployeeWithManager> {

	/**
	 * Define links to add to every individual {@link Resource}.
	 *
	 * @param resource
	 */
	@Override
	public void addLinks(Resource<EmployeeWithManager> resource) {

		resource.add(linkTo(methodOn(EmployeeController.class).findDetailedEmployee(resource.getContent().getId())).withSelfRel());
		resource.add(linkTo(methodOn(EmployeeController.class).findOne(resource.getContent().getId())).withRel("summary"));
		resource.add(linkTo(methodOn(EmployeeController.class).findAllDetailedEmployees()).withRel("detailedEmployees"));
	}

	/**
	 * Define links to add to the {@link Resources} collection.
	 *
	 * @param resources
	 */
	@Override
	public void addLinks(Resources<Resource<EmployeeWithManager>> resources) {

		resources.add(linkTo(methodOn(EmployeeController.class).findAllDetailedEmployees()).withSelfRel());
		resources.add(linkTo(methodOn(EmployeeController.class).findAll()).withRel("employees"));
		resources.add(linkTo(methodOn(ManagerController.class).findAll()).withRel("managers"));
		resources.add(linkTo(methodOn(RootController.class).root()).withRel("root"));
	}
}
