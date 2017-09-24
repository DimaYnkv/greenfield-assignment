# This is Simple Vert.x-web Server for word analysis - test assignment

Used maven dependencies are:
vertx-web and jackson-databind

To compile and run:

```
mvn clean compile
```

```
mvn org.codehaus.mojo:exec-maven-plugin:exec -Dexec.executable=java \
	-Dexec.args="-cp %classpath io.vertx.core.Launcher run test.project1.Server"
```

Tested using Postman on the following address:
```
http://localhost:8080/analyze
```
Parameters:
```
name: 'text', type: string
```
