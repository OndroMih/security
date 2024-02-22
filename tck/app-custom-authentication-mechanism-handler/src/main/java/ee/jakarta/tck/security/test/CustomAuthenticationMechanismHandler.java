/*
 * Copyright (c) 2024 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */
package ee.jakarta.tck.security.test;

import static jakarta.interceptor.Interceptor.Priority.APPLICATION;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;
import jakarta.security.enterprise.AuthenticationException;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanismHandler;
import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * This HttpAuthenticationMechanismHandler overrides the default provided one and delegates
 * requests to two individual authentication mechanisms depending on the request path.
 */
@Alternative
@Priority(APPLICATION)
@ApplicationScoped
public class CustomAuthenticationMechanismHandler implements HttpAuthenticationMechanismHandler {

    @Inject
    TestAuthenticationMechanism1 authenticationMechanism1;

    @Inject
    TestAuthenticationMechanism2 authenticationMechanism2;

    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest request, HttpServletResponse response,
            HttpMessageContext httpMessageContext) throws AuthenticationException {

        if (getRequestRelativeURI(request).startsWith("/protectedServlet1")) {
            return authenticationMechanism1.validateRequest(request, response, httpMessageContext);
        }

        return authenticationMechanism2.validateRequest(request, response, httpMessageContext);
    }

    public static String getRequestRelativeURI(HttpServletRequest request) {
        return request.getRequestURI().substring(request.getContextPath().length());
    }

}
