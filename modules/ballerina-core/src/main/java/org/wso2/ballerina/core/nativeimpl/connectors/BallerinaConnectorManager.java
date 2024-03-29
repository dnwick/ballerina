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
package org.wso2.ballerina.core.nativeimpl.connectors;

import org.wso2.ballerina.core.exception.BallerinaException;
import org.wso2.ballerina.core.runtime.MessageProcessor;
import org.wso2.ballerina.core.runtime.dispatching.ResourceDispatcher;
import org.wso2.ballerina.core.runtime.dispatching.ServiceDispatcher;
import org.wso2.ballerina.core.runtime.registry.DispatcherRegistry;
import org.wso2.carbon.connector.framework.ConnectorManager;
import org.wso2.carbon.messaging.ClientConnector;
import org.wso2.carbon.messaging.ServerConnector;
import org.wso2.carbon.messaging.ServerConnectorErrorHandler;
import org.wso2.carbon.messaging.exceptions.ServerConnectorException;

import java.util.Optional;
import java.util.ServiceLoader;

/**
 * {@code BallerinaConnectorManager} is responsible for managing all the server connectors with ballerina runtime.
 */
public class BallerinaConnectorManager {

    private ConnectorManager connectorManager = new ConnectorManager();

    private static BallerinaConnectorManager instance = new BallerinaConnectorManager();

    private boolean connectorsInitialized = false;

    private BallerinaConnectorManager() {
    }

    public static BallerinaConnectorManager getInstance() {
        return instance;
    }

    /**
     * Returns the server connector instance associated with the given protocol.
     * @param id the identifier of the server connector.
     * @return server connector instance.
     */
    public ServerConnector getServerConnector(String id) {
        return connectorManager.getServerConnector(id);
    }

    /**
     * Returns the client connector instance associated with the given protocol.
     * @param protocol of the client connector.
     * @return client connector instance.
     */
    public ClientConnector getClientConnector(String protocol) {
        return connectorManager.getClientConnector(protocol);
    }

    /**
     * Creates and return a server connector using the given protocol and id. The protocol is used with acquiring the
     * correct server connector provider. An error will be thrown, if there are no server connector provider registered
     * for the given protocol. Throws {@code BallerinaException} error if there were any issues trying to create a new
     * connector instance.
     *
     * @param protocol transport protocol used with finding the correct server connector provider.
     * @param id unique id to use when creating the server connector instance.
     * @return returns the newly created instance.
     */
    public ServerConnector createServerConnector(String protocol, String id) {
        ServerConnector serverConnector;
        try {
            serverConnector = connectorManager.createServerConnector(protocol, id);
        } catch (ServerConnectorException e) {
            throw new BallerinaException("Error occurred while creating a server connector for protocol : '" +
                    protocol + "' with the given id : '" + id + "'", e);
        }
        return serverConnector;
    }

    /**
     * Register the given server connector error handler instance with the manager. Protocol of the handler will be
     * used with registering the handler.
     * @param serverConnectorErrorHandler handler instance to register.
     */
    public void registerServerConnectorErrorHandler(ServerConnectorErrorHandler serverConnectorErrorHandler) {
        connectorManager.registerServerConnectorErrorHandler(serverConnectorErrorHandler);
    }

    /**
     * Returns an {@code Optional} value of the server connector error handler registered against the given transport
     * protocol.
     * @param protocol the transport protocol associated with the error handler.
     * @return error handler instance.
     */
    public Optional<ServerConnectorErrorHandler> getServerConnectorErrorHandler(String protocol) {
        return connectorManager.getServerConnectorErrorHandler(protocol);
    }

    /**
     * Initialize and load all the server and client connectors. The given instance of the message processor will
     * be used to initialize all the default server connectors and it will be used with subsequent new connector
     * creation as-well.
     *
     * @param messageProcessor message processor instance used with initializing the server connectors.
     */
    public void initialize(MessageProcessor messageProcessor) {
        if (connectorsInitialized) {
            return;
        }
        //1. Loading service and resource dispatchers related to transports
        loadDispatchers();

        //2. Initialize server connectors
        try {
            connectorManager.initializeServerConnectors(messageProcessor);
        } catch (ServerConnectorException e) {
            throw new BallerinaException("Error occurred while initializing all server connectors", e);
        }

        //3. Initialize client connectors
        connectorManager.initializeClientConnectors(messageProcessor);

        connectorsInitialized = true;
    }

    /**
     * Initialize and load client connectors. The given instance of the message processor will
     * be used to initialize all the client connectors and it will be used with subsequent new connector
     * creation as-well.
     *
     * @param messageProcessor message processor instance used with initializing the server connectors.
     */
    public void initializeClientConnectors(MessageProcessor messageProcessor) {
        connectorManager.initializeClientConnectors(messageProcessor);
    }

    private void loadDispatchers() {
        ServiceLoader<ResourceDispatcher> resourceDispatcherServiceLoader =
                ServiceLoader.load(ResourceDispatcher.class);
        resourceDispatcherServiceLoader.forEach(resourceDispatcher ->
                DispatcherRegistry.getInstance().registerResourceDispatcher(resourceDispatcher));

        ServiceLoader<ServiceDispatcher> serviceDispatcherServiceLoader = ServiceLoader.load(ServiceDispatcher.class);
        serviceDispatcherServiceLoader.forEach(serviceDispatcher ->
                DispatcherRegistry.getInstance().registerServiceDispatcher(serviceDispatcher));
    }

}
