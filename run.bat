@echo off
REM Try to run the JavaFX app using Maven (requires Maven or the Maven wrapper)
if exist mvnw.cmd (
  echo Running via mvnw.cmd...
  mvnw.cmd javafx:run
  goto :eof
)

where mvn >nul 2>&1
if %ERRORLEVEL%==0 (
  echo Running via local Maven...
  mvn javafx:run
  goto :eof
)

echo Could not find mvn or mvnw.cmd in the project root.
echo Please install Maven (https://maven.apache.org/install.html) or run the project from an IDE (IntelliJ/IDEA) that supports JavaFX.
pause

