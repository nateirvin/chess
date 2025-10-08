# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

[WebAPI Sequence Diagram](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=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)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
