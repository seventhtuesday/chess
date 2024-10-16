# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

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
## Sequence Diagram Link
https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uE3RkM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKw4C+1LGiWmrWs06W622n1+h1g9W5U6Ai5lCJQpFQSKqJVYFPAmWFI6XGDXDp3SblVZPQN++oQADW6ErU32jsohfgyHM5QATE4nN0y0MxWMYFXHlNa6l6020C3Vgd0BxTF5fP4AtB2OSYAAZCDRJIBNIZLLdvJF4ol6p1JqtAzqBJoIei0azF5vDgHYsgwr5kvDrco4rF8H5LCBALnAWspqig5QIAePKwvuh6ouisTYgmhgumGbpkhSBq0uWo4mkS4YWhyMDcryBqCsKMCvmIrrSkml6weURraI6zoErhLLlJ63pBgGQYhsx5qRpR0YwLGwZcVhnb-mmKE8tmuaYEpnY-lcAzEaMC6PH006zs246tn035XiCpzZD2MD9oOvS6SO+lmdWU5BiZ85uW2y6rt4fiBF4KDoHuB6+Mwx7pJkmC2ReRTUNe0gAKK7sl9TJc0LQPqoT7dMZjboO2v5nECAFGZ5hXeROFkaVBHYwU68owAh9gRch4W+mhGKYXK2G8WReEwOSYCySJdZVaRTJuhR5QAOIUjJol6kKYSyV5oaDSxjXgjA07xn1Gp8aSRgoNwmRjft2hTWaEaFJa6bDBANBLXG8mHaxpWpqUKERWpCB5vV1lsSUVyWYlwNdjkYB9gOQ5Lpw-nroEkK2ru0IwHNo6slFp6xeezCNdec3pVl9ijvllVzsVbJKTpBVzgZtWaTtzXILEyHQt1GHcYmA3Tfxw0UpdVPoDd5GSfNi2yYShj0etVWbQLEms3Be3LfIvP9TIx3ui10JY6MqiwuLM2S5ji0UMwEAAGYycqmOjg64kRqrVwKLuu6YwoACyyXSAo9QKODTXYcmQPlOjsT-WAGgs0TaY9FM5NG+MlT9CnKAAJLSGnACMvYAMwACxPCemQGhWPlfDoCCgA2lfAdXTyZwAck3NV7DAjQhw1NkE7DjlJ30meqGnFQZ6OOf50XpdTOX+p6WONVPLX9eN65K-J6O7eb+ZXc92YiOeAFG7YD4UDYNw8C6pkjujCk0VntDbLaZUtQNGTFPBKLz5OW3Hd9403DmVROI8d6AOrBVCajNq6QVAa-PqCoUDJENigWEglMjcyxFrI6W0TojRFjAsWStbqzQtswGW2hVqvRnIrF2WkkHqzeprBSOF8F60wSgNBsJM5iV1uQ6SaCDqwUUhHG+XoCCJBjnVBBn037D0ztPMyBcS690hnFGG9k4a9G3qMZR5RVHFwRiuE+yMAiWDOghZIMAABSEAeT30MAENeIAGz4xfgnco1RKR3haJnSmxC-79CvsASxUA4AQAQlABcAB1FgWcMotAAEK7gUHAAA0l8JRucVGz3UbTCOIS67hMidEuJCSkmpPSVkluU9cmGNntMaBdDYGd1kamRB7EYAACsHFoB4fY1SKA0Q9Vwew5WetCHCQZiQhh5sFqUI1sAGhCtqbzLdswuSrCPr81uvhMAPCcmmxYvdSiiynE0LQTnUhEtNl8PeqIiZ+yGLYC0JkNBhEcnvhKZQMp0BZiMRORJM55RpIb0MJAJxIjQ5iNAeUIZaAZHxxBgBApn1NGD3hkfUxa5AoBC8GErsXpYDAGwFfQg8REiPzxporpoNKgpTShlLKxhgF-nET0eBnTNkgG4HgDB-KswjPQjgthezwwCSFSbW5ZtQWPWeoYW29sEDQu0LMWi6qYAADJaEwp4l9YE5REAkuRUDelaL2VQzsg5bFfkgA
