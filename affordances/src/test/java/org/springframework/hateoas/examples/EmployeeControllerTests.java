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

import static org.hamcrest.CoreMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

/**
 * @author Greg Turnquist
 */
@RunWith(SpringRunner.class)
@WebMvcTest(EmployeeController.class)
@Import({ HypermediaConfiguration.class })
public class EmployeeControllerTests {

	@Autowired private MockMvc mvc;

	@MockBean private EmployeeRepository repository;

	@Test
	public void getAllShouldFetchAHalFormsEmbeddedDocument() throws Exception {

		given(repository.findAll()).willReturn(Arrays.asList( //
				new Employee(1L, "Frodo", "Baggins", "ring bearer"), //
				new Employee(2L, "Bilbo", "Baggins", "burglar")));

		mvc.perform(get("/employees").accept(MediaTypes.HAL_FORMS_JSON_VALUE)) //
				.andDo(print()) //
				.andExpect(status().isOk()) //
				.andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_FORMS_JSON_VALUE))

				.andExpect(jsonPath("$._embedded.employees[0].id", is(1)))
				.andExpect(jsonPath("$._embedded.employees[0].firstName", is("Frodo")))
				.andExpect(jsonPath("$._embedded.employees[0].lastName", is("Baggins")))
				.andExpect(jsonPath("$._embedded.employees[0].role", is("ring bearer")))
				.andExpect(jsonPath("$._embedded.employees[0]._templates.default.method", is("put")))
				.andExpect(jsonPath("$._embedded.employees[0]._templates.default.properties[0].name", is("firstName")))
				.andExpect(jsonPath("$._embedded.employees[0]._templates.default.properties[1].name", is("id")))
				.andExpect(jsonPath("$._embedded.employees[0]._templates.default.properties[2].name", is("lastName")))
				.andExpect(jsonPath("$._embedded.employees[0]._templates.default.properties[3].name", is("role")))
				.andExpect(jsonPath("$._embedded.employees[0]._links.self.href", is("http://localhost/employees/1")))
				.andExpect(jsonPath("$._embedded.employees[0]._links.employees.href", is("http://localhost/employees")))

				.andExpect(jsonPath("$._embedded.employees[1].id", is(2)))
				.andExpect(jsonPath("$._embedded.employees[1].firstName", is("Bilbo")))
				.andExpect(jsonPath("$._embedded.employees[1].lastName", is("Baggins")))
				.andExpect(jsonPath("$._embedded.employees[1].role", is("burglar")))
				.andExpect(jsonPath("$._embedded.employees[1]._templates.default.method", is("put")))
				.andExpect(jsonPath("$._embedded.employees[1]._templates.default.properties[0].name", is("firstName")))
				.andExpect(jsonPath("$._embedded.employees[1]._templates.default.properties[1].name", is("id")))
				.andExpect(jsonPath("$._embedded.employees[1]._templates.default.properties[2].name", is("lastName")))
				.andExpect(jsonPath("$._embedded.employees[1]._templates.default.properties[3].name", is("role")))
				.andExpect(jsonPath("$._embedded.employees[1]._links.self.href", is("http://localhost/employees/2")))
				.andExpect(jsonPath("$._embedded.employees[1]._links.employees.href", is("http://localhost/employees")))

				.andExpect(jsonPath("$._templates.default.method", is("post")))
				.andExpect(jsonPath("$._templates.default.properties[0].name", is("firstName")))
				.andExpect(jsonPath("$._templates.default.properties[1].name", is("id")))
				.andExpect(jsonPath("$._templates.default.properties[2].name", is("lastName")))
				.andExpect(jsonPath("$._templates.default.properties[3].name", is("role")))

				.andExpect(jsonPath("$._links.self.href", is("http://localhost/employees")));
	}

	@Test
	public void getOneShouldFetchASingleHalFormsDocument() throws Exception {

		given(repository.findById(any())).willReturn(Optional.of(new Employee(1L, "Frodo", "Baggins", "ring bearer")));

		mvc.perform(get("/employees/1").accept(MediaTypes.HAL_FORMS_JSON_VALUE)) //
				.andDo(print()) //
				.andExpect(status().isOk()) //
				.andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_FORMS_JSON_VALUE))

				.andExpect(jsonPath("$.id", is(1))) //
				.andExpect(jsonPath("$.firstName", is("Frodo"))) //
				.andExpect(jsonPath("$.lastName", is("Baggins"))) //
				.andExpect(jsonPath("$.role", is("ring bearer")))

				.andExpect(jsonPath("$._templates.default.method", is("put")))
				.andExpect(jsonPath("$._templates.default.properties[0].name", is("firstName")))
				.andExpect(jsonPath("$._templates.default.properties[1].name", is("id")))
				.andExpect(jsonPath("$._templates.default.properties[2].name", is("lastName")))
				.andExpect(jsonPath("$._templates.default.properties[3].name", is("role")))

				.andExpect(jsonPath("$._links.self.href", is("http://localhost/employees/1")))
				.andExpect(jsonPath("$._links.employees.href", is("http://localhost/employees")));
	}
}
