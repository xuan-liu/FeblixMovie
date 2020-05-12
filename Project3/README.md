# cs122b-spring20-team-38 Project3 
  
### Demo video URL  
  
https://youtu.be/mxS31EBOKQY
  
### Deploy the application with Tomcat  
  
1. Clone this repository using  `git clone <repo url>`  
2. Inside the repo,  `cd Project3` Then build the war file:  `mvn clean package` The war file is in the "target" folder.  
3. Copy the WAR file to "tomcat_directory/webapps". For example "`cp target/project3.war tomcat_directory/webapps/`"  
4. Refresh the tomcat manager page. You should see a new project (just deployed): project 3.  
5. Click the project link, which goes to the website's landing page.  

If you want to importing the XML data files, see more details in Project3Task7.
  
### Prepared Statement  

[LoginServlet.java](src/LoginServlet.java)

[AddMovieServlet.java](src/AddMovieServlet.java)

[AddStarServlet.java](src/AddStarServlet.java)

[ResultServlet.java](src/ResultServlet.java)

[SingleMovieServlet.java](src/SingleMovieServlet.java)

[SingleStarServlet.java](src/SingleStarServlet.java)

[PaymentServlet.java](src/PaymentServlet.java")

### Import XML data: Performance tuning

We used batch inserts, auto commit off, prepared statement to reduce the running time the program. Besides, we used two parsing time optimization strategies.

1.  In-memory hash tables. We add the data in this order: insert all stars, insert all movies and genres, insert all the relationships of star and movie. When we insert the stars and movies, there are some information that is useful in the future. For example, inserting relationship needs the star's id and movie's id that comes from previous steps. So we store the hash tables of star <stageName, starId> and movie <fid, movieId> in memory.

2.  Loading stars information of database in memory. Rather than always querying the database for whether the star already exists, we load the stars table of moviedb in memory, and store all the records in the hash table. Then when we need insert stars, we just need to check whether the hash table contains the star’s name.

Note: We use stage name as a star’s name.

### Import XML data: Inconsistent data report from parsing

After running Project3Task7, the Inconsistent data report can be seen in “Project3Task7/inconsistent_report.txt”

### Each member's contribution  

Hongen Lei: Add reCAPTCHA, HTTPS, prepared statement, encrypted password; Implement a dashboard using stored procedure.

Xuan Liu: Add HTTPS, prepared statement, encrypted password; Import large XML data files into the Fabflix database.
