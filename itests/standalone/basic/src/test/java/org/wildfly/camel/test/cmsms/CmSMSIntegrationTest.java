/*
 * #%L
 * Wildfly Camel :: Testsuite
 * %%
 * Copyright (C) 2013 - 2017 RedHat
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
package org.wildfly.camel.test.cmsms;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cm.client.SMSMessage;
import org.apache.camel.impl.DefaultCamelContext;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.camel.test.cmsms.subA.FakeCmSMSAPIServlet;
import org.wildfly.camel.test.common.http.HttpRequest;
import org.wildfly.extension.camel.CamelAware;

@CamelAware
@RunWith(Arquillian.class)
public class CmSMSIntegrationTest {

    private static final String FAKE_PRODUCT_TOKEN = "00000000-0000-0000-0000-000000000000";

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "camel-cm-sms-tests.war")
            .addClasses(FakeCmSMSAPIServlet.class, HttpRequest.class);
    }

    @Test
    public void testCmSMSComponent() throws Exception {
        // Force WildFly to generate a self-signed SSL cert & keystore
        HttpRequest.get("https://localhost:8443").throwExceptionOnFailure(false).getResponse();

        // Use the generated keystore
        String keystorePath = System.getProperty("jboss.server.config.dir") + "/application.keystore";
        System.setProperty("javax.net.ssl.trustStorePassword", "password");
        System.setProperty("javax.net.ssl.trustStore", keystorePath);

        CamelContext camelctx = new DefaultCamelContext();
        camelctx.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                .toF("cm-sms://localhost:8443/camel-cm-sms-tests/fake-cm-sms-api?productToken=%s&defaultFrom=wfc", FAKE_PRODUCT_TOKEN);
            }
        });

        camelctx.start();
        try {
            SMSMessage message = new SMSMessage("Kello Kermit", "+447000000000");
            ProducerTemplate template = camelctx.createProducerTemplate();

            // Send the SMS message to the fake API. Note: there's no useful payload response to assert success against
            // If no exception was thrown, we assume everything worked ok
            template.requestBody("direct:start", message);
        } finally {
            camelctx.stop();
            System.clearProperty("javax.net.ssl.trustStorePassword");
            System.clearProperty("javax.net.ssl.trustStore");
        }
    }
}