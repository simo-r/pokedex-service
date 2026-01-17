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

### Business
- Multiple locale support
- Multiple flavour support