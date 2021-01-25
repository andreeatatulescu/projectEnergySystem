# Project Energy System Phase 2

## About

Object Oriented Programming Course

January 2021

<https://ocw.cs.pub.ro/courses/poo-ca-cd/teme/proiect/etapa2>

Student: Tatulescu Diana-Andreea, 321CD

## Run tests

Class Test#main
  * runs the solution on test files from checker/, comparing the results with refs
  * runs checkstyle
  
More about the tests: checker/README

Libraries used for the implementation:
* Jackson Core 
* Jackson Databind 
* Jackson Annotations
* org.json.simple-0.30incubating.jar

Tutorial Jackson JSON: 
<https://ocw.cs.pub.ro/courses/poo-ca-cd/laboratoare/tutorial-json-jackson>


## Implementation

	This is an idea of energy system which can be used in real life in order
	to manage money, profits and contracts in an enterprise. The code could
	be extended for other similars ideas due to OOPL. 
	
	The parsing is based on JsonObject. In input package, I have provided 
	classes for the entities needed, all of them being final.

### Structure

```
src
|____
    |__commom
    |  |__Constants (useful for parsing)
    |
    |__documents
    |  |__Contract (info about contracts)
    |  |__MonthlyStat (info about each month status)
    |
    |__entities
    |  |__EnergyType (enum for energy types)
    |
    |__factories
    |  |__IPersonFactory (Singleton Factory)
    |  |__StrategyFactory (it helps when it comes to choose strategies)
    |
    |__gameflow
    |  |__BasicRound (the flow of the game for a basic round)
    |  |__InitialRound (the flow for round 0 - initiazation)
    |
    |__input
    |  |__ConsumerInputData (stores data about consumers)
    |  |__DistributorInputDatas (stores data about distributors)
    |  |__DistributorChanges (stores data about distributors changes)
    |  |__Input (data needed for parsing)
    |  |__InputLoader (the parsing itself)
    |  |__MonthlyUpdateInputData (stores data about monthly updates)
    |  |__ProducerChanges (stores data about producers changes)
    |  |__ProducerInputData (stores data about producers)
    |
    |__interfaces
    |  |__IPerson (implemented by all persons 
    |  |      - void monthlyPay(int costs, Contract contract) ;
    |  |      - void monthlyReceive(int sum);
    |  |__ProducerStrategy (implemented by all strategies, sorting the producers)
    |         - void sortByStrategy();
    |
    |__output (useful for writing in file, all data required by ref files)
    |  |__ConsumerOutput
    |  |__DistributorOutput
    |  |__ProducerOutput
    |  |__Writer
    |
    |__persons
    |  |__Consumer (implements IPerson)
    |  |__Distributor (implements IPerson)
    |  |__Producer (implements IPerson)
    |
    |__strategies
    |  |__EnergtChoiceStrategyType (enum for strategies)
    |  |__GreenStrategy (sorting by renewable enerby, by price, by quantity)
    |  |__PriceStrategy (sorting by price)
    |  |__QuantityStrategy (sorting by quantity)
    |
    |__checker
    |  |__checkstyle
    |
    |__Main (the whole game flow)
    |
    |__Test (runs the tests)
    
```


### Flow

	There is a light difference between the initial round and the others 
	because there are no updates and it needs a initialization of the 
	input data used for the game.
	
	1. Initialization - formimg the arraylists of Consumers, Distributors, 
	Producers
	2. The distributors choose the Producers conforming to their strategy
	3. Calculating the production cost for each distributor
	4. Calculating the contracts price and storing the info in a LinkedHashMap,
	sorted in ascendent order by price,	used by the consumers in order to 
	choose advantageously their contract
	5. After they have chosen, the consumers need to pay
	6. The distributors receive the money and pay the monthly costs
	
	========================= INITIAL ROUND DONE ============================= 
	
	1. Reading updates for consumers and distributors
	2. Calculating the contracts price and storing the info in a LinkedHashMap,
	sorted in ascendent order by price,	used by the consumers in order to 
	choose advantageously their contract
	3. After they have chosen, the consumers need to pay
	4. The distributors receive the money and pay the monthly costs
	
	5. Reading the updates for producers
	6. In case of updates, the distributors must to update their current
	producers(Observer Pattern) => used again the strategy (Strategy Pattern) 
	and recalculating the production cost in order to use this new info
	next month
	
	========================= BASIC ROUND DONE ===============================
	

### OOPL

	OOPL speaking, I have used inheritance and abstractization for the 
	classes which implements interfaces, and also encapsulation, 
	having finals fields and classes which cannot be modified by someone else, 
	just by using public methods - getters and setters.


### Design patterns

	I have used Factory Design Pattern for Consumator and Distributor, which
	extends IPerson. I though that it will be useful if we will have to add 
	some new entities such as new types of "players".
	Singleton is used for IPersonFactory in order to do
	this a single time because we don't want to have more instances
	for this. (Singleton Factory)
	
	More than this, I have implemented Observer Pattern and Strategy Pattern, 
	which requires itself a new factory. Using Observer Pattern, I notify
	the observers for each producer (its current distributors are the observs)
	because they have to apply again their strategy in order to find new
	producers. There are 3 types of strategy, which sort the producer 
	Arraylist respecting each distributor criteria.

### Difficulties

	For the complex tests, I had a problem because my Observer implementation
	was removing the producer's current distributors all at once and when 
	it came to choose again, some distributors arrived at different producers
	from the ones showed in ref. To solve this, I had to remove the
	distributors one by one.
	
	Also it was a problem with the distributorIds
	order because it wasn't sorted from the begin, but I solved using Collections.
	
	
## Feedback
	* easier tests comparing to the ones from phase 1, but it was good because
	I had no problems with the bankrupt consumers/distributors and contract
	prices.
	* checker/README nice
