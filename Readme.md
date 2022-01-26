# File versioning

It is created to maintain file versions along and switching back to any version whenever needed.

## Installation

### For Development
1>clone the repository.
2> upload it as a maven project in you eclipse
3> change database configuration to local from production
4> create database svn
5> run and enjoy

### For production
1> Clone the repository.
2> Form project root run command ``` docker build -t=fileversioning:v1 .```
3>create database svn
4> run docker command docker docker run -p 8080:8080 -e MYSQL_JDBC_URL=jdbc:mysql://<your ip here>:3306/svn -e MYSQL_USERNAME=root -e MYSQL_PASSWORD=  fileversioning:v1 
5> access application at localhost:8080/tasksvn/
6> run and enjoy

## How to run

1> upload file that you want to have as a version
2> once file is uploaded select file form the dropdown 
3> once the file is selected in the dropdown you will see all its versions in other dropdown
4> on selecting its latest version all the content will be displayed to you
5> you can alter the content and when you click on submit a new version will be created 
6> if you will select the older version of file and click on submit after editing it then it will 
   rollback to that particular version eleminating all the other versions.
