# Nadia

Nadia est un petit jeu d’aventure narratif développé en Java avec JavaFX. Le joueur progresse dans une histoire structurée sous forme de nœuds, choisit des options, collecte des objets et tente d’aboutir à une fin victorieuse ou d’échouer en fonction de ses décisions.

## Présentation du projet

Le principe est simple mais extensible :
- une histoire est décrite dans un fichier JSON ;
- chaque scène correspond à un nœud avec un texte, une image et des choix ;
- un choix peut conduire à une autre scène, déclencher une fin de partie ou offrir un objet ;
- l’inventaire limite le nombre d’objets que le joueur peut conserver ;
- l’interface graphique affiche les scènes, les choix et les objets récupérés.

Le jeu est pensé comme une base de prototype pour des histoires interactives ou des scénarios de type “livre-jeu” sans devoir recompiler le code pour modifier le contenu narratif.

## Fonctionnalités

- Interface JavaFX moderne et lisible
- Structure narrative chargée depuis des fichiers JSON
- Système d’objets avec inventaire limité
- Choix de type “suivant”, “victoire” et “échec”
- Gestion de fins de partie avec messages personnalisés
- Possibilité de redémarrer une partie facilement

## Architecture

Le projet est organisé autour de quelques éléments clés :

- `com.story.nadia.Nadia` : point d’entrée de l’application
- `com.story.nadia.ui.JavaFxApp` : interface graphique et rendu des scènes
- `com.story.nadia.engine.StoryBuilder` : lecture et construction de l’histoire à partir du JSON
- `com.story.nadia.engine.GameEngine` : logique de jeu, choix, inventaire et résolution des actions
- `com.story.nadia.model.*` : modèles de données de l’histoire, objets et résultats de jeu

Les ressources de contenu sont stockées dans `src/main/resources` :

- `stories/nadia.json` : scénario principal
- `items/nadia-items.json` : catalogue d’objets
- `images/` : visuels du jeu

## Structure du scénario

L’histoire est définie dans un fichier JSON avec un nœud de départ et une liste de nœuds. Chaque nœud contient :

- un texte de description ;
- une image associée ;
- une liste de choix ;
- pour chaque choix : le texte affiché, le type de résultat, la cible ou le message final, et éventuellement un objet à récupérer.

C’est ce qui permet de modifier facilement le contenu sans toucher au code Java.

## Prérequis

- Java 25
- Maven 3.6+

## Lancer le projet

Depuis la racine du projet :

```bash
mvn clean install
mvn exec:java
```

La classe principale est `com.story.nadia.Nadia` et est également configurée dans le `pom.xml`.

## Construire un JAR

```bash
mvn package
java -jar target/nadia-1.0-SNAPSHOT.jar
```

## Tests

```bash
mvn test
```

## À retenir

Nadia est un projet de démonstration de moteur narratif Java/JavaFX orienté “story-driven game”. Il montre comment séparer :
- le contenu narratif (JSON),
- la logique du jeu (Java),
- la présentation (JavaFX).

Cette séparation rend le projet facile à étendre, à enrichir et à réutiliser pour d’autres scénarios.
