============
Key Concepts
============
Each Ballerina program represents a discrete unit of functionality that performs an integration task. You can create a Ballerina program as a **service** that runs in the Ballerina server and awaits requests over HTTP. Or you can create your program as an executable program that executes a ``main()`` function and then exits (see below). Your Ballerina programs can be as simple or as complex as you like, but for best results, each program should focus on a specific task.

As you create your program, there are several constructs available that you can use:

* **Service**: When defining a Ballerina program as a service instead of an executable program, the ``service`` construct acts as the top-level container that holds all the integration logic and can interact with the rest of the world. Its base path is the context part of the URL that you use when sending requests to the service. Note that you can have multiple services in a single Ballerina program, each with their own base path. This is useful in more complex scenarios like the Content-Based Routing sample. 
* **Resource**: A resource is a single request handler within a service. When you create a service in Ballerina using the visual editor, a default resource is automatically created as well. The resource contains the integration logic.   
* **Function**: A function is a single operation. Ballerina includes a set of native functions you can call, such as the ``ConvertToResponse()`` function you used in the Tutorial, and you can define additional functions within your Ballerina programs.
* **Worker**: A worker is a thread that executes a function. 
* **Connector**: A connector represents a participant in the integration and is used to interact with an external system or a service you've defined in Ballerina. Ballerina includes a set of standard connectors that allow you to connect to Twitter, Facebook, and more, and you can define additional connectors within your Ballerina programs.
* **Action**: An action is an operation you can execute against a connector. It represents a single interaction with a participant of the integration.

You can also define constants, variables, structured types, and more as you do with other programming languages, and you can use logic statements like if and while. For more information, see the :doc:`language-ref`. 

.. image:: images/Figure1-1.png

-------------------
Executable programs
-------------------
An executable program contains the core integration logic within the ``main()`` function. For example::

  import ballerina.lang.system;

  function main (string[] args) {
    system:println("Hello, World!");
  }

When you run this program, the ``main()`` function executes, and then the program terminates. You can define additional functions, connectors, etc. inside the program and call them from ``main()``. For a more complex example of an executable Ballerina program, see the Twitter Connector sample.
