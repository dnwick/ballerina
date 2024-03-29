/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.ballerina.core.runtime.dispatching;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.ballerina.core.exception.BallerinaException;
import org.wso2.ballerina.core.interpreter.Context;
import org.wso2.ballerina.core.model.Annotation;
import org.wso2.ballerina.core.model.Resource;
import org.wso2.ballerina.core.model.Service;
import org.wso2.ballerina.core.runtime.dispatching.uri.QueryParamProcessor;
import org.wso2.ballerina.core.runtime.dispatching.uri.URITemplate;
import org.wso2.ballerina.core.runtime.dispatching.uri.URITemplateException;
import org.wso2.carbon.messaging.CarbonCallback;
import org.wso2.carbon.messaging.CarbonMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * Resource level dispatching handler for HTTP protocol.
 */
public class HTTPResourceDispatcher implements ResourceDispatcher {

    private static final Logger log = LoggerFactory.getLogger(HTTPResourceDispatcher.class);

    @Override
    public Resource findResource(Service service, CarbonMessage cMsg, CarbonCallback callback, Context balContext)
            throws BallerinaException {

        String method = (String) cMsg.getProperty(Constants.HTTP_METHOD);
        String subPath = (String) cMsg.getProperty(Constants.SUB_PATH);

        try {
            for (Resource resource : service.getResources()) {
                Annotation subPathAnnotation = resource.getAnnotation(Constants.ANNOTATION_NAME_PATH);
                String subPathAnnotationVal;
                if (subPathAnnotation != null) {
                    subPathAnnotationVal = subPathAnnotation.getValue();
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("Path not specified in the Resource, using default sub path");
                    }
                    subPathAnnotationVal = Constants.DEFAULT_SUB_PATH;
                }

                Map<String, String> resourceArgumentValues = new HashMap<>();
                //to enable dispatching with query params products/{productId}?regID={regID}
                String queryStr = cMsg.getProperty(Constants.QUERY_STR) != null
                                  ? "?" + cMsg.getProperty(Constants.QUERY_STR)
                                  : "";
                if ((matches(subPathAnnotationVal, (subPath + queryStr), resourceArgumentValues) ||
                        Constants.DEFAULT_SUB_PATH.equals(subPathAnnotationVal))
                        && (resource.getAnnotation(method) != null)) {

                    if (cMsg.getProperty(Constants.QUERY_STR) != null) {
                        QueryParamProcessor.processQueryParams
                                ((String) cMsg.getProperty(Constants.QUERY_STR))
                                .forEach((resourceArgumentValues::put));
                    }
                    cMsg.setProperty(org.wso2.ballerina.core.runtime.Constants.RESOURCE_ARGS, resourceArgumentValues);
                    return resource;
                }
            }
        } catch (Throwable e) {
            throw new BallerinaException(e.getMessage(), balContext);
        }

        // Throw an exception if the resource is not found.
        throw new BallerinaException("no matching resource found for Path : " + subPath + " , Method : " + method);
    }

    public static boolean matches(String uriTemplate, String reqPath,
                                  Map<String, String> variables) throws URITemplateException {
        URITemplate template = new URITemplate(uriTemplate);
        return template.matches(reqPath, variables);

    }

    @Override
    public String getProtocol() {
        return Constants.PROTOCOL_HTTP;
    }
}
