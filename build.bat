@echo off
echo Building ZenSpawnController...
mvn clean package
if %errorlevel% == 0 (
    echo.
    echo Build successful! 
    echo Plugin JAR created at: target\zenspawncontroller-1.0.0.jar
    echo.
    echo To install:
    echo 1. Copy the JAR to your server's plugins folder
    echo 2. Restart your server
    echo 3. Configure using /zenspawn commands
) else (
    echo Build failed! Check the output above for errors.
)
pause
