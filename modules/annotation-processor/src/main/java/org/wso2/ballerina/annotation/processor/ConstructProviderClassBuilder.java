/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.ballerina.annotation.processor;

import org.wso2.ballerina.core.exception.BallerinaException;
import org.wso2.ballerina.core.model.NativeUnit;
import org.wso2.ballerina.core.model.SymbolName;
import org.wso2.ballerina.core.model.SymbolScope;
import org.wso2.ballerina.core.model.types.SimpleTypeName;
import org.wso2.ballerina.core.model.types.TypeEnum;
import org.wso2.ballerina.core.nativeimpl.NativeConstructLoader;
import org.wso2.ballerina.core.nativeimpl.NativeUnitProxy;
import org.wso2.ballerina.core.nativeimpl.annotations.Argument;
import org.wso2.ballerina.core.nativeimpl.annotations.BallerinaAction;
import org.wso2.ballerina.core.nativeimpl.annotations.BallerinaConnector;
import org.wso2.ballerina.core.nativeimpl.annotations.ReturnType;
import org.wso2.ballerina.core.nativeimpl.connectors.AbstractNativeConnector;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;

/**
 * Builder class to generate the ballerina constructs provider class.
 * The class generated by this builder will register all the annotated 
 * classes as {@link NativeUnitProxy}s to the global symbol table, via java SPI.
 * 
 * TODO: Refactor this class ASAP
 */
public class ConstructProviderClassBuilder {
    
    private static final String SERVICES = "services" + File.separator;
    private static final String META_INF = "META-INF" + File.separator;
    private static final String GLOBAL_SCOPE = "globalScope";
    private Writer sourceFileWriter;
    private String className;
    private String packageName;
    private String nativeUnitClassName = NativeUnit.class.getSimpleName();
    private String symbolNameClassName = SymbolName.class.getSimpleName();
    private String nativeProxyClassName = NativeUnitProxy.class.getSimpleName();
    
    private final String importPkg = "import " + SymbolScope.class.getCanonicalName() + ";\n" + 
                             "import " + NativeUnitProxy.class.getCanonicalName() + ";\n" + 
                             "import " + SymbolName.class.getCanonicalName() + ";\n" + 
                             "import " + NativeConstructLoader.class.getCanonicalName() + ";\n" +
                             "import " + SimpleTypeName.class.getCanonicalName() + ";\n" +
                             "import " + AbstractNativeConnector.class.getCanonicalName() + ";\n" +
                             "import " + NativeUnit.class.getCanonicalName() + ";\n\n";
    
    private String symbolNameStr = "new %s(\"%s\",\"%s\")";
    
    private String getsuplierInsertionStr(String nativeUnitVarName, String classVarName) {
        String supplierInsertStr = "%s.define(%s,%n" +
                "  new %s(() -> {%n" +
                "      %s " + nativeUnitVarName + " = null;%n" +
                "      try {%n" +
                "          Class " + classVarName + " = Class.forName(\"%s\");%n" +
                "          " + nativeUnitVarName + " = ((%s) " + classVarName + 
                ".getConstructor(%s).newInstance(%s));%n" +
                "          " + nativeUnitVarName + ".setName(\"%s\");%n" +
                "          " + nativeUnitVarName + ".setPackagePath(\"%s\");%n" +
                "          " + nativeUnitVarName + ".setArgTypeNames(%s);%n" +
                "          " + nativeUnitVarName + ".setReturnParamTypeNames(%s);%n" +
                "          " + nativeUnitVarName + ".setStackFrameSize(%s);%n" +
                "          " + nativeUnitVarName + ".setSymbolName(%s);%n" +
                "          %s" +
                "      } catch (Exception ignore) {%n" +
                "      } finally {%n" +
                "          return " + nativeUnitVarName + ";%n" +
                "      }%n" +
                "  })%n" +
                ");%n%n";
        
        return supplierInsertStr;
    }

    /**
     * Create a construct provider builder.
     * 
     * @param filer {@link Filer} of the current processing environment
     * @param packageName Package name of the generated construct provider class
     * @param className Class name of the generated construct provider class
     */
    public ConstructProviderClassBuilder(Filer filer, String packageName, String className) {
        this.packageName = packageName;
        this.className = className;
        
        // Initialize the class writer. 
        initClassWriter(filer);
        
        // Create config file int META-INF/services directory
        createServiceMetaFile(filer);
    }
    
