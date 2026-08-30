Projet Maven minimal pour Nadia.java

Prérequis:
- Java 17 (ou supérieur)
- Maven 3.6+

Commandes utiles depuis C:\Dev\Nadia :

- Compiler et empaqueter :
  mvn package

- Exécuter le jar produit :
  java -jar target\nadia-1.0-SNAPSHOT.jar

- Ou exécuter via Maven (sans créer de jar) :
  mvn exec:java -Dexec.mainClass="Nadia"

Remarque : le fichier Nadia.java est placé dans src/main/java. Le code utilise les text-blocks et les sealed interfaces, d'où la nécessité de Java 17.