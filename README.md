# COMP2121 - Blockchains

### Overview

This repository contains a very basic implementation of Blockchains for an assignment for the unit of study - [COMP2121: Principles of Distributed Systems and Networks](https://cusp.sydney.edu.au/students/view-unit-page/alpha/COMP2121) at [The University of Sydney](https://sydney.edu.au/).

### Prerequisites

The following packages are required for running this application.
```
JDK 8
JRE 8
```
Install both from [here](https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html).
### Starting the Application

There are two applications - a server and a client. Both applications are multithreaded implemented using thread pools. 

Both applications need to be compiled before use. Use the following command to compile from the project folder.
```
mkdir bin
javac -d bin src/*.java
```

The client application can be started on the terminal/command prompt using the following command
```
java -cp bin BlockchainServer [port]
```

where port is any valid port you want to start a server on your machine.

Start the client application on the same machine or any other machine that can connect to your server through a network using the following command.
```
java -cp bin BlockchainClient [config pathfile]
```

The client preloads servers from the config file. Read the [Assignment Specifications](assnspec.pdf) for a list of broader functions that can be performed using these applications.

## Built With

[Java](https://docs.oracle.com/javase/8/docs/api/overview-summary.html) 

## Authors

* **Nahian Al Hasan** - *Initial work* - [bitnahian](https://github.com/bitnahian)

See also the list of [contributors](https://github.com/your/project/contributors) who participated in this project.

## License

This project is licensed under the GNU 3.0 License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* [Dr. Vincent Gramoli](http://sydney.edu.au/engineering/people/vincent.gramoli.php)

