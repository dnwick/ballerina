/*
 * Copyright (c) 2017, WSO2 Inc. (http://wso2.com) All Rights Reserved.
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
 */
package org.wso2.ballerina.core.model;

import org.wso2.ballerina.core.model.builder.CallableUnitBuilder;
import org.wso2.ballerina.core.model.statements.BlockStmt;
import org.wso2.ballerina.core.model.symbols.BLangSymbol;
import org.wso2.ballerina.core.model.types.BType;

import java.util.HashMap;
import java.util.Map;

/**
 * {@code BTypeConvertor} represents a  TypeConvertor in ballerina.
 *
 * @since 0.8.0
 */
public class BTypeConvertor implements TypeConvertor, SymbolScope, CompilationUnit {
    private NodeLocation location;

    // BLangSymbol related attributes
    protected String name;
    protected String pkgPath;
    protected boolean isPublic;
    protected SymbolName symbolName;

    private Annotation[] annotations;
    private ParameterDef[] parameterDefs;
    private BType[] parameterTypes;
    private ParameterDef[] returnParams;
    private BType[] returnParamTypes;
    private BlockStmt typeConverterBody;
    private int stackFrameSize;

    // Scope related variables
    private SymbolScope enclosingScope;
    private Map<SymbolName, BLangSymbol> symbolMap;

    private BTypeConvertor(SymbolScope enclosingScope) {
        this.enclosingScope = enclosingScope;
        this.symbolMap = new HashMap<>();
    }

    @Override
    public void setSymbolName(SymbolName symbolName) {
        this.symbolName = symbolName;
    }

    /**
     * Get all the Annotations associated with a BallerinatypeConverter
     *
     * @return list of Annotations
     */
    public Annotation[] getAnnotations() {
        return annotations;
    }

    /**
     * Get list of Arguments associated with the typeConvertor definition
     *
     * @return list of Arguments
     */
    public ParameterDef[] getParameterDefs() {
        return parameterDefs;
    }

    /**
     * Get all the variableDcls declared in the scope of BallerinaTypeConvertor
     *
     * @return list of all BallerinaTypeConvertor scoped variableDcls
     */
    public VariableDef[] getVariableDefs() {
        return null;
    }

    public ParameterDef[] getReturnParameters() {
        return returnParams;
    }

    @Override
    public String getTypeConverterName() {
        return null;
    }

    public int getStackFrameSize() {
        return stackFrameSize;
    }

    public void setStackFrameSize(int stackFrameSize) {
        this.stackFrameSize = stackFrameSize;
    }

    @Override
    public BlockStmt getCallableUnitBody() {
        return this.typeConverterBody;
    }

    @Override
    public BType[] getReturnParamTypes() {
        return returnParamTypes;
    }

    @Override
    public void setReturnParamTypes(BType[] returnParamTypes) {
        this.returnParamTypes = returnParamTypes;
    }

    @Override
    public BType[] getArgumentTypes() {
        return parameterTypes;
    }

    @Override
    public void setParameterTypes(BType[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    // Methods in Node interface

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public NodeLocation getNodeLocation() {
        return location;
    }


    // Methods in BLangSymbol interface

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPackagePath() {
        return pkgPath;
    }

    @Override
    public boolean isPublic() {
        return isPublic;
    }

    @Override
    public boolean isNative() {
        return false;
    }

    @Override
    public SymbolName getSymbolName() {
        return symbolName;
    }

    @Override
    public SymbolScope getSymbolScope() {
        return this;
    }


    // Methods in the SymbolScope interface

    @Override
    public ScopeName getScopeName() {
        return ScopeName.LOCAL;
    }

    @Override
    public SymbolScope getEnclosingScope() {
        return enclosingScope;
    }

    @Override
    public void define(SymbolName name, BLangSymbol symbol) {
        symbolMap.put(name, symbol);
    }

    @Override
    public BLangSymbol resolve(SymbolName name) {
        return resolve(symbolMap, name);
    }

    /**
     * {@code BTypeConvertorBuilder} is responsible for building a {@cdoe BTypeConvertor} node.
     *
     * @since 0.8.0
     */
    public static class BTypeConvertorBuilder extends CallableUnitBuilder {
        private BTypeConvertor bTypeCon;

        public BTypeConvertorBuilder(SymbolScope enclosingScope) {
            bTypeCon = new BTypeConvertor(enclosingScope);
            currentScope = bTypeCon;
        }

        public BTypeConvertor buildTypeConverter() {
            bTypeCon.location = this.location;
            bTypeCon.name = this.name;
            bTypeCon.pkgPath = this.pkgPath;

            bTypeCon.annotations = this.annotationList.toArray(new Annotation[this.annotationList.size()]);
            bTypeCon.parameterDefs = this.parameterDefList.toArray(new ParameterDef[this.parameterDefList.size()]);
            bTypeCon.returnParams = this.returnParamList.toArray(new ParameterDef[this.returnParamList.size()]);
            bTypeCon.typeConverterBody = this.body;
            return bTypeCon;
        }
    }
}
