@echo on
rem This is shim.bat
rem Change this to your development directory:
cd C:\Users\FERMAT\workspace\MyWebServer
echo "We are now in a shim called from the Web Browser"
echo Arg one is: %1
rem Change this to point to your Handler directory:
cd C:\Users\FERMAT\workspace\MyWebServer\bin\
rem pause
rem have to set classpath in batch, passing as arg does not work.
rem Change this to point to your own Xstream library files:
set classpath=%classpath%C:\Users\FERMAT\workspace\MyWebServer\bin\;C:\Users\FERMAT\workspace\MyWebServer\xstream-1.2.1.jar;C:\Users\FERMAT\workspace\MyWebServer\xpp3_min-1.1.3.4.O.jar
rem pass the name of the first argument to java:
 java -Dfirstarg=%1 BCHandler
pause
