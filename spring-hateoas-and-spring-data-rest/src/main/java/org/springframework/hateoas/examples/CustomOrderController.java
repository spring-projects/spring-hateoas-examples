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

import static org.springframework.hateoas.examples.OrderStatus.*;

import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author Greg Turnquist
 */
@BasePathAwareController
public class CustomOrderController {

	private final OrderRepository repository;

	public CustomOrderController(OrderRepository repository) {
		this.repository = repository;
	}

	@PostMapping("/orders/{id}/pay")
	ResponseEntity<?> pay(@PathVariable Long id) {

		Order order = this.repository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

		if (valid(order.getOrderStatus(), OrderStatus.PAID_FOR)) {

			order.setOrderStatus(OrderStatus.PAID_FOR);
			return ResponseEntity.ok(repository.save(order));
		}

		return ResponseEntity.badRequest()
				.body("Transitioning from " + order.getOrderStatus() + " to " + OrderStatus.PAID_FOR + " is not valid.");
	}

	@PostMapping("/orders/{id}/cancel")
	ResponseEntity<?> cancel(@PathVariable Long id) {

		Order order = this.repository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

		if (valid(order.getOrderStatus(), OrderStatus.CANCELLED)) {

			order.setOrderStatus(OrderStatus.CANCELLED);
			return ResponseEntity.ok(repository.save(order));
		}

		return ResponseEntity.badRequest()
				.body("Transitioning from " + order.getOrderStatus() + " to " + OrderStatus.CANCELLED + " is not valid.");
	}

	@PostMapping("/orders/{id}/fulfill")
	ResponseEntity<?> fulfill(@PathVariable Long id) {

		Order order = this.repository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

		if (valid(order.getOrderStatus(), OrderStatus.FULFILLED)) {

			order.setOrderStatus(OrderStatus.FULFILLED);
			return ResponseEntity.ok(repository.save(order));
		}

		return ResponseEntity.badRequest()
				.body("Transitioning from " + order.getOrderStatus() + " to " + OrderStatus.FULFILLED + " is not valid.");
	}
}
