
javac -cp \Source\Java\bin -d \Source\Java\bin Constants.java
@IF %ERRORLEVEL% NEQ 0 GOTO ERROR

javac -cp \Source\Java\bin -d \Source\Java\bin Tile.java
@IF %ERRORLEVEL% NEQ 0 GOTO ERROR

javac -cp \Source\Java\bin -d \Source\Java\bin Runtime.java
@IF %ERRORLEVEL% NEQ 0 GOTO ERROR

javac -cp \Source\Java\bin -d \Source\Java\bin Database.java
@IF %ERRORLEVEL% NEQ 0 GOTO ERROR

javac -cp \Source\Java\bin -d \Source\Java\bin Interaction.java
@IF %ERRORLEVEL% NEQ 0 GOTO ERROR

javac -cp \Source\Java\bin -d \Source\Java\bin Board.java
@IF %ERRORLEVEL% NEQ 0 GOTO ERROR

javac -cp \Source\Java\bin -d \Source\Java\bin Company.java
@IF %ERRORLEVEL% NEQ 0 GOTO ERROR

javac -cp \Source\Java\bin -d \Source\Java\bin Player.java
@IF %ERRORLEVEL% NEQ 0 GOTO ERROR

javac -cp \Source\Java\bin -d \Source\Java\bin Interactions\Dummy.java
@IF %ERRORLEVEL% NEQ 0 GOTO ERROR

javac -cp \Source\Java\bin -d \Source\Java\bin Session.java
@IF %ERRORLEVEL% NEQ 0 GOTO ERROR

javac -cp \Source\Java\bin -d \Source\Java\bin Game.java
@IF %ERRORLEVEL% NEQ 0 GOTO ERROR

@GOTO END

:ERROR
@echo Compile Error

:END
