@echo off
setlocal enabledelayedexpansion
set LIB_DIR=lib
set JAVAFX_MODULES=javafx.controls,javafx.fxml

if not exist "%LIB_DIR%" (
  echo ERROR: JavaFX lib folder not found: %LIB_DIR%
  exit /b 1
)

if not exist out (
  mkdir out
)

javac --module-path "%LIB_DIR%" --add-modules %JAVAFX_MODULES% -d out -sourcepath src src\cinema\Main.java src\cinema\CinemaHouseManagementSystem.java
if errorlevel 1 (
  echo Compilation failed.
  exit /b 1
)
echo Compilation complete.
