Running the Autobahn Server Tests
=================================

Install the Autobahn wstest command line tool
---------------------------------------------

The full installation instructions can be found at [http://autobahn.ws/testsuite/installation]

    $ sudo apt-get install python python-dev python-twisted
    $ sudo apt-get install python-setuptools
    $ sudo easy_install autobahntestsuite


Run Jetty
---------

    $ cd jetty-autobahn-websocket-webapp
    $ mvn exec:exec


Run the websocket tests
-----------------------

    $ wstest --mode=fuzzingclient --spec=fuzzingclient.json


