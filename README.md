# simplewebcrawler

A simple webcrawler for crawling through list of urls specified in 

https://s3.amazonaws.com/fieldlens-public/urls.txt

To run the application

Follow the steps below to run the application. Maven should be installed on your machine to run the application. The target directory contains the executable jar. The output of the program is generated to results.txt file which is at the root of the application folder.

 - download the zip file from github
 - unzip the file
 - open terminal and cd into com.sn.webcrawler folder
 - Run mvn install 
	mvn clean install or mvn install -Dmaven.test.skip=true
 - cd into target directory 
	cd target
 - Run the application
	java -jar search-com.sn.webcrawler-0.0.1-SNAPSHOT.jar
 - View the results
	cd ..
	vi com.sn.webcrawler/results.txt


How to install maven 

https://maven.apache.org/download.cgi

https://maven.apache.org/install.html