<div style="text-align: justify">  

> Authors       : Dejvid Muaremi, Mentor Reka, Xavier Vaz Afonso    
> Professor     : Nastaran Fatemi   
> Assistants    : Maxime Lovino , Miguel Santamaria   
> Date          : 05.05.2019  

# Tweet Analyser Backend

__Our web client can be found [here](https://github.com/XavierAfonso/TweetAnalyser-Frontend).__


## Description 
The objective of this project is to analyze the fellings coming from tweets on Twitter. This tool would allow a company, for example, to analyze the responses of the most recent tweet and to know the general feeling of it.  
A web client would allow to easily visualize the result, you can see our client as an exemple of what can be done.

The database would allow authentication to be managed and would serve as a cache so you won't have to recalculate the results each time.


## Technologies

- Main framework of the backend : [Scala Play](https://www.playframework.com/ "Scala Play").
- Functional Relational Mapping (FRM) for Scala : [Slick](http://slick.lightbend.com/ "Slick").



## Tools

- [Twitter's API](https://developer.twitter.com/en/docs "Twitter's API")

- [Stanford CoreNLP](https://stanfordnlp.github.io/CoreNLP/ "Stanford CoreNLP")

## Requirements
First you need to obtain an application token from Twitter and add it to your environnment as shown in the `env-example.conf`.  

Next, you'll need a database, you can configure the links to it in the application.con file.  
This database should contain a schema called `scala-project` which contains at least the schema. If you have docker installed on your server you can simply go to `./docker/topologies/dev` and run `docker-compose up --build` this will create the database and it's table for you.  
The MySQL scripts used for this is under `docker\images\mysql\scripts`, if you put any other script here it will also be executed when you create the database.
Once the database is created, you just have to use `docker-compose up` to start it.