
rmdir /s /q \Source\Java\bin\game
@IF %ERRORLEVEL% NEQ 0 GOTO ERROR

@GOTO END

:ERROR
@echo Compile Error

:END
