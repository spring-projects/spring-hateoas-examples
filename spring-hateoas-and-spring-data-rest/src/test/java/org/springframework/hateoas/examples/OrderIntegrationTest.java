/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.hateoas.examples;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * @author Greg Turnquist
 */
@SpringBootTest()
@AutoConfigureMockMvc
public class OrderIntegrationTest {

	@Autowired MockMvc mvc;

	@Test
	void basics() throws Exception {

		// Core operations provided by Spring Data REST

		this.mvc.perform(get("/api")) //
				.andDo(print()) //
				.andExpect(status().isOk()) //
				.andExpect(content().contentType(MediaTypes.HAL_JSON)) //
				.andExpect(jsonPath("$._links.orders.href", is("http://localhost/api/orders")))
				.andExpect(jsonPath("$._links.profile.href", is("http://localhost/api/profile")));

		this.mvc.perform(get("/api/orders")).andDo(print()) //
				.andExpect(status().isOk()) //
				.andExpect(content().contentType(MediaTypes.HAL_JSON)) //
				.andExpect(jsonPath("$._embedded.orders[0].orderStatus", is("BEING_CREATED")))
				.andExpect(jsonPath("$._embedded.orders[0].description", is("grande mocha")))
				.andExpect(jsonPath("$._embedded.orders[0]._links.self.href", is("http://localhost/api/orders/1")))
				.andExpect(jsonPath("$._embedded.orders[0]._links.order.href", is("http://localhost/api/orders/1")))
				.andExpect(jsonPath("$._embedded.orders[0]._links.payment.href", is("http://localhost/api/orders/1/pay")))
				.andExpect(jsonPath("$._embedded.orders[0]._links.cancel.href", is("http://localhost/api/orders/1/cancel")))
				.andExpect(jsonPath("$._embedded.orders[1].orderStatus", is("BEING_CREATED")))
				.andExpect(jsonPath("$._embedded.orders[1].description", is("venti hazelnut machiatto")))
				.andExpect(jsonPath("$._embedded.orders[1]._links.self.href", is("http://localhost/api/orders/2")))
				.andExpect(jsonPath("$._embedded.orders[1]._links.order.href", is("http://localhost/api/orders/2")))
				.andExpect(jsonPath("$._embedded.orders[1]._links.payment.href", is("http://localhost/api/orders/2/pay")))
				.andExpect(jsonPath("$._embedded.orders[1]._links.cancel.href", is("http://localhost/api/orders/2/cancel")))
				.andExpect(jsonPath("$._links.self.href", is("http://localhost/api/orders")))
				.andExpect(jsonPath("$._links.profile.href", is("http://localhost/api/profile/orders")));

		// Fulfilling an unpaid-for order should fail.

		this.mvc.perform(post("/api/orders/1/fulfill")) //
				.andDo(print()) //
				.andExpect(status().is4xxClientError()) //
				.andExpect(content().contentType(MediaType.APPLICATION_JSON)) //
				.andExpect(content().string("\"Transitioning from BEING_CREATED to FULFILLED is not valid.\""));

		// Pay for the order.

		this.mvc.perform(post("/api/orders/1/pay")) //
				.andDo(print()) //
				.andExpect(status().isOk()) //
				.andExpect(content().contentType(MediaType.APPLICATION_JSON)) //
				.andExpect(jsonPath("$.id", is(1))) //
				.andExpect(jsonPath("$.orderStatus", is("PAID_FOR")));

		// Paying for an already paid-for order should fail.

		this.mvc.perform(post("/api/orders/1/pay")) //
				.andDo(print()) //
				.andExpect(status().is4xxClientError()) //
				.andExpect(content().contentType(MediaType.APPLICATION_JSON)) //
				.andExpect(content().string("\"Transitioning from PAID_FOR to PAID_FOR is not valid.\""));

		// Cancelling a paid-for order should fail.

		this.mvc.perform(post("/api/orders/1/cancel")) //
				.andDo(print()) //
				.andExpect(status().is4xxClientError()) //
				.andExpect(content().contentType(MediaType.APPLICATION_JSON)) //
				.andExpect(content().string("\"Transitioning from PAID_FOR to CANCELLED is not valid.\""));

		// Verify a paid-for order now shows links to fulfill.

		this.mvc.perform(get("/api/orders/1")) //
				.andDo(print()) //
				.andExpect(status().isOk()) //
				.andExpect(content().contentType(MediaTypes.HAL_JSON)) //
				.andExpect(jsonPath("$.orderStatus", is("PAID_FOR"))) //
				.andExpect(jsonPath("$.description", is("grande mocha"))) //
				.andExpect(jsonPath("$._links.self.href", is("http://localhost/api/orders/1")))
				.andExpect(jsonPath("$._links.order.href", is("http://localhost/api/orders/1")))
				.andExpect(jsonPath("$._links.fulfill.href", is("http://localhost/api/orders/1/fulfill")));

		// Fulfill the order.

		this.mvc.perform(post("/api/orders/1/fulfill")) //
				.andDo(print()) //
				.andExpect(status().isOk()) //
				.andExpect(content().contentType(MediaType.APPLICATION_JSON)) //
				.andExpect(jsonPath("$.orderStatus", is("FULFILLED"))) //
				.andExpect(jsonPath("$.description", is("grande mocha")));

		// Cancelling a fulfilled order should fail.

		this.mvc.perform(post("/api/orders/1/cancel")) //
				.andDo(print()) //
				.andExpect(status().is4xxClientError()) //
				.andExpect(content().contentType(MediaType.APPLICATION_JSON)) //
				.andExpect(content().string("\"Transitioning from FULFILLED to CANCELLED is not valid.\""));

		// Cancel an order.
		
		this.mvc.perform(post("/api/orders/2/cancel")) //
				.andDo(print()) //
				.andExpect(status().isOk()) //
				.andExpect(content().contentType(MediaType.APPLICATION_JSON)) //
				.andExpect(jsonPath("$.orderStatus", is("CANCELLED"))) //
				.andExpect(jsonPath("$.description", is("venti hazelnut machiatto")));
	}

}
