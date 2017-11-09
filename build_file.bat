
javac -cp \Source\Java\bin -d \Source\Java\bin %1.java
@IF %ERRORLEVEL% NEQ 0 GOTO ERROR

@GOTO END

:ERROR
@echo Compile Error

:END
