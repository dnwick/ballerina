/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.ballerina.nativeimpl.net.http;

import org.wso2.ballerina.core.exception.BallerinaException;
import org.wso2.ballerina.core.interpreter.Context;
import org.wso2.ballerina.core.model.types.TypeEnum;
import org.wso2.ballerina.core.model.values.BInteger;
import org.wso2.ballerina.core.model.values.BMessage;
import org.wso2.ballerina.core.model.values.BValue;
import org.wso2.ballerina.core.nativeimpl.AbstractNativeFunction;
import org.wso2.ballerina.core.nativeimpl.annotations.Argument;
import org.wso2.ballerina.core.nativeimpl.annotations.BallerinaFunction;
import org.wso2.ballerina.core.nativeimpl.annotations.ReturnType;
import org.wso2.ballerina.nativeimpl.connectors.http.Constants;

/**
 * Get HTTP StatusCode from the message.
 */
@BallerinaFunction(
        packageName = "ballerina.net.http",
        functionName = "getStatusCode",
        args = {@Argument(name = "message", type = TypeEnum.MESSAGE)},
        returnType = {@ReturnType(type = TypeEnum.INT)},
        isPublic = true
)
public class GetStatusCode extends AbstractNativeFunction {
    public BValue[] execute(Context ctx) {
        int statusCode = -1;
        BMessage bMsg = (BMessage) getArgument(ctx, 0);
        String statusCodeStr = String.valueOf(bMsg.value().getProperty(Constants.HTTP_STATUS_CODE));

        try {
            statusCode = Integer.parseInt(statusCodeStr);
        } catch (NumberFormatException e) {
            throw new BallerinaException("Invalid status code found");
        }
        return getBValues(new BInteger(statusCode));
    }
}
