@echo off
cd /d "d:\FAKS_4\SBZ\PROJEKAT\sbz\kjar-example\drools-spring-app"
echo Starting Social Network Backend API...
echo.
echo PostgreSQL should be running on localhost:5432
echo Database: sbz
echo Username: postgres
echo Password: super
echo.
echo API will be available at: http://localhost:8080
echo.
mvn spring-boot:run
pause