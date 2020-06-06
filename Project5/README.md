- # General
    - #### Team#: cs122b-spring20-team-38
    
    - #### Names: Xuan Liu, Hongen Lei
    
    - #### Project 5 Video Demo Link:

    - #### Instruction of deployment: 
	1. Clone this repository
	2. Locate "Project5-Master" and "Project5-Slave". Then build the war file for each.
	3. Deploy the Master's war file to the master instance that has already been set up.
	4. Deploy the Slave's war file to the slave instance that has already been set up.
	5. Modify the Balancer instance to balance the assignment of connections between the two servers.

    - #### Collaborations and Work Distribution:


- # Connection Pooling
    - #### Include the filename/path of all code/configuration files in GitHub of using JDBC Connection Pooling.
        ##### Configuration files:
            Project5\web\META-INF\context.xml
            Project5\web\WEB-INF\web.xml
            Project5-Slave\web\META-INF\context.xml
            Project5-Slave\web\WEB-INF\web.xml

	    ##### Code files:
            Project5\src\AddMovieServlet.java
            Project5\src\AddStarServlet.java
            Project5\src\AdminLogin.java
            Project5\src\CategoryResultServlet.java
            Project5\src\LoginServlet.java
            Project5\src\MetaDataServlet.java
            Project5\src\MovieSuggestion.java
            Project5\src\PaymentServlet.java
            Project5\src\ResultServlet.java
            Project5\src\SingleMovieServlet.java
            Project5\src\SingleStarServlet.java
		
    
    - #### Explain how Connection Pooling is utilized in the Fabflix code.
    
    - #### Explain how Connection Pooling works with two backend SQL.
    

- # Master/Slave
    - #### Include the filename/path of all code/configuration files in GitHub of routing queries to Master/Slave SQL.
        ##### Master Configuration files:
            Project5-Master\web\META-INF\context.xml
            Project5-Master\web\WEB-INF\web.xml
           
        ##### Master Code files:
            Project5-Master\src\AddMovieServlet.java
            Project5-Master\src\AddStarServlet.java
            Project5-Master\src\AdminLogin.java
            Project5-Master\src\CategoryResultServlet.java
            Project5-Master\src\LoginServlet.java
            Project5-Master\src\MetaDataServlet.java
            Project5-Master\src\MovieSuggestion.java
            Project5-Master\src\PaymentServlet.java
            Project5-Master\src\ResultServlet.java
            Project5-Master\src\SingleMovieServlet.java
            Project5-Master\src\SingleStarServlet.java
         
        ##### Slave Configuration files:
            Project5-Slave\web\META-INF\context.xml
            Project5-Slave\web\WEB-INF\web.xml
           
        ##### Slave Code files:
            Project5-Slave\src\AddMovieServlet.java
            Project5-Slave\src\AddStarServlet.java
            Project5-Slave\src\AdminLogin.java
            Project5-Slave\src\CategoryResultServlet.java
            Project5-Slave\src\LoginServlet.java
            Project5-Slave\src\MetaDataServlet.java
            Project5-Slave\src\MovieSuggestion.java
            Project5-Slave\src\PaymentServlet.java
            Project5-Slave\src\ResultServlet.java
            Project5-Slave\src\SingleMovieServlet.java
            Project5-Slave\src\SingleStarServlet.java
    - #### How read/write requests were routed to Master/Slave SQL?
        - In the Master Instance, read and write requests are sent directly to the SQL without rerouting.
        - In the Slave Instance, we have 2 MYSQL resources in the context file. One refers to the MYSQL server of the Slave instance. Another refers to the master MYSQL server. All the read requests will be sent to the Slave MYSQL server. And all the write requests are sent to the Slave MYSQL server.
    

- # JMeter TS/TJ Time Logs
    - #### Instructions of how to use the `log_processing.*` script to process the JMeter logs.


- # JMeter TS/TJ Time Measurement Report

| **Single-instance Version Test Plan**          | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 2: HTTP/10 threads                        | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 3: HTTPS/10 threads                       | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 4: HTTP/10 threads/No connection pooling  | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |

| **Scaled Version Test Plan**                   | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 2: HTTP/10 threads                        | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 3: HTTP/10 threads/No connection pooling  | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
