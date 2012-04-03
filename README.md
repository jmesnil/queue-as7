# Example of using JMS Queues in AS7

This project shows how to use [Arquillian][arquillian] to test code using JMS resources provided by [HornetQ][hornetq] hosted in [JBoss AS 7][jbossas].

## Configuration

configure `src/test/resources/arquillian.xml` to specify the home directory where you have install JBoss AS 7 (or comment the line if you have set `JBOSS_HOME`)

## Run test in a shell

In a shell, type:

    $ mvn clean test -Parq-jbossas-managed

## Run in Eclipse

Import the Maven project into the Eclipse workspace. 

To execute the test, you need first to enable the Arquillian's profile for JBoss AS7 container

1. Right click on the project and select `Properties`
2. Select the `Maven` property sheet
3. In the first form field, enter  `arq-jbossas-managed`
4. Uncheck `Resolve dependencies from Workspace projects`

Once this is done, you can run the test `QueueTestCase` as a regular JUnit test.
Arquillian will start an instance of JBoss AS 7 with HornetQ enabled (using its `standalone-full.xml` configuration) and deploy the `hornetq-jms.xml` that declares the JMS Queue `/queue/test` that is used by the test.

[jbossas]: http://www.jboss.org/jbossas
[arquillian]: http://www.jboss.org/arquillian
[hornetq]: http://www.jboss.org/hornetq


