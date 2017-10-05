Running the Autobahn Server Tests
=================================

Install the Autobahn wstest command line tool
---------------------------------------------

The full installation instructions can be found at [http://autobahn.ws/testsuite/installation.html]

**Debian / Ubuntu**

    $ sudo apt-get install python python-dev python-twisted
    $ sudo apt-get install python-pip
    $ sudo pip install autobahntestsuite

    
**Redhat / Fedora**

    $ sudo yum install python python-dev python-pip twisted
    $ sudo yum install libffi-devel
    $ sudo pip install autobahntestsuite

Note: to update your autobahntestsuite use ...

    $ sudo pip install -U autobahntestsuite

Run Jetty
---------

    $ cd jetty-autobahn-websocket-webapp
    $ mvn exec:exec


Run the websocket tests
-----------------------

    $ wstest --mode=fuzzingclient --spec=fuzzingclient.json


