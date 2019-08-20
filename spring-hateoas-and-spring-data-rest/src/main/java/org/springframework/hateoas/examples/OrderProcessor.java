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
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

/**
 * A {@link RepresentationModelProcessor} that takes an {@link Order} that has been wrapped by Spring Data REST into an
 * {@link EntityModel} and applies custom Spring HATEAOS-based {@link Link}s based on the state.
 *
 * @author Greg Turnquist
 */
@Component
public class OrderProcessor implements RepresentationModelProcessor<EntityModel<Order>> {

	private final RepositoryRestConfiguration configuration;

	public OrderProcessor(RepositoryRestConfiguration configuration) {
		this.configuration = configuration;
	}

	@Override
	public EntityModel<Order> process(EntityModel<Order> model) {

		CustomOrderController controller = methodOn(CustomOrderController.class);
		String basePath = configuration.getBasePath().toString();

		// If PAID_FOR is valid, add a link to the `pay()` method
		if (valid(model.getContent().getOrderStatus(), OrderStatus.PAID_FOR)) {
			model.add(applyBasePath( //
					linkTo(controller.pay(model.getContent().getId())) //
							.withRel(IanaLinkRelations.PAYMENT), //
					basePath));
		}

		// If CANCELLED is valid, add a link to the `cancel()` method
		if (valid(model.getContent().getOrderStatus(), OrderStatus.CANCELLED)) {
			model.add(applyBasePath( //
					linkTo(controller.cancel(model.getContent().getId())) //
							.withRel(LinkRelation.of("cancel")), //
					basePath));
		}

		// If FULFILLED is valid, add a link to the `fulfill()` method
		if (valid(model.getContent().getOrderStatus(), OrderStatus.FULFILLED)) {
			model.add(applyBasePath( //
					linkTo(controller.fulfill(model.getContent().getId())) //
							.withRel(LinkRelation.of("fulfill")), //
					basePath));
		}

		return model;
	}

	/**
	 * Adjust the {@link Link} such that it starts at {@literal basePath}.
	 *
	 * @param link - link presumably supplied via Spring HATEOAS
	 * @param basePath - base path provided by Spring Data REST
	 * @return new {@link Link} with these two values melded together
	 */
	private static Link applyBasePath(Link link, String basePath) {

		URI uri = link.toUri();

		URI newUri = null;
		try {
			newUri = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), //
					uri.getPort(), basePath + uri.getPath(), uri.getQuery(), uri.getFragment());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		return new Link(newUri.toString(), link.getRel());
	}
}
