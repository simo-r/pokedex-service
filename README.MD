# Pokedex service
Fun Pokedex in the form of a REST API, it returns:
1. Basic Pokemon information.
2. Basic Pokemon information but with a ‘fun’ translation of the Pokemon description.

## Develop

### Requirements
- Java 21+
- Docker 

### Build & Test

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

## API used

### Pokedex
https://pokeapi.co/api/v2/pokemon-species/mewtwo

[WIP]
- Choose default language: EN
- Choose default pokedex flavour: RED (?)





## Production readiness

### Functional
- Authentication & Authorization
-

### Business
- Multiple locale support
- Multiple flavour support