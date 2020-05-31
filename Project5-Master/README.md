#Master server

##Before running the project on your AWS Tomcat Server:

1. Add rule to the security group to open the mysql port 3306 to your slave server. (Also your local machine if testing locally)

2. Run the following commands in your Master aws if you have not done so:
        shell> mysql -u root -p
	mysql> CREATE USER 'mytestuser'@'%' IDENTIFIED BY 'mypassword';
	mysql> GRANT ALL PRIVILEGES ON * . * TO 'mytestuser'@'%';
	mysql> flush privileges
	shell> sudo /etc/init.d/mysql restart