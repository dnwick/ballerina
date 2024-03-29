/*
*  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/
package org.wso2.ballerina.core.model.expressions;

import org.wso2.ballerina.core.model.Function;
import org.wso2.ballerina.core.model.NodeExecutor;
import org.wso2.ballerina.core.model.NodeLocation;
import org.wso2.ballerina.core.model.NodeVisitor;
import org.wso2.ballerina.core.model.types.BType;
import org.wso2.ballerina.core.model.values.BValue;

/**
 * {@code FunctionInvocationExpr} represents function invocation expression.
 *
 * @since 0.8.0
 */
public class FunctionInvocationExpr extends AbstractExpression implements CallableUnitInvocationExpr<Function> {
    private String name;
    private String pkgName;
    private String pkgPath;
    private Expression[] exprs;
    private Function calleeFunction;
    private BType[] types = new BType[0];

    public FunctionInvocationExpr(NodeLocation location,
                                  String name,
                                  String pkgName,
                                  String pkgPath,
                                  Expression[] exprs) {
        super(location);
        this.name = name;
        this.pkgName = pkgName;
        this.pkgPath = pkgPath;
        this.exprs = exprs;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPackageName() {
        return pkgName;
    }

    @Override
    public String getPackagePath() {
        return pkgPath;
    }

    @Override
    public Expression[] getArgExprs() {
        return exprs;
    }

    @Override
    public Function getCallableUnit() {
        return calleeFunction;
    }

    @Override
    public void setCallableUnit(Function callableUnit) {
        this.calleeFunction = callableUnit;
    }

    @Override
    public BType[] getTypes() {
        return this.types;
    }

    @Override
    public void setTypes(BType[] types) {
        this.types = types;

        multipleReturnsAvailable = types.length > 1;
        if (!multipleReturnsAvailable && types.length == 1) {
            this.type = types[0];
        }
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public BValue[] executeMultiReturn(NodeExecutor executor) {
        return executor.visit(this);
    }

    @Override
    public BValue execute(NodeExecutor executor) {
        BValue[] values = executor.visit(this);

        if (calleeFunction.getReturnParamTypes().length == 0) {
            return null;
        }

        return values[0];
    }
}
