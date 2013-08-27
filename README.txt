System Event Tracker
-------------------------
Put system/network wide events into a database and look at them later in a timeline.

Running
--------
The war file can be put into a webapp container, like tomcat, or run directly from the commandline as java -jar evet.war

Configuration
---------------
The configuration is searched for first via its System property (-Dname=var) and then via its environment variable.

Http port - what port to listen to for http requests (commandline only)
  property: evet.httpPort
  env var:  EVET_HTTP_PORT
  Default:  8080

MongoDB server host - the host name where the mongo is running.
  property: evet.DbHost
  env var:  EVET_DB_HOST
  Default:  localhost

MongoDB database name - the name of the database in the Mongo server
  property: evet.DbName
  env var:  EVET_DB_NAME
  Default:  evet1

MongoDB user name - the username to use when logging into the Mongo.
  property: evet.DbUser
  env var:  EVET_DB_USER
  Default:  <NO_USER>

MongoDB password - the password for the user logging into the Mongo.
  property: evet.DbPassword
  env var:  EVET_DB_PASSWORD
  Default:  <NO_PASSWORD>

REST Endpoints
/event
  Parameters: system, title, <node>, <description>, <tags[]>
A non duration event.
If node is not specified the client host name will be used.

/startEvent
  Parameters: <id>, system, title, <node>, <description>, <tags[]>
The start of a duration event.
Id is optional, the id of the created event will always be returned in json format,
but a client can specify its own id if it wants to make things simple. NO INPUT VALIDATION IS PERFORMED so use some guid service if possible.
If node is not specified the client host name will be used.

/endEvent
  Parameters: id, <title>, <description>, <tags[]>
Ends a previously started duration event.
The only required parameter is the id from the startEvent, but the client can change or add tags at the end if it wants to.


Any of the endpoints can be provided any arbitrary parameters besides those "hard coded" and they will be saved into the database as well.