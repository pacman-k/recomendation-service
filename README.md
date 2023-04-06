# recommendation-service
***
**SpringBoot application that exposes APIs for retrieving Crypto statistic.**
## Requirements
***
**Java 11 or docker installed**
## StartUp
***
**Run `startup` or `startup.bat`.</br>Default port is specified in application.yml - `8080`.**
## Usage
***
```GET:${host}:8080/cryptos/${symbol}/stats?m=${count_of_months}``` **--- returns aggregated statistic (oldest/newest/min/max prices)
for specified crypto. `m` - is optional parameter determines the count of last months to read statistic for.**</br>
```GET:${host}:8080/cryptos/ranking?m=${count_of_months}``` **--- returns descending sorted list of all aggregated crypto statistics (oldest/newest/min/max prices) comparing the normalized range (i.e. (max-min)/min)
. `m` - is optional parameter determines the count of last months to read statistic for.**</br>
```GET:${host}:8080/cryptos/ranking/${date}``` **--- returns aggregated crypto statistic (oldest/newest/min/max prices) with the highest normalized range (i.e. (max-min)/min) for specified day
. `date` - should be with next pattern: yyyy-MM-dd.**</br>
## Datastore
****
**Data is stored in `csv` files from resource folder.**<br/>
Data is presented for the next cryptos: `BTC`, `DOGE`, `ETH`, `LTC`, `XRP`<br/>
Data is presented for the next time period: `2022-01-01` - `2022-01-31`
