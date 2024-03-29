/*
*  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/
package org.wso2.ballerina.test.service.http.sample;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.ballerina.test.IntegrationTestCase;
import org.wso2.ballerina.test.util.HttpClientRequest;
import org.wso2.ballerina.test.util.HttpResponse;
import org.wso2.ballerina.test.util.TestConstant;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Testing the Routing service sample located in
 * ballerina_home/samples/routingServices/routingServices.bal.
 */
public class RoutingServiceSampleTestCase extends IntegrationTestCase {
    private final String requestNyseMessage = "{\"name\":\"nyse\"}";
    private final String responseNyseMessage = "{\"exchange\":\"nyse\",\"name\":\"IBM\",\"value\":\"127.50\"}";
    private final String requestNasdaqMessage = "{\"name\":\"nasdaq\"}";
    private final String responseNasdaqMessage = "{\"exchange\":\"nasdaq\",\"name\":\"IBM\",\"value\":\"127.50\"}";

    @Test(description = "Test Content base routing sample")
    public void testContentBaseRouting() throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put(TestConstant.HEADER_CONTENT_TYPE, TestConstant.CONTENT_TYPE_JSON);
        //sending nyse as name
        HttpResponse response = HttpClientRequest.doPost(getServiceURLHttp("cbr"), requestNyseMessage, headers);
        Assert.assertEquals(response.getResponseCode(), 200, "Response code mismatched");
        Assert.assertEquals(response.getHeaders().get(TestConstant.HEADER_CONTENT_TYPE)
                , TestConstant.CONTENT_TYPE_JSON, "Content-Type mismatched");
        Assert.assertEquals(response.getData(), responseNyseMessage, "Message content mismatched. " +
                                                                     "Routing failed for nyse");

        //sending nasdaq as name
        response = HttpClientRequest.doPost(getServiceURLHttp("cbr"), requestNasdaqMessage, headers);
        Assert.assertEquals(response.getResponseCode(), 200, "Response code mismatched");
        Assert.assertEquals(response.getHeaders().get(TestConstant.HEADER_CONTENT_TYPE)
                , TestConstant.CONTENT_TYPE_JSON, "Content-Type mismatched");
        Assert.assertEquals(response.getData(), responseNasdaqMessage, "Message content mismatched. " +
                                                                       "Routing failed for nasdaq");
    }

    @Test(description = "Test Header base routing sample")
    public void testHeaderBaseRouting() throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put(TestConstant.HEADER_CONTENT_TYPE, TestConstant.CONTENT_TYPE_JSON);
        //sending nyse as name header
        headers.put("name", "nyse");
        HttpResponse response = HttpClientRequest.doGet(getServiceURLHttp("hbr"), headers);
        Assert.assertEquals(response.getResponseCode(), 200, "Response code mismatched");
        Assert.assertEquals(response.getHeaders().get(TestConstant.HEADER_CONTENT_TYPE)
                , TestConstant.CONTENT_TYPE_JSON, "Content-Type mismatched");
        Assert.assertEquals(response.getData(), responseNyseMessage
                , "Message content mismatched. Routing failed for nyse");

        //sending nasdaq as http header
        headers.put("name", "nasdaq");
        response = HttpClientRequest.doGet(getServiceURLHttp("hbr"), headers);
        Assert.assertEquals(response.getResponseCode(), 200, "Response code mismatched");
        Assert.assertEquals(response.getHeaders().get(TestConstant.HEADER_CONTENT_TYPE)
                , TestConstant.CONTENT_TYPE_JSON, "Content-Type mismatched");
        Assert.assertEquals(response.getData(), responseNasdaqMessage
                , "Message content mismatched. Routing failed for nasdaq");
    }
}