    /**
     * Initialize the class writer. Write static codes of the including:
     * <ul>
     * <li>The package name</li>
     * <li>Package imports</li>
     * <li>Class definition</li>
     * <li>Public constructor with no parameters</li>
     * <li>Method name for load() method</li>
     * </ul>
     * @param filer
     */
    private void initClassWriter(Filer filer) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("package " + packageName + ";\n\n");
        stringBuilder.append(importPkg);
        stringBuilder.append("public class " + className + 
                " implements " + NativeConstructLoader.class.getSimpleName() + " {\n\n");
        stringBuilder.append("public " + className + "() {}\n\n");
        stringBuilder.append("public void load(" + SymbolScope.class.getSimpleName() + " globalScope) {\n");
        
        try {
            JavaFileObject javaFile = filer.createSourceFile(packageName + "." + className);
            sourceFileWriter = javaFile.openWriter();
            sourceFileWriter.write(stringBuilder.toString());
        } catch (IOException e) {
            throw new BallerinaException("failed to initialize source generator: " + e.getMessage());
        }
    }
    
    /**
     * Add a native construct to this class builder.
     * 
     * @param constructName Native construct Name
     * @param className Name of the implementation class
     * @param packageName
     * @param returnTypes
     * @param arguments
     */
    public void addNativeConstruct(String packageName, String constructName, String constructQualifiedName, 
            String className, Argument[] arguments, ReturnType[] returnTypes, int stackFrameSize) {
        String functionSupplier = getConstructInsertStr(GLOBAL_SCOPE, packageName, constructName, 
            constructQualifiedName, null, null, className, arguments, returnTypes, stackFrameSize, 
            "nativeCallableUnit", null, nativeUnitClassName, "nativeUnitClass", null, null);
        try {
            sourceFileWriter.write(functionSupplier);
        } catch (IOException e) {
            throw new BallerinaException("failed to write to source file: " + e.getMessage());
        }
    }
    
    private String getConstructInsertStr(String scope, String constructPkgName, String constructName, 
            String constructQualifiedName, String constructArgType, String constructArg, String className, 
            Argument[] arguments, ReturnType[] returnTypes, int stackFrameSize, String constructVarName, 
            String scopeElements, String nativeUnitClassName, String nativeUnitCLassVarName, String enclosingScopeName,
            String enclosingScopePkg) {
        String createSymbolStr = String.format(symbolNameStr, symbolNameClassName, constructQualifiedName, 
            constructPkgName);
        String retrunTypesArrayStr = getReturnTypes(returnTypes);
        String argsTypesArrayStr = getArgTypes(arguments, enclosingScopeName, enclosingScopePkg);
        String supplierInsertStr = getsuplierInsertionStr(constructVarName, nativeUnitCLassVarName);
        if (scopeElements == null) {
            scopeElements = "";
        }
        if (constructArgType == null) {
            constructArgType = "";
        }
        if (constructArg == null) {
            constructArg = "";
        }
        return String.format(supplierInsertStr, scope, createSymbolStr, nativeProxyClassName, 
            nativeUnitClassName, className, nativeUnitClassName, constructArgType, constructArg, constructName, 
            constructPkgName, argsTypesArrayStr, retrunTypesArrayStr, stackFrameSize, createSymbolStr, scopeElements);
    }
    
    
    /**
     * Get the return types array construction string.
     * 
     * @param returnTypes Array of return types
     * @return Return types array construction string
     */
    private String getReturnTypes(ReturnType[] returnTypes) {
        String simpleTypeNameClass = SimpleTypeName.class.getSimpleName();
        StringBuilder sb = new StringBuilder("new " + simpleTypeNameClass + "[]{");
        if (returnTypes != null) {
            int returnCount = 0;
            for (ReturnType returnType : returnTypes) {
                String bType;
                boolean isArray = false;
                // For non-array types.
                if (!returnType.type().equals(TypeEnum.ARRAY)) {
                    bType = returnType.type().getName();
                } else {
                    isArray = true;
                    bType = returnType.elementType().getName();
                }
                sb.append("new " + simpleTypeNameClass + "(\"" + bType + "\", " + isArray + ")");
                if (returnCount != returnTypes.length - 1) {
                    sb.append(",");
                }
                returnCount++;
            }
        }
        sb.append("}");
        return sb.toString();
    }
    
    /**
     * Get the argument types array construction string.
     * 
     * @param arguments Array of arguments
     * @param enclosingScopePkg 
     * @param enclosingScopeName 
     * @return Argument types array construction string
     */
    private String getArgTypes(Argument[] arguments, String enclosingScopeName, String enclosingScopePkg) {
        String simpleTypeNameClass = SimpleTypeName.class.getSimpleName();
        StringBuilder sb = new StringBuilder("new " + simpleTypeNameClass + "[]{");
        if (arguments != null) {
            int argCount = 0;
            for (Argument argType : arguments) {
                TypeEnum bType;
                boolean isArray = false;
                // For non-array types.
                if (!argType.type().equals(TypeEnum.ARRAY)) {
                    bType = argType.type();
                } else {
                    isArray = true;
                    bType = argType.elementType();
                }

                // if the argument is a connector, create the symbol name with connector name and package
                if (bType == TypeEnum.CONNECTOR) {
                    sb.append("new " + simpleTypeNameClass + "(\"" + enclosingScopeName + "\",\"" + enclosingScopePkg +
                        "\", " + isArray + ")");
                } else {
                    sb.append("new " + simpleTypeNameClass + "(\"" + bType.getName() + "\", " + isArray + ")");
                }

                if (argCount != arguments.length - 1) {
                    sb.append(", ");
                }
                argCount++;
            }
        }
        sb.append("}");
        return sb.toString();
        
    }
    
    /**
     * Build the class. Append the remaining implemented methods and and write the source
     * file to the target (package) location.
     */
    public void build() {
        try {
            sourceFileWriter.write("}\n}\n");
        } catch (IOException e) {
            throw new BallerinaException("error while writing source to file: " + e.getMessage());
        } finally {
            if (sourceFileWriter != null) {
                try {
                    sourceFileWriter.close();
                } catch (IOException ignore) {
                }
            }
        }
    }
    
    /**
     * Create the configuration file in META-INF/services, required for java service
     * provider api.
     * 
     * @param filer {@link Filer} associated with this annotation processor.
     */
    private void createServiceMetaFile(Filer filer) {
        Writer configWriter = null;
        try {
            //Find the location of the resource/META-INF directory.
            FileObject metaFile = filer.createResource(StandardLocation.CLASS_OUTPUT, "",  META_INF + SERVICES + 
                    NativeConstructLoader.class.getCanonicalName());
            configWriter = metaFile.openWriter();
            configWriter.write(packageName + "." + className);
        } catch (IOException e) {
            throw new BallerinaException("error while generating config file: " + e.getMessage());
        } finally {
            if (configWriter != null) {
                try {
                    configWriter.close();
                } catch (IOException ignore) {
                }
            }
        }
    }

    /**
     * @param connectors
     */
    public void addConnectors(Map<String, Connector> connectors) {
        Iterator<Connector> connecorItr = connectors.values().iterator();
        String connectorVarName = "nativeConnector";
        while (connecorItr.hasNext()) {
            Connector con = connecorItr.next();
            BallerinaConnector balConnector = con.getBalConnector();
            String connectorName = balConnector.connectorName();
            String connectorPkgName = balConnector.packageName();
            String connectorClassName = con.getClassName();
            
            StringBuilder strBuilder = new StringBuilder();
            
            // Add all the actions of this connector
            for (Action action : con.getActions()) {
                BallerinaAction balAction = action.getBalAction();
                String actionQualifiedName = getActionQualifiedName(balAction, connectorName, connectorPkgName);
                String actionPkgName = balAction.packageName();
                String actionClassName = action.getClassName();
                
                String actionAddStr = getConstructInsertStr(connectorVarName, actionPkgName, balAction.actionName(),
                    actionQualifiedName, null, null, actionClassName, balAction.args(), balAction.returnType(),
                    balAction.args().length, "nativeAction", null, nativeUnitClassName, "nativeActionClass",
                    connectorName, connectorPkgName);
                strBuilder.append(actionAddStr);
            }
            
            String nativeConnectorClassName = AbstractNativeConnector.class.getSimpleName();
            String symbolScopClass = SymbolScope.class.getName() + ".class";
            String connectorAddStr = getConstructInsertStr(GLOBAL_SCOPE, connectorPkgName, connectorName, connectorName,
                    symbolScopClass, GLOBAL_SCOPE, connectorClassName, balConnector.args(), null, 
                    balConnector.args().length, connectorVarName, strBuilder.toString(), nativeConnectorClassName, 
                    "nativeConnectorClass", null, null);
            try {
                sourceFileWriter.write(connectorAddStr);
            } catch (IOException e) {
                throw new BallerinaException("failed to write to source file: " + e.getMessage());
            }
        }
    }
    
    private String getActionQualifiedName(BallerinaAction balAction, String connectorName, String connectorPkg) {
        StringBuilder actionNameBuilder = new StringBuilder(balAction.connectorName() + "." + balAction.actionName());
        Argument[] args = balAction.args();
        for (Argument arg : args) {
            if (arg.type() == TypeEnum.CONNECTOR) {
                actionNameBuilder.append("." + connectorPkg + ":" + connectorName);
            } else if (arg.type() == TypeEnum.ARRAY && arg.elementType() != TypeEnum.EMPTY) {
             // if the argument is arrayType, then append the element type to the method signature
                actionNameBuilder.append("." + arg.elementType().getName() + "[]");
            } else {
                actionNameBuilder.append("." + arg.type().getName());
            }
        }
        return actionNameBuilder.toString();
    }
}
