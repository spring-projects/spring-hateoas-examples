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

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.client.Traverson;
import org.springframework.hateoas.server.core.TypeReferences.CollectionModelType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

/**
 * A web controller that serves up client data found on a remote REST service.
 *
 * @author Greg Turnquist
 */
@Controller
public class HomeController {

	private static final String REMOTE_SERVICE_ROOT_URI = "http://localhost:9000";

	private final RestTemplate rest;

	public HomeController(RestTemplate restTemplate) {
		this.rest = restTemplate;
	}

	/**
	 * Get a listing of ALL {@link Employee}s by querying the remote services' root URI, and then "hopping" to the
	 * {@literal employees} rel. NOTE: Also create a form-backed {@link Employee} object to allow creating a new entry
	 * with the Thymeleaf template.
	 *
	 * @param model
	 * @return
	 * @throws URISyntaxException
	 */
	@GetMapping
	public String index(Model model) throws URISyntaxException {

		Traverson client = new Traverson(new URI(REMOTE_SERVICE_ROOT_URI), MediaTypes.HAL_JSON);
		CollectionModel<EntityModel<Employee>> employees = client //
				.follow("employees") //
				.toObject(new CollectionModelType<EntityModel<Employee>>() {});

		model.addAttribute("employee", new Employee());
		model.addAttribute("employees", employees);

		return "index";
	}

	/**
	 * Instead of putting the creation link from the remote service in the template (a security concern), have a local
	 * route for {@literal POST} requests. Gather up the information, and form a remote call, using {@link Traverson} to
	 * fetch the {@literal employees} {@link Link}. Once a new employee is created, redirect back to the root URL.
	 *
	 * @param employee
	 * @return
	 * @throws URISyntaxException
	 */
	@PostMapping("/employees")
	public String newEmployee(@ModelAttribute Employee employee) throws URISyntaxException {

		Traverson client = new Traverson(new URI(REMOTE_SERVICE_ROOT_URI), MediaTypes.HAL_JSON);
		Link employeesLink = client //
				.follow("employees") //
				.asLink();

		this.rest.postForEntity(employeesLink.expand().getHref(), employee, Employee.class);

		return "redirect:/";
	}
}
