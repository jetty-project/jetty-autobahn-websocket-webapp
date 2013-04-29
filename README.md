Running the Autobahn Server Tests
=================================

Install the Autobahn wstest command line tool
---------------------------------------------

The full installation instructions can be found at [http://autobahn.ws/testsuite/installation]

    $ sudo apt-get install python python-dev python-twisted
    $ sudo apt-get install python-setuptools
    $ sudo easy_install autobahntestsuite


Install JDK7
------------

On Ubuntu, you can install and activate OpenJDK7 by doing

    $ sudo apt-get install openjdk-7-jdk
    $ sudo update-alternatives --config java
    $ sudo update-alternatives --config javac
    $ export JAVA_HOME=/usr/lib/jvm/java-1.7.0-openjdk-amd64/

For more information on alternative JDKs, see [here](https://help.ubuntu.com/community/Java).


Build and run Jetty
-------------------

    $ cd jetty-autobahn-websocket-webapp
    $ mvn clean install
    $ mvn exec:exec


Run the websocket tests
-----------------------

    $ wstest --mode=fuzzingclient --spec=fuzzingclient.json
