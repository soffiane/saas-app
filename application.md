On peut faire generer les tables au demarrage de l'application via Spring et Hibernate avec la bonne configuration du application.yml

On peut aussi utiliser flyway pour generer les tables. 

Il faut deposer le script SQL qu'on veut faire executer au demarrage de l'application dans un des chemin renseigné dans la configuration

flyway:
enabled: true
baseline-on-migrate: true
locations:
- classpath:/db/migration
- classpath:/db/migration/common
schemas:
- public
- tenant
table: flyway_schema_history
validate-on-migrate: true

Si ca se passe bien, ca ajoute une ligne dans la table flyway_schema_history

1,1,Init DB,SQL,V1__Init_DB_For_Tenant.sql,909064249,postgres,2026-05-31 15:17:24.794172,19,true
909064249 est un checksum
Si le script est modifié, il faut supprimer la ligne dans la table flyway_schema_history sinon ca plante

----------------------------------------------------------------

Approche base unique multi tenant

1ere etape : gestion de la migration FLyway en ajoutant le tenant_id dans les tables
On ajoute un scripts SQL qui va ajouter la colonne tenant_id et on l'ajoute a l'entité generique AbstractEntity
Au demarrage de l'application, le script SQL est executé et la colonne tenant_id est ajoutée dans les tables
On peut aussi verifier dans la table flyway_schema_history que le script a bien ete executé

2eme etape : 
TenantContext et TenantFilter qui implements Filter de Servlet

3eme etape : 
composant AOP avec TenantHibernateFilter

4eme etape : 
Comment faire executer le filter TenantFilter ? Avec des annotations au niveau des entités
@FilterDef(name = "tenantFilter",
parameters = @ParamDef(name = "tenantId", type = String.class),
defaultCondition = "tenant_id = :tenantId")
@Filter(name = "tenantFilter")

Les requetets vont ajouter le tenant_id et spring va pouvoir filtrer les requetes

on ajoute un header X-Tenant-Id dans la requete HTTTP
Dans le code Java, on ajout une classe TenantFilter qui va intercepter la requete et ajouter le tenant_id, ce tenant_id sera disponible dans toute la requete
TenantContext.getCurrentTenantId() permet de recuperer le tenant_id et on l'injecte dans le TenantHibernateFilter
Avec AOP on va activer le tenant_id
Ensuite coté hibernate il faut gerer ce tenant_id 

Avec SpringSecurity on peut mettre le tenant_id dans le token JWT et l'extraire ensuite

-----------------------------------------------------------------------------------------------------------------------------------
Chiffrement RSA asymetrique
Cle privée - clé publique - public.pem / private.prem generée par exemple avec OpenSSL
Token JWT :
- header : algorithme utilisé et type
- payload : données (roles, nom, id, etc, iat, exp)
- signature : cree avec la cle privée
Flux authentification :
- appel du frontend avec un formulaire de connexion
- AuthController : valide login et password : construit les claims et signe avec la cle privée
- JwtService : genere un token JWT
- Reponse : envoie le token JWT au frontend

Flux validation
- frontend ajoute le token JWT dans le header Authorization (bearer)
- JwtAuthenticationFilter : lit le token JWT et le decode et crée le SecurityContext
- JwtService valide le token avec la cle publique
- SecurityContext contient les informations de l'utilisateur
- Le controller peut recuperer les informations de l'utilisateur avec SecurityContextHolder

Composants Spring Boot impliqués
- RsaKeyProperties
- JwtService
- JwtAuthenticationFilter
- SecurityConfig

openssl genrsa -out private.pem 2048
openssl rsa -in private.pem -pubout -out public.pem

Charger les clés dans Springboot
