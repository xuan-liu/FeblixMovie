#Slave server

##Please read the README.md of the Master Project before running the Slave Project

##Before running the project on your AWS Tomcat Server:

1. Add rule to the security group to open the mysql port 3306 to your slave server. (Also your local machine if testing locally)

2. Change the the IP address of the url of the resource named "movie_write"(line 24, context.xml) to your master's private IP.