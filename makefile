####
# Java Makefile
####

JFLAGS = -g -cp ../bin -d ../bin
JC = javac
#JVM = java
FILE=

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
    Constants.java \
    Tile.java \
    Runtime.java \
    Database.java \
    Interaction.java \
    Board.java \
    Company.java \
    Player.java \
    Interactions/Dummy.java \
    Interactions/DummyInputTile.java \
    Session.java \
    Game.java \

MAIN = Game

default: classes

classes: $(CLASSES:.java=.class)

run: $(MAIN).class
	$(JVM) $(MAIN)

clean:
	rmdir /s /q ..\bin\game
