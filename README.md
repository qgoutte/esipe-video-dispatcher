# esipe-video-dispatcher

###Solution choisie 
La base de donnée choisie est DynamoDB car plus facile 
d'utilisation (pour l'insertion et modification de données) 
malgré une documentation pas forcément évidente au 
premier coup d'oeil. De plus l'utilisation de DynamoDB 
permet de limiter le nombre de librairies importées car 
elle est inclue dans le sdk aws alors que Google Datastore
nécessite l'importation d'une autre librairie.


###Mise en place

* Mettre en place votre variable 
GOOGLE_ACCESS_CREDENTIALS (dirigeant vers le 
chemin de votre fichier json, généré sur GCP) 
ainsi que le fichier "credentials" dans ~/.aws 

* Modifier la valeur des variables dans le application.yml 
pour la partie google cloud. 







