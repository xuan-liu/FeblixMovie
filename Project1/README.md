# # cs122b-spring20-team-38 Project1

###  Demo video URL
https://www.youtube.com/watch?v=WICHBGzBS5g&feature=youtu.be

### Deploy the application with Tomcat

1. Clone this repository using  `git clone <repo url>`
2. Inside the repo, `cd Project1` Then build the war file:  `mvn clean package` The war file is in the "target" folder.
3. Copy the WAR file to "tomcat_directory/webapps". For example "`cp target/project1.war tomcat_directory/webapps/`"
4.  Refresh the tomcat manager page. You should see a new project (just deployed): project 1.
5.  Click the project link, which goes to the website's landing page.

### Each member's contribution
Hongen Lei: Create movie list page, single movie page and single star page.

Xuan Liu: Create database tables, implement jump requirement, UI improvement.