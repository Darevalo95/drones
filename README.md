<h1>Drones Application</h1>
<p>This project is a task requested by Musalasoft, to apply to a Job as
a Java Developer. Here we can find a few endpoints to perform the next actions:</p>
<li>Registering a drone</li>
<li>Loading a drone with medication items</li>
<li>Checking loaded medication items for a given drone</li>
<li>Checking available drones for loading</li>
<li>Check drone battery level for a given drone</li>
<p>The endpoints are respectively:</p>
<li>POST: http://localhost:8080/api/drones</li>
<li>GET: http://localhost:8080/api/drones/load/{serialNumber}</li>
<li>GET: http://localhost:8080/api/drones/medications/{serialNumber}</li>
<li>GET: http://localhost:8080/api/drones/available</li>
<li>GET: http://localhost:8080/api/drones/batteryCheck/{serialNumber}</li>
<p>This service is using an H2 database to save data in memory and the 
required data to start to use it, it's being loaded at the beginning of 
execution.</p>
<h2>How to run</h2>
<h3>Application</h3>
<p>This application can be run using IntelliJ feature of right-click on 
<code>DronesApplication</code> class, or running it by terminal using the 
<code>mvn</code> command: <code>mvn spring-boot: run</code>.</p>
<h3>Tests</h3>
<p>Tests can be performed using IntelliJ feature of right-click on the Java 
directory inside test directory and run 'All Tests' or running them using 
the terminal with the command: <code>mvn test</code></p>