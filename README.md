# Solactive Code Challenge

Solactive code challenge solution by Jay Joshi.

## How to run the project?

I ran the application on JDk 14. 

```
git clone https://github.com/jaydjoshi/Solactive.git
cd to the cloned project
./mvnw spring-boot:run
```

Junit test cases will fail with current code, to run junit test cases, uncomment below code from application.properties
```
spring.autoconfigure.exclude=net.bull.javamelody.JavaMelodyAutoConfiguration
```

## Assumptions
1. No updates are performed to tick in the application. Application supports only inserts. if same timestamp is entered twice, we will consider both as valid.
2. Price of an instrument cannot be negative
3. Future timestamps wont be received in the application
4. We might get data for roughly 50,000 companies from various stock exchanges in the world
source : https://www.theglobaleconomy.com/rankings/listed_companies/
Hence, set initial value of map to 50,000
5. Retry attempts in disruptor is set to 5 with 5 ms delays. Even after retrys we will log the tick and wont process it

## Improvements
1. More testing
2. Write JMH tests
3. Create utility methods class and move common methods from aggregation layer class to Utility class
4. Integrate JMeter tests in the project
5. There is duplicate code in TickerAggregatorNonBlocking and TickerAggregator class. I was trying multiple options for testing hence the duplicate code.
once benchmarking is done, I would remove one of the 2 classes
6. As we are dealing with financial data we should use BigDecimal instead of double

## JMeter test results
Used Jmeter GUI to test post API calls.
Used NYSE.csv data in src/main/resources/static folder

### Blocking algorithm

#### With 10 users and 10,000 iterations
Label|# Samples|Average|Min|Max|Std. Dev.|Error %|Throughput|Received KB/sec|Sent KB/sec|Avg. Bytes
--- | --- | --- | --- |--- |--- |--- |--- |--- |--- |--- 
POST Ticks HTTP Request|100000|83|3|1026|41.69|0.000%|119.20486|14.09|27.27|121.0
TOTAL|100000|83|3|1026|41.69|0.000%|119.20486|14.09|27.27|121.0

#### With 100 users and 1000 iterations
Label|# Samples|Average|Min|Max|Std. Dev.|Error %|Throughput|Received KB/sec|Sent KB/sec|Avg. Bytes
--- | --- | --- | --- |--- |--- |--- |--- |--- |--- |--- 
POST Ticks HTTP Request|100000|907|13|3766|264.66|0.000%|109.69074|12.96|25.09|121.0
TOTAL|100000|907|13|3766|264.66|0.000%|109.69074|12.96|25.09|121.0

### Non blocking algorithm

#### With 10 users and 10,000 iterations
Label|# Samples|Average|Min|Max|Std. Dev.|Error %|Throughput|Received KB/sec|Sent KB/sec|Avg. Bytes
--- | --- | --- | --- |--- |--- |--- |--- |--- |--- |--- 
POST Ticks HTTP Request|100000|3|0|127|3.78|0.000%|2881.26315|339.65|659.05|120.7
TOTAL|100000|3|0|127|3.78|0.000%|2881.26315|339.65|659.05|120.7

#### With 100 users and 1000 iterations
Label|# Samples|Average|Min|Max|Std. Dev.|Error %|Throughput|Received KB/sec|Sent KB/sec|Avg. Bytes
--- | --- | --- | --- |--- |--- |--- |--- |--- |--- |--- 
POST Ticks HTTP Request|100000|48|0|790|56.26|0.000%|1967.18732|232.45|449.97|121.0
TOTAL|100000|48|0|790|56.26|0.000%|1967.18732|232.45|449.97|121.0



## Notes
1. Upgraded com.thoughtworks.xstream to 1.4.12
2. Had to add spring.autoconfigure.exclude=net.bull.javamelody.JavaMelodyAutoConfiguration in application.properties because of failing junit test cases. Currently, it is commented, uncomment to run junit cases


## Whether I liked the challenge?
Absolutely! challenge is fantastic. had a great time working on it. Kudos to the team who created this challenge. :)
