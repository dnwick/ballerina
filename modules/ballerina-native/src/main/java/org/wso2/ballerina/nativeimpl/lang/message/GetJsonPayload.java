/**
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * <p>
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 **/

package org.wso2.ballerina.nativeimpl.lang.message;

import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import org.wso2.ballerina.core.interpreter.Context;
import org.wso2.ballerina.core.model.types.TypeEnum;
import org.wso2.ballerina.core.model.values.BJSON;
import org.wso2.ballerina.core.model.values.BMessage;
import org.wso2.ballerina.core.model.values.BValue;
import org.wso2.ballerina.core.nativeimpl.AbstractNativeFunction;
import org.wso2.ballerina.core.nativeimpl.annotations.Argument;
import org.wso2.ballerina.core.nativeimpl.annotations.BallerinaFunction;
import org.wso2.ballerina.core.nativeimpl.annotations.ReturnType;
import org.wso2.ballerina.nativeimpl.lang.utils.ErrorHandler;
import org.wso2.carbon.messaging.MessageDataSource;

/**
 *  Get the payload of the Message as a JSON.
 */
@BallerinaFunction(
        packageName = "ballerina.lang.message",
        functionName = "getJsonPayload",
        args = {@Argument(name = "message", type = TypeEnum.MESSAGE)},
        returnType = {@ReturnType(type = TypeEnum.JSON)},
        isPublic = true
)
public class GetJsonPayload extends AbstractNativeFunction {

    private static final String OPERATION = "get json payload";

    @Override
    public BValue[] execute(Context ctx) {
        BJSON result = null;
        try {
            // Accessing First Parameter Value.
            BMessage msg = (BMessage) getArgument(ctx, 0);

            if (msg.isAlreadyRead()) {
                MessageDataSource payload = msg.getMessageDataSource();
                if (payload instanceof BJSON) {
                    result = (BJSON) payload;
                } else {
                    // else, build the JSON from the string representation of the payload.
                    result = new BJSON(msg.getMessageDataSource().getMessageAsString());
                }
            } else {
                result = new BJSON(msg.value().getInputStream());
                msg.setMessageDataSource(result);
                msg.setAlreadyRead(true);
            }
        } catch (JsonSyntaxException e) {
            ErrorHandler.handleMalformedJson(OPERATION, e);
        } catch (JsonParseException e) {
            ErrorHandler.handleJsonException(OPERATION, e);
        } catch (Throwable e) {
            ErrorHandler.handleJsonException(OPERATION, e);
        }

        // Setting output value.
        return getBValues(result);
    }
}
