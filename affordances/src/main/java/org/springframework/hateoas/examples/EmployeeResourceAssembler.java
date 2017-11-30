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
class EmployeeResourceAssembler extends SimpleIdentifiableResourceAssembler<Employee> {

	/**
	 * Link the {@link Employee} domain type to the {@link EmployeeController} using this
	 * {@link SimpleIdentifiableResourceAssembler} in order to generate both {@link org.springframework.hateoas.Resource}
	 * and {@link org.springframework.hateoas.Resources}.
	 */
	EmployeeResourceAssembler() {
		super(EmployeeController.class);
	}

	/**
	 * Define links to add to every {@link Resource}.
	 *
	 * @param resource
	 */
	@Override
	protected void addLinks(Resource<Employee> resource) {

		resource.getContent().getId()
			.ifPresent(id -> {
				resource.add(getCollectionLinkBuilder().slash(resource.getContent()).withSelfRel()
					.andAffordance(afford(methodOn(EmployeeController.class).updateEmployee(null, id))));
			});

		resource.add(getCollectionLinkBuilder().withRel(this.getRelProvider().getCollectionResourceRelFor(this.getResourceType())));
	}

	/**
	 * Define links to add to {@link Resources} collection.
	 *
	 * @param resources
	 */
	@Override
	protected void addLinks(Resources<Resource<Employee>> resources) {
		resources.add(getCollectionLinkBuilder().withSelfRel()
			.andAffordance(afford(methodOn(EmployeeController.class).newEmployee(null))));
	}
}
