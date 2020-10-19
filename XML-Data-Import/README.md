# Import large XML data files into the Fabflix database

## Run the project

Before running it, it's recommended to back up your "moviedb" database. You can follow the step.
1. create a database named "moviedb_backup".
2. `mysqldump -u root -p moviedb > moviedb.sql`
3. `mysql -u root -p moviedb_backup < moviedb.sql`


Then you can run the project.
1. Prepare "actors63.xml", "casts124.xml" and "mains243.xml" files in a folder named "data". The folder should be in the same path as this file.
2. Clone this repository using `git clone <repo url>`
3. `cd XML-Data-Import`
4. `mvn package`
5. `time java -cp target/cs122b-spring20-project3-SAXParser-example-0.0.1-SNAPSHOT.jar:/home/ubuntu/.m2/repository/mysql/mysql-connector-java/5.1.46/mysql-connector-java-5.1.46.jar SAXParserCast` Using "time" at first can track the time the project consumes.
