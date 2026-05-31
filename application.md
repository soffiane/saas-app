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

1,1,Init DB,SQL,V1__Init_DB.sql,909064249,postgres,2026-05-31 15:17:24.794172,19,true
909064249 est un checksum
Si le script est modifié, il faut supprimer la ligne dans la table flyway_schema_history sinon ca plante

