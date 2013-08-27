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