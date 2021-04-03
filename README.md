# CSCI2020 Assignment 2

## Group members

| Group Member               | ID         |
|:--------------------------:|:----------:|
| Mariya Anashkina           | 100746854  |
| Avril Lopez van Domselaar  | 100746008  |

When running the program, the user must type a username and
select a local folder where they have the files they want to 
upload to the server, or simply where they want to store the 
downloaded files.

After that, the program sends a DIR request to the server and displays
both the Server and local directory files. from there, the user can
upload files to the server, download files from the server, delete files from the server or the local directory, and look at the content of both
local and server files.


## Screenshot

![Alt text](screenshot.png?raw=true "Screenshot")

## Framework used / built with

We used IntelliJ to layout, organize and code the program.

And we used Gradle to organise the execution of files.


## Installation / How to use?

For easy running, using any terminal, starting from the repository folder, run gradle startServer to dtart the Server.
Then, from another terminal, navigate to the repository folder and run gradle run.

The project was built with Java 11, so we recomment compiling with Java 11 or higher.

If the problem arises(UnsupportedClassVersionError) when previewing the file from the Server directory, make sure to change
the Project SDK to Java12. To do so, navigate to Project structure->Project settings->Project->Project SDK.


## Credit

For some of the classes in the program, mostly Server.java and, some functions and code structure was taken from the modules 6 and 7 course examples, to be specific, from the ChatServer example.

For the layout of the README we took inspiration from https://meakaakka.medium.com/a-beginners-guide-to-writing-a-kickass-readme-7ac01da88ab3

