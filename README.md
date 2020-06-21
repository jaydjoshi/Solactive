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
3. We will get data for roughly 50,000 companies from various stock exchanges in the world
source : https://www.theglobaleconomy.com/rankings/listed_companies/
Hence, set initial value of map to 50,000

## Improvements
1. More testing
2. Write JMH tests
3. create utility methods class and move common methods from aggregation class to Utility class
4. Integrate JMeter tests in the project
5. There is lot of duplicate code in TickerAggregatorNonBlocking and TickerAggregator class. I was trying multiple options for testing hence the duplicate code.
once benchmarking is done, we should remove of the 2 classes

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
