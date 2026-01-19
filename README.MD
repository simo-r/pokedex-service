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

> [!NOTE]
> Docker daemon must be running before executing tests

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

#### Docker compose

```shell
docker-compose up -d
```

#### Build

```shell
docker build -t pokedex-service:1.0 .
```

#### Run
```shell
docker run -p 8080:8080 pokedex-service:1.0
```

## Third-party api

### Pokedex
https://pokeapi.co/api/v2/pokemon-species

### Fun description translation
1. For `cave` or legendary Pokemon: https://funtranslations.com/api/yoda
2. Others: https://funtranslations.com/api/shakespeare

## Usage

### Basic Pokemon information endpoint
Substitute the {pokemon-name} with an existing Pokemon name
```shell
curl --request GET \
  --url http://localhost:8080/v1/pokemon/{pokemon-name}
```

### Translated basic Pokemon information endpoint
Substitute the {pokemon-name} with an existing Pokemon name
```shell
curl --request GET \
  --url http://localhost:8080/v1/pokemon/translated/{pokemon-name}
```

## Production readiness

### Functional
- Authentication & Authorization
- Observability: metrics and logs
- Data model validation: add optional/required fields, nullable
- Improve exception handling: rest client exceptions, business logic exceptions, Spring rest controllers exception
- Decoupling adapter models and internal DTOs
- Clients should be abstracted from the actual implementation of using Spring Rest Client
- Integration tests stub could be improved by decoupling and make use case more explicit
- Cache our API responses 

### Business
- Multiple locale support
- Multiple flavour support


# Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                          HTTP Client                            │
└───────────────────────────┬─────────────────────────────────────┘
                            │
┌───────────────────────────▼─────────────────────────────────────┐
│                  ADAPTER LAYER                                   │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │  GetPokemonInfoController                               │   │
│  │  - Receives HTTP requests                               │   │
│  │  - Delegates to use cases                               │   │
│  │  - Transforms domain → response model                   │   │
│  └──────────────────────────────────────────────────────────┘   │
│                            │                                    │
│  ┌────────────────────────┴────────────────────────────────┐   │
│  │  GlobalExceptionHandler                                │   │
│  │  - Maps exceptions to HTTP responses                  │   │
│  └────────────────────────────────────────────────────────┘   │
└───────────────────────────┬─────────────────────────────────────┘
                            │
┌───────────────────────────▼─────────────────────────────────────┐
│                 APPLICATION LAYER (Use Cases)                    │
│  ┌──────────────────┐        ┌──────────────────────────────┐   │
│  │ GetPokemonInfo   │        │ GetTranslatedPokemonInfo     │   │
│  │ UseCase          │        │ UseCase                      │   │
│  │ (Orchestration)  │        │ (Orchestration + Strategy)   │   │
│  └────────┬─────────┘        └────────┬─────────────────────┘   │
│           │                           │                         │
│           └───────────────┬───────────┘                         │
│                           │                                     │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │  TranslationStrategy (Strategy Pattern)                 │   │
│  │  - YodaTranslationStrategy                             │   │
│  │  - ShakespeareTranslationStrategy                      │   │
│  └──────────────────────────────────────────────────────────┘   │
└───────────────────────────┬─────────────────────────────────────┘
                            │
┌───────────────────────────▼─────────────────────────────────────┐
│                    ADAPTER LAYER (Clients)                       │
│  ┌──────────────────┐        ┌──────────────────────────────┐   │
│  │ PokemonRestClient│        │ TranslationRestClient        │   │
│  │ + Resilience4j:  │        │ + Resilience4j:              │   │
│  │  - Retry         │        │  - Retry                     │   │
│  │  - CircuitBreaker│        │  - CircuitBreaker            │   │
│  │  - Cache         │        │  - Cache                     │   │
│  └────────┬─────────┘        └────────┬─────────────────────┘   │
└───────────┼─────────────────────────┼─────────────────────────────┘
            │                         │
┌───────────▼──────┐       ┌──────────▼─────────────────────┐
│ PokéAPI          │       │ FunTranslations API            │
│ External Service │       │ External Service              │
└──────────────────┘       └────────────────────────────────┘
```

---