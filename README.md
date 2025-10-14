# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

[WebAPI Sequence Diagram](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=C4S2BsFMAIGEAtIGcnQOqQEbQIIAUBJAKCIEMBjYAewCc5wRIA7YIgB1JtHJA5egDKkGgDdh7Tt16l+ACRkATKDQlcQPPsEHCR6yKqmboAEVLBSOcuWRIiCs6UykkMBZhLNgNAJ5IOPJgBzaAAGADoATiJAmioAVzYAYgAWAGYADgAmCPJoRIZA+GBMcDiYACVIQJAkLzMQKiYiWAZPaABaAD5tUWEALmgAbTwAeQEAFQBdaAB6OJcaAB0mAG8AInnhJlIAW0g1vrXoNYAaY44UAHdaBQOj0+PIHdIQcDvjgF8iIV66Luh5EwlP1oOtNjRtnt3g81hckNcaLdDsczmsni83si1l9AcC-t0frprAMaFUasBhAAKSrVWrCSoARzKtQAlN8dHoOt1TOZLNYUANApBgABVBaU8GQyBsnkWKw2LnQNx9ABiICB0DFwll0Ew3mgkt2+kwpNIAGtNQtoJcwPADQspdBSOBTQp9ZAAB7k2yyvkK9r-Ql6AZamiy9miTkBgk6EE4F2QUhu8bm5gAUQ91jYoEaEbEf3+LUYLAGyRCqWW6z2KFIQoOxzTNFiNAGhr2ToTSf15jNzGxRGYCiIvvlKA6gY5xOgTDi4HAeaj3IcfoF5FNFNDEoWsply9HqH+ypwCgUlu1DgX1kVI-5SD6a8TFJwcWA8EppBf8B3w73t8VR5PXBP3DIMr2jAFFGUAYaXJelkFnVhcWUccY1+AYwQdI0jgGe5UQ-V9xioXsmGw45PjzYQUPoYtgAGTIQhCSsNkwqFoBwlFjnw+BCOI0j7i+QcSBieIkloGQhTyFVYDTYxYBwaAABkqGqJoizaCc0KGUYJmmGYXBQBomErNtIDOOEEQUL5CUo-4kJBFYTLM5x4RuL4TUTC1Q0dWhoHMm5oBqaAdhqJB1UCIg7ILVD8wGAg5yqZ0cBoQI4j2FgMyzHMmmsqLqM8UsGMrY5qyQWt9hwxtmwGJxT1JJlkGAI4BKBCLIJs6Lg2gcBlPVTcuO-UCYH+G8bEFYVN0c3znIs3deX3f9MAGABxYUzzDBxdX1R1FGgFwH2AZZKRcGByCoHY0uANlEmgcZECmq5-KQeB4nAU9MBgFx+GoaBX1cBwnBcaBdFIaBGkgdpLlId0mDXbxs0Ms4dqel63o+6hSVPdUfru+xzABmB5jC7GPqNUGmHByHodh+HGjOJAqGJ+6XMRaBTp2DgaBqQyAtQBQwYCphGdxxxnFM7H5mtVwGd+rqqCIhJdVF09GjW7aNTwaabkRjUQAAM2gSBbUo-m+eQJgAHItGeYByDtS4YDNJgqEuSXoHgUgxCdWWVOgXWMTiUltdPPnpyoLRycgU9vq8R97S2UmfL8ln8z19R6hV5xdsgDmzDR7OkDCIh3PNVWE7oJPMdQJ2tFOptIEoH85r-cDBoGGc50vIaNJizVtk-WgQAAL0jjLs6yijcrUktoDLABGIq1hKsr6zWSraFbPvXwH4fbmgZqhxGscW8nSAQ23C9BuvX9RtZ9dIGfV8t3jvZEc-HjmFmuVm+6ZVcEAh+vwXkPgebunUAEgRPlRSKAxwEXkilRHK6FjgmRhFxd+JE94TyolPWi0B6KMVWMgli+w8JvyIn2TBgloixASIkGIkBmB5AKEUehjClKBHiKwHBipEFDGMGmBSaZxhpl0vpUKjRlhoPIdlWM+IIJAigk6MhxFWoKPaj0Ikp9vacL6soj+ncr5NxvrrdUChYHmEpNQYin8VwHh-otaAK0tDmNIEXU0FoAHoNDloXW8QWrAIQSfNus55yX2PppEUTAkAJDYLQCkCgRhsGEOnJgo8aYyN+Ng1o0854LyXnWCqTZ14Gk3s9TmO8moDn8dfI+oCpwuIMcNGpd4lSQCgE+YCDh3x6KYDY+ah4HGVB2FQT2DSAnhM0QMaJ+5Gm2Tai2XacQZnwImSCaZt4sGFmybg-BVShzUJEokDGeQ2DqgtApckjijS2G4d3EEgwlpCN0oEI0kiembO6NApRBFpGqLxDwoJ0AhTAHjOAJa1zKRsjCUuIxApfamJcZY6RfTv5KgcU4oCr5wzF08qU7ekdG5f39HUrR6oRDOhAMrOgTtyZejpPwMRhlZnRRBAAOTDiqPxCg0njxylkmiBV56EMXjYZehSqolK4vi3e+9GkwqJXC4FoLwXVkhYS2xC1lqrWdOAK5ewdRUEwAAK3rsAH0zTAmRnqU2KGFzagAB4VWQFlJ0OV8i8QwJtd4O1wBHVGhdX85CdyFkrCBdcgYgwViUrOFKM4lx4BgEgKGVlRozglAoGaZNRoPjTCsrIxUOC6KFUIS86sEao0KBjam60CaNwLBTS-XU4AM1Zr2DmyhLUDm0IUAAdgiCESAIQ8hplSLAaSAA2OAd89UwGaNsgFmlhhjCmLMUtkA3k-OIpWNdDbIB5syXMtRCypEqJWR1KcD5c5Ou6Zu5gZwd1GihZAppsKWkmKBIiqx+jxn2K1c4zp5g3EeV7lK8pBLxkkoGGSilVLQ60tggymwTKwnBoGOy4AnK4hAh5ch-N4FC0zxCEK0NIqawFIbEUhZWHQND0jpUwSEHz1aIadCkwzSBjvoUNeh9ewUX+l-Y41aTqdR6mnEaIDJcnUdldO6OlZroBHQYazM6F0rrQAIPrGWa6xPtkCtXEpIB6pnGei7QKMtwSLLYLElwUdpZtN1cAa4So9a62EG0NdqBSAc2AAAfhundAgxgeaLN1iYngbRfF0BlsLfGaaXzeMZuCQujGNGdWExfZ9qHcCdmTKmVJmYx64YPVsgVhGCEkfyeVCjEqarQDqsyRqHahyseAVBqJwhgDXvS+YPjR8BPHlPN11xKXW5hr2AQZrmXPnzMFEaCbgabJZbXfNvl+HtlFvK8cZbxh6yz0yKkZITWhI0KSN4ezzsmEgEKMAM7c4LsACkqBYyk4kdN5AzRzpogunuwwRQrpmGujd3FpGVjYM2s7NBYBUG6jQe9c3jD7vzIqL5J7mDLGWwoZYYOobCChzDhbcjRuPfVE6xkDW3WtfhR+gDpAkXWPVf0gTGKWPPvlbYjjpjuNzYUL1uxaK-0zpE-qQLEmLRSfQ9ATD1TX2WsmdOEJzKNFso5VynDuZVslfymVvJoryOr0o9VJMdXID1VqPR6XCqQFMeWv6i8OLcDV0QHQCzgVnQyexoFU6+OUMsoWfGGTKZiJq4yUjtbpWywVmFZVlea9j05e7Hl83k2rVDTZ-uKZwpxiJh2JSbbZwve0DOCZXnmqDRsFxjADzywXBaHtW9s04JKUAB942JsbwoboABeaAPnljxrc2Nw2p5u8+YZ83SDiyZk+-dYo9ZNgCey7WUsjZGvugEd2VQ4StCYhQ0u9dnf+oWiJjoF5sHacsrQE++pX3Eb+GCOEbpNwHyZ8gmMG04UkAycNQXxPt-7T74dwtbsatL-7KoQol4DIDAKDv4UjQAABUvssQOwg+tgQBr6UBMBAB4AACSAaqP6-OIBH+8BiBZ03y8AqBrObG6BhBT4c4oYuBEBAm0B-+xBusSBccNAFBKeUCM2k+GyKyWWc+KAmymu08uy9ukSnoSSlAkcBslGY+xKN+VBluweIha+620AAArMWhVrrlVvrhKpSNAUgGuLwBflQPrMIM2CyEnnskQEAA)

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
