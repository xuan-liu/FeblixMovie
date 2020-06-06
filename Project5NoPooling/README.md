# cs122b-spring20-team-38 Project4
  
### Demo video URL  
  
https://youtu.be/cLADEvmdbbk
  
### Deploy the application with Tomcat  
  
1. Clone this repository using  `git clone <repo url>`  
2. Inside the repo,  `cd Project4` Then build the war file:  `mvn clean package` The war file is in the "target" folder.  
3. Copy the WAR file to "tomcat_directory/webapps". For example "`cp target/project4.war tomcat_directory/webapps/`"  
4. Refresh the tomcat manager page. You should see a new project (just deployed): project 4.  
5. Click the project link, which goes to the website's landing page.  

If you want to deploy and run the Android App, see more details in Project4Task2.

### Each member's contribution  

Hongen Lei: Improve the Fabflix by Full-text Search and Autocomplete. Implement the Login Page for the Android App.

Xuan Liu: Implement the Search Page, Movie List Page and Single Movie Page for the Android App.