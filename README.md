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

- Java 21
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

- JDK 21
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
docker compose -f docker-compose.yml up -d
```

Cela démarre un conteneur PostgreSQL nommé :

```text
saas-db
```

Pour arrêter la base :

```bash
docker compose -f docker-compose.yml down
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
V1__Init_DB_For_Tenant.sql
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

L'application utilise Spring Security avec une authentification JWT en chiffrement RSA asymétrique (RS256).

### Authentification JWT

Les tokens JWT sont signés avec une clé privée RSA et vérifiés avec la clé publique correspondante.

Chaque token embarque :

- `sub` : identifiant de l'utilisateur
- `role` : rôle de l'utilisateur
- `tenantId` : identifiant du tenant
- `iat` / `exp` : dates d'émission et d'expiration

Les clés RSA sont générées avec OpenSSL :

```bash
openssl genrsa -out private.pem 2048
openssl rsa -in private.pem -pubout -out public.pem
```

### Flux d'authentification

1. Le client envoie ses identifiants sur `POST /api/v1/auth/login`
2. `AuthenticationServiceImpl` valide les identifiants et génère un token via `JwtService`
3. Le token est retourné au client

### Flux de validation par requête

1. Le client envoie le token dans le header `Authorization: Bearer <token>`
2. `JwtAuthenticationFilter` intercepte la requête, valide le token et alimente le `SecurityContext`
3. Le `tenantId` extrait du token est stocké dans `TenantContext` (ThreadLocal) pour la durée de la requête
4. `TenantContext.clear()` est appelé dans le `finally` pour éviter toute fuite entre threads

### Endpoints publics

Les URLs suivantes sont accessibles sans token :

```text
/api/v1/**
/swagger-ui/**
/v3/api-docs/**
```

> Note : la liste `PUBLIC_URLS` dans `SecurityConfig` sera resserrée pour la production (login/register uniquement).

### Suppression du TenantFilter

Dans une première approche, l'isolation multi-tenant était assurée par un filtre servlet dédié (`TenantFilter implements Filter`) qui :

- Lisait un header HTTP `X-Tenant-Id` sur chaque requête
- Stockait la valeur dans `TenantContext` (ThreadLocal)
- Activait un filtre Hibernate (`@FilterDef` / `@Filter`) via AOP pour ajouter automatiquement `WHERE tenant_id = :tenantId` à toutes les requêtes

Cette approche a été abandonnée pour les raisons suivantes :

- Le `X-Tenant-Id` était fourni par le client, sans garantie d'authenticité
- L'activation du filtre Hibernate via AOP ajoutait de la complexité
- L'architecture a évolué vers une isolation par schéma PostgreSQL (multi-tenant schema-based) : chaque tenant possède son propre schéma, éliminant le besoin d'un filtre de ligne par `tenant_id`
- Le `tenantId` est désormais extrait du token JWT signé (source de confiance) par `JwtAuthenticationFilter`, rendant le `TenantFilter` superflu

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
docker compose -f docker-compose.yml up -d
```

Arrêter la base PostgreSQL :

```bash
docker compose -f docker-compose.yml down
```

## Workflow de démarrage rapide

1. Démarrer PostgreSQL :

```bash
docker compose -f docker-compose.yml up -d
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
