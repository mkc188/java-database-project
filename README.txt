CSCI3170 GROUP PROJECT
FILE: README.txt
DATE: 22/11/2013



* Included Files
  FILE PATH               | DESCRIPTION
  -----------------------------------------------------------------
  ./README.txt            | this file.
  ./web/                  | contains all files of web interface.
  ./web/database.php      | establish connection to Oracle DB.
  ./web/index.php         | php search page.
  ./web/style.css         | style sheet for the search page.
  ./java/                 | contains all files of java application.
  ./java/SalesSystem.java | main java class.


* Methods of Compilation and Execution
   JAVA APPLICATION
   ----------------
1. Compile the java file with the following command:
       javac SalesSystem.java
2. Use command "java -classpath ./ojdbc6.jar:./ SalesSystem.class"
   to run the program.
3. Follow the project specification to access the sale system.

   WEB INTERFACE
   ----------------
1. Copy all files in ./web/ to the path and
   change files permission to 600 for all PHP files.
2. Edit database.php with database hostname, username and password:
   - Line 9 contains hostname.
   - Line 10 contains username.
   - Line 11 contains password.
3. Save the edited file and
   the search page would work at coordinated path.
