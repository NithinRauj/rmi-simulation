compile:
	javac ProcessObject.java
	javac ProcessObjectNode.java
	javac ProcessClient.java
	javac Message.java

p1:
	java ProcessObjectNode p1

p2:
	java ProcessObjectNode p2

p3:
	java ProcessObjectNode p3

p4:
	java ProcessObjectNode p4

client:
	java ProcessClient

clean:
	rm -f *.class
