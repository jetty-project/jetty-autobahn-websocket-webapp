Running the Autobahn Server Tests
=================================

Grab yourself a copy of autobahn
--------------------------------

    $ git clone https://github.com/oberstet/Autobahn


Install the python dependencies
-------------------------------

    $ sudo apt-get install python python-dev python-twisted
    $ sudo apt-get install python-setuptools


Install Autobahn itself
-----------------------

    $ cd Autobahn
    $ git checkout v0.4.2
    $ cd lib/python
    $ sudo python setup.py install


Configure Autobahn for testing with jetty
-----------------------------------------

    $ cd Autobahn/testsuite/websockets
    $ vim fuzzing_client_spec.json

Note: make sure the agent, hostname, and port make sense.
The defaults present in git tag v0.4.2 are generally sane and only the agent needs updating.


Run Jetty
---------

    $ cd jetty-autobahn-websocket-webapp
    $ mvn exec:exec


Run the websocket tests
-----------------------

    $ cd Autobahn/testsuite/websocket
    $ python fuzzing_client.py


