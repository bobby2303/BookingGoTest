# BookingGoTest #
 Bobby Rangoonwala - My attempt at the BookingGo Technical Test

Quick Note:
* I have changed the response timeout from 2 seconds to 5 seconds due to many cases where responses were empty as a result of not having enough time to be retrieved from the API.
* This can be changed back to 2 seconds by changing line 20 in both `Part1/Client.java` and `Part2/FindOptions.java`:

Before:
	`.readTimeout(5000, TimeUnit.MILLISECONDS).build(); `
	
After
	`.readTimeout(2000, TimeUnit.MILLISECONDS).build(); `
 
 ## Dependencies ##
This program was written using the following:
* Java 8 
* [JSON in Java](https://mvnrepository.com/artifact/org.json/json/20190722) - version 2019-07-22
* [Kotlin Standard Library](https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-stdlib/1.3.50) - version 1.3.50
* [Kotlin Standard Library Common](https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-stdlib-common/1.3.50) - version 1.3.50
* [OkHttp](https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp/4.2.2) - version 4.2.2
* [Okio](https://mvnrepository.com/artifact/com.squareup.okio/okio/2.0.0-RC1) - version 2.0.0

## Setup ##
All the required libraries are included within the source folders and are utilised using the -cp command during compiliation and execution.

Begin by cloning this repository:
`https://github.com/bobby2303/BookingGoTest.git`

## Part 1

### Console application to print the search results for Dave's Taxis

Navigate:
`cd Part1`

Compile:
`javac *.java -cp .:json-20190722.jar:okhttp-4.2.2.jar:okio-2.0.0.jar:kotlin-stdlib-common-1.3.50.jar:kotlin-stdlib-1.3.50.jar`

Execute (add parameters): 
`java -cp .:json-20190722.jar:okhttp-4.2.2.jar:okio-2.0.0.jar:kotlin-stdlib-common-1.3.50.jar:kotlin-stdlib-1.3.50.jar Client [pickupLatitude] [pickupLongitude] [dropoffLatitude] [dropoffLongitude] DAVE`

Example Execute: 
`java -cp .:json-20190722.jar:okhttp-4.2.2.jar:okio-2.0.0.jar:kotlin-stdlib-common-1.3.50.jar:kotlin-stdlib-1.3.50.jar Client 51.470020 -0.454295 58.167241 -0.53187 DAVE`

### Console application to filter by number of passengers

Navigate:
`cd Part1`

Compile:
`javac *.java -cp .:json-20190722.jar:okhttp-4.2.2.jar:okio-2.0.0.jar:kotlin-stdlib-common-1.3.50.jar:kotlin-stdlib-1.3.50.jar`

Execute (add parameters): 
`java -cp .:json-20190722.jar:okhttp-4.2.2.jar:okio-2.0.0.jar:kotlin-stdlib-common-1.3.50.jar:kotlin-stdlib-1.3.50.jar Client [pickupLatitude] [pickupLongitude] [dropoffLatitude] [dropoffLongitude] [maxPassengers]`

Example Execute: 
`java -cp .:json-20190722.jar:okhttp-4.2.2.jar:okio-2.0.0.jar:kotlin-stdlib-common-1.3.50.jar:kotlin-stdlib-1.3.50.jar Client 51.470020 -0.454295 58.167241 -0.53187 16`

## Part 2

Navigate:
`cd Part2`

Compile:
`javac *.java -cp .:json-20190722.jar:okhttp-4.2.2.jar:okio-2.0.0.jar:kotlin-stdlib-common-1.3.50.jar:kotlin-stdlib-1.3.50.jar`

Start server - running on localhost:8080: 
`java -cp .:json-20190722.jar:okhttp-4.2.2.jar:okio-2.0.0.jar:kotlin-stdlib-common-1.3.50.jar:kotlin-stdlib-1.3.50.jar Server`

URL for API request (add parameters): 
`http://localhost:8080/taxis?dropoff=[dropoffLatitude],[dropoffLongitude]&pickup=[pickupLatitude],[pickupLongitude]&maxPassengers=[maxPassengers]`

Example URL:
`http://localhost:8080/taxis?dropoff=51.470020,-0.454295&pickup=52.167241,-0.443187&maxPassengers=16`
