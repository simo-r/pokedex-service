# Pokedex service
Fun Pokedex in the form of a REST API, it returns:
1. Basic Pokemon information.
2. Basic Pokemon information but with a ‘fun’ translation of the Pokemon description.

## Develop

### Requirements
- Java 21+
- Docker 

### Build & Test

Test requires docker environment as the service uses `testcontainer` framework to boot instances
Run complete build and test

[!NOTE]
Start docker daemon before running tests

```shell
./gradlew build
```
Run tests
```shell
./gradlew test
```

## Run

### Gradle

```shell
./gradlew run
```

### Docker
TODO

## Third-party api

### Pokedex
https://pokeapi.co/api/v2/pokemon-species

### Fun description translation
1. For `cave` or legendary pokemons: https://funtranslations.com/api/yoda
2. Others: https://funtranslations.com/api/shakespeare

## Usage

### Basic Pokemon information endpoint
Substitute the {pokemon-name} with an existing Pokemon name
```shell
curl --request GET \
  --url http://localhost:8080/v1/pokemon/{pokemon-name}
```

## Production readiness

### Functional
- Authentication & Authorization
- Logging 
- Data model validation: add optional/required fields, nullable
- Improve exception handling: rest client exceptions, business logic exceptions
- Decoupling adapter models and internal DTOs
- Cache external dependencies response
- Clients should be abstracted from the actual implementation of using Spring Rest Client
- Integration tests stub could be improved by decoupling and make use case more explicit

### Business
- Multiple locale support
- Multiple flavour support