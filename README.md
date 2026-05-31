# SaaS App

API REST Spring Boot pour gérer des catégories, des produits et des mouvements de stock dans une application SaaS.

Le projet utilise Spring Boot, Spring Data JPA, PostgreSQL, Flyway, Spring Security et Swagger/OpenAPI.

## Fonctionnalités

- Gestion des catégories
- Gestion des produits
- Gestion des mouvements de stock
- Pagination des résultats
- Mapping DTO avec services dédiés
- Migrations SQL avec Flyway
- Documentation API avec Swagger UI
- Configuration par variables d'environnement via `.env`

## Stack technique

- Java 17
- Spring Boot 4
- Spring Data JPA
- Spring Security
- PostgreSQL
- Flyway
- Maven
- Lombok
- Springdoc OpenAPI / Swagger UI
- Docker Compose

## Structure principale

```text
src/main/java/com/boudissa/saasapp
├── config          # Configuration sécurité et auditing
├── controller      # Endpoints REST
├── dto             # Requests, responses et mappers
├── entities        # Entités JPA
├── repositories    # Repositories Spring Data JPA
└── services        # Interfaces et implémentations métier
```

Les migrations Flyway se trouvent dans :

```text
src/main/resources/db/migration/common
src/main/resources/db/migration/tenant
```

## Prérequis

Avant de lancer le projet, installer :

- JDK 17
- Maven ou utiliser le wrapper Maven fourni
- Docker Desktop
- Un client API optionnel : Swagger UI, Postman, Insomnia

## Configuration

Le projet charge automatiquement le fichier `.env` grâce à :

```yaml
spring:
  config:
    import: optional:file:.env[.properties]
```

Exemple de configuration `.env` :

```properties
SPRING_PROFILES_ACTIVE=dev
DB_HOST=localhost
DB_PORT=5432
DB_NAME=saas-app-db
DB_USER=postgres
DB_PASSWORD=postgres
SERVER_PORT=8080
```

La base de données est configurée dans `application.yml` avec PostgreSQL.

## Lancer PostgreSQL avec Docker

Depuis la racine du projet :

```bash
docker compose -f docker.compose.yml up -d
```

Cela démarre un conteneur PostgreSQL nommé :

```text
saas-db
```

Pour arrêter la base :

```bash
docker compose -f docker.compose.yml down
```

## Lancer l'application

Avec le wrapper Maven Windows :

```bash
./mvnw.cmd spring-boot:run
```

Ou avec Maven installé :

```bash
mvn spring-boot:run
```

L'application démarre par défaut sur :

```text
http://localhost:8080
```

Le port peut être modifié avec la variable :

```properties
SERVER_PORT=8080
```

## Accéder à Swagger

Une fois l'application démarrée, ouvrir :

```text
http://localhost:8080/swagger-ui/index.html
```

La documentation OpenAPI brute est disponible ici :

```text
http://localhost:8080/v3/api-docs
```

## Endpoints principaux

### Catégories

```http
POST   /api/v1/categories
GET    /api/v1/categories
GET    /api/v1/categories/{id}
PUT    /api/v1/categories/{id}
DELETE /api/v1/categories/{id}
```

### Produits

```http
POST   /api/v1/products
GET    /api/v1/products
GET    /api/v1/products/{id}
PUT    /api/v1/products/{id}
DELETE /api/v1/products/{id}
```

### Mouvements de stock

```http
POST   /api/v1/stock-mvts
GET    /api/v1/stock-mvts
GET    /api/v1/stock-mvts/{id}
PUT    /api/v1/stock-mvts/{id}
DELETE /api/v1/stock-mvts/{id}
```

Les endpoints `GET` paginés acceptent :

```text
?page=0&size=10
```

## Flyway

Flyway est activé dans `application.yml` :

```yaml
spring:
  flyway:
    enabled: true
    baseline-on-migrate: true
    locations:
      - classpath:/db/migration/common
      - classpath:/db/migration/tenant
    schemas: public
    table: flyway_schema_history
    validate-on-migrate: true
```

Au démarrage, Flyway exécute les scripts SQL versionnés, par exemple :

```text
V1__Init_DB.sql
```

Si la migration s'exécute correctement, une ligne est ajoutée dans la table :

```text
flyway_schema_history
```

Important : après exécution, il ne faut pas modifier un script Flyway déjà appliqué en base, sinon le checksum ne correspondra plus. Il faut créer une nouvelle migration, par exemple :

```text
V2__Add_new_column.sql
```

## Hibernate et génération du schéma

Le projet laisse Flyway gérer la création des tables.

Hibernate est configuré en mode validation :

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
```

Cela signifie que Hibernate vérifie que les tables existent et correspondent aux entités, mais ne crée pas les tables automatiquement.

## Sécurité

Spring Security est présent dans le projet.

Une configuration temporaire autorise les endpoints API et Swagger :

```text
/api/v1/**
/swagger-ui/**
/v3/api-docs/**
```

Cette configuration est pratique pendant le développement. Pour une version production, il faudra ajouter une vraie authentification et une gestion des rôles.

## Auditing JPA

Le projet utilise l'auditing JPA avec :

- `created_at`
- `updated_at`
- `created_by`
- `updated_by`

Si aucun utilisateur authentifié n'est disponible, la valeur utilisée pour l'audit est :

```text
system
```

## Commandes utiles

Compiler le projet :

```bash
./mvnw.cmd clean compile
```

Lancer les tests :

```bash
./mvnw.cmd test
```

Créer le package :

```bash
./mvnw.cmd clean package
```

Démarrer la base PostgreSQL :

```bash
docker compose -f docker.compose.yml up -d
```

Arrêter la base PostgreSQL :

```bash
docker compose -f docker.compose.yml down
```

## Workflow de démarrage rapide

1. Démarrer PostgreSQL :

```bash
docker compose -f docker.compose.yml up -d
```

2. Lancer l'application :

```bash
./mvnw.cmd spring-boot:run
```

3. Ouvrir Swagger :

```text
http://localhost:8080/swagger-ui/index.html
```

4. Tester un endpoint, par exemple :

```http
GET /api/v1/categories?page=0&size=10
```

## Notes de développement

- Les scripts Flyway doivent être placés dans les dossiers configurés dans `application.yml`.
- Les noms des migrations doivent suivre le format `V1__Description.sql`, `V2__Description.sql`, etc.
- Les relations `@OneToMany` sont lazy par défaut. Éviter d'y accéder directement dans les mappers hors transaction.
- Pour les données calculées comme `nbProducts`, préférer une requête dédiée ou une projection DTO.
