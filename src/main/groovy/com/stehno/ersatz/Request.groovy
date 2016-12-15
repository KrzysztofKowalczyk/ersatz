/*
 * Copyright (C) 2016 Christopher J. Stehno
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stehno.ersatz

import groovy.transform.CompileStatic

import java.util.function.Consumer
import java.util.function.Function

/**
 * Configuration interface for HTTP request expectations.
 */
@CompileStatic
interface Request {

    /**
     * Specifies a request header to be configured in the expected request.
     *
     * @param name the header name
     * @param value the header value
     * @return this request
     */
    Request header(final String name, final String value)

    /**
     * Specifies request headers as a Map of names to values to be configured in the expected request.
     *
     * @param heads the map of headers
     * @return this request
     */
    Request headers(final Map<String, String> heads)

    /**
     * Used to retrieve a specific configured request header from the configuration.
     *
     * @param name the header name
     * @return the header value
     */
    String getHeader(final String name)

    /**
     * Used to specify a request query parameter to be configured in the expected request. As per the HTTP spec, the query string parameters may be
     * specified multiple times with different values to denote a parameter with multiple values.
     *
     * @param name the parameter name
     * @param value the parameter value
     * @return this request
     */
    Request query(final String name, final String value)

    /**
     * Used to specify a map of request query parameters for configuration on the expected request.
     *
     * @param map the map of query parameters
     * @return this request
     */
    Request queries(final Map<String, List<String>> map)

    /**
     * Retrieves the list of query parameters configured with the specified parameter name.
     *
     * @param name the parameter name
     * @return the list of values associated with the parameter (or an empty list if there are none)
     */
    List<String> getQuery(final String name)

    /**
     * Specifies a request cookie to be configured with the given name and value.
     *
     * @param name the cookie name
     * @param value the cookie value
     * @return this request
     */
    Request cookie(final String name, final String value)

    /**
     * Used to configure a map of cookies on the request.
     *
     * @param cookies the map of cookies
     * @return this request
     */
    Request cookies(final Map<String, String> cookies)

    /**
     * Retrieves the value of the specified cookie, or <code>null</code> if the cookie is not present.
     *
     * @param name the cookie name
     * @return the cookie value
     */
    String getCookie(final String name)

    /**
     * Retrieves the configured request path.
     *
     * @return the request path
     */
    String getPath()

    /**
     * Retrieves the configured request method for the request.
     *
     * @return the configured request method
     */
    String getMethod()

    /**
     * Specifies a listener which will be called with the active request whenever this request is matched at test-time.
     *
     * @param listener the request call listener
     * @return a reference to this request
     */
    Request listener(final Consumer<Request> listener)

    /**
     * Allows the specification of a custom call verifier so that the number of times the request is called may be matched. See the
     * <code>Verifiers</code> class for available implementations.
     *
     * @param verifier the verifier to be used
     * @return a reference to this request
     */
    Request verifier(final Function<Integer, Boolean> verifier)

    /**
     * Initiates the definition of a response for this request.
     *
     * @return a response for this request, for configuration
     */
    Response responds()

    /**
     * Allows for configuration of a <code>Response</code> by the given <code>Consumer</code>, which will have a <code>Response</code> object passed
     * into it.
     *
     * @param responder the <code>Consumer<Response></code> to provide configuration of the response
     * @return a reference to this request
     */
    Request responder(final Consumer<Response> responder)

    /**
     * Allows for configuration of a <code>Response</code> by the given Groovy <code>Closure</code>, which will delegate to a <code>Response</code>
     * instance passed into it for configuration using the Groovy DSL.
     *
     * @param closure the <code>Consumer<Response></code> to provide configuration of the response
     * @return a reference to this request
     */
    Request responder(@DelegatesTo(Response) final Closure closure)

    /**
     * Allows for additional configuration of request matching criteria. The provided <code>Function<ClientRequest,Boolean></code> will have the
     * active request passed into it and the function will return a value of <code>true</code> if the condition is met. The standard matching
     * criteria will not be applied if conditions are applied; however, they may be added back in using the <code>Conditions</code> functions.
     *
     * Multiple additional conditions may be applied.
     *
     * @param matcher the matching function to be added.
     * @return a reference to this request
     */
    Request condition(final Function<ClientRequest, Boolean> matcher)

    /**
     * Allows multiple request matching conditions to be applied to the configuration. See <code>condition(Function<ClientRequest,Boolean>)</code> for
     * more details.
     *
     * @param matchers the list of condition matchers to be applied
     * @return this request
     */
    Request conditions(List<Function<ClientRequest, Boolean>> matchers)
}