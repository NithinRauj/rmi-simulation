compile:
	javac ProcessObject.java
	javac ProcessObjectNode.java
	javac ProcessClient.java
	javac Message.java

p1:
	java ProcessObjectNode rmi://localhost:6000/ p1

p2:
	java ProcessObjectNode rmi://localhost:6000/ p2

p3:
	java ProcessObjectNode rmi://localhost:6000/ p3

p4:
	java ProcessObjectNode rmi://localhost:6000/ p4

client:
	java ProcessClient rmi://localhost:6000/

clean:
	rm -f *.class
