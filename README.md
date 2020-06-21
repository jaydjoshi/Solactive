# Solactive Code Challenge

Solactive code challenge solution by Jay Joshi.

## How to run the project?

```
git clone https://github.com/jaydjoshi/Solactive.git
cd to the cloned project
./mvnw spring-boot:run
```

## Assumptions
1. No updates are performed to tick in the application
2. price of an instrument cannot be negative
3. We will get data for roughly 50,000 companies in the world
source : https://www.theglobaleconomy.com/rankings/listed_companies/

## Improvements
1. More testing
2. Write JMH tests

## TODO Priority
1. accuracy
	- implement get call scenarios (done)
2. efficiency
	- use, volatile, atomic and final (done). reCalculate
3. testing
	JMH and Jmeter and curl
4. Use cas instead of locking
	


## Whether you liked the challenge?
Absolutely! challenge is fantastic. had a great time working on it. Kudos to the team who created this challenge. :)
