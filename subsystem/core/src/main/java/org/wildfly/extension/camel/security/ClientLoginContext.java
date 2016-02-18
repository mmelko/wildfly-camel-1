/*
 * #%L
 * Wildfly Camel :: Subsystem
 * %%
 * Copyright (C) 2013 - 2014 RedHat
 * %%
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
 * #L%
 */

package org.wildfly.extension.camel.security;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.wildfly.extension.camel.security.LoginContextBuilder.Type;

/**
 * Provides access RunAs Client login context
 *
 * @deprecated see {@link LoginContextBuilder}
 *
 * @author Thomas.Diesler@jboss.com
 * @since 08-May-2015
 */
@Deprecated
public final class ClientLoginContext {

    // Hide ctor
    private ClientLoginContext() {
    }

    public static LoginContext newLoginContext(final String username, final char[] password) throws LoginException {
        return new LoginContextBuilder(Type.CLIENT).username(username).password(password).build();
    }
}