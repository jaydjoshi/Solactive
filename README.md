# Solactive Code Challenge

Solactive code challenge solution by Jay Joshi.

## How to run the project?

```
git clone https://github.com/jaydjoshi/Solactive.git
cd to the cloned project
./mvnw spring-boot:run
```

## Assumptions
1. No updates are performed to tick in the application. Application supports only inserts. if same timestamp is entered twice, we will consider both as valid.
2. Price of an instrument cannot be negative
3. We will get data for roughly 50,000 companies from various stock exchanges in the world
source : https://www.theglobaleconomy.com/rankings/listed_companies/
Hence, set initial value of map to 50,000

## Improvements
1. More testing
2. Write JMH tests
3. create utility methods class and move common methods from aggregation class to Utility class
4. Integrate JMeter tests in the project
5. There is lot of duplicate code in TickerAggregatorNonBlocking and TickerAggregator class. I was trying multiple options for testing hence the duplicate code.
once benchmarking is done, I would remove one of the 2 classes
6. As we are dealing with financial data we should use BigDecimal instead of double

## JMeter test results
Used Jmeter GUI to test post API calls.
Used NYSE.csv data in src/main/resources/static folder

### With 10 users and 10,000 iterations
Label|# Samples|Average|Min|Max|Std. Dev.|Error %|Throughput|Received KB/sec|Sent KB/sec|Avg. Bytes
--- | --- | --- | --- |--- |--- |--- |--- |--- |--- |--- 
POST Ticks HTTP Request|100000|83|3|1026|41.69|0.000%|119.20486|14.09|27.27|121.0
TOTAL|100000|83|3|1026|41.69|0.000%|119.20486|14.09|27.27|121.0

### With 100 users and 1000 iterations
Label|# Samples|Average|Min|Max|Std. Dev.|Error %|Throughput|Received KB/sec|Sent KB/sec|Avg. Bytes
--- | --- | --- | --- |--- |--- |--- |--- |--- |--- |--- 
POST Ticks HTTP Request|100000|907|13|3766|264.66|0.000%|109.69074|12.96|25.09|121.0
TOTAL|100000|907|13|3766|264.66|0.000%|109.69074|12.96|25.09|121.0

## Whether you liked the challenge?
Absolutely! challenge is fantastic. had a great time working on it. Kudos to the team who created this challenge. :)
