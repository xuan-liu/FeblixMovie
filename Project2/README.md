# cs122b-spring20-team-38 Project2

### Demo video URL

https://youtu.be/TA4Jg_LJgqs

### Deploy the application with Tomcat

1.  Clone this repository using  `git clone <repo url>`
2.  Inside the repo,  `cd Project2`  Then build the war file:  `mvn clean package`  The war file is in the "target" folder.
3.  Copy the WAR file to "tomcat_directory/webapps". For example "`cp target/project1.war tomcat_directory/webapps/`"
4.  Refresh the tomcat manager page. You should see a new project (just deployed): project 2.
5.  Click the project link, which goes to the website's landing page.

### Substring matching design

In “browse by alphanumerical characters” part, we use "**movies.title like category_param%**". category_param is the alphanumerical characters (0,1,2,3..A,B,C...X,Y,Z).

In “search movie” part, we use "**movies.title like %substring%**”; "**movies.director like %substring%**”; "**movies.star like %substring%**”. Substring is the keyword user searched.

### Each member's contribution

Hongen Lei: Implement the login page, the main page (with search and browse), the movie list page, single pages and jump functionality

Xuan Liu: Implement the shopping cart, UI improvement