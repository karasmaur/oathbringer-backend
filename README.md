# Oathbringer-backend

A basic Clojure REST API Implementation using Compojure and Ring/HTTPKit.

This is the backend for a web application meant to be used as a tool for tabletop RPG players.
The main functionality is the item manager, which can be used to track the items your characters have, and the total weight of them.

Some features of the item manager are:
* Item sharing between campaigns(other players can change and use the same items).
* Multiple containers for each character, eg. self, horse, bag holding, house, etc.

## Installation

Follow the tutorial over at medium:
https://medium.com/@functionalhuman/building-a-rest-api-in-clojure-3a1e1ae096e

## Usage

You can run the web server with the following command:

    lein run

## Bugs

Failing to pass in a parameter for :firstname or :surname crashes our server! We need to update our code to handle missing values better.
