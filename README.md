Running the Autobahn Server Tests
=================================

Install the Autobahn wstest command line tool
---------------------------------------------

The full installation instructions can be found at [http://autobahn.ws/testsuite/installation]

    $ sudo apt-get install python python-dev python-twisted
    $ sudo apt-get install python-setuptools
    $ sudo easy_install autobahntestsuite


Compile Jetty Test Server
-------------------------

    $ cd jetty-autobahn-websocket-webapp
    $ mvn clean install


Run Jetty Test Server
---------------------

    $ mvn exec:exec

The server is now started, and listening on [ws://localhost:9001/]


Run the websocket tests
-----------------------

    $ wstest --mode=fuzzingclient --spec=fuzzingclient.json


