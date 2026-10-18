@echo off
set LIB_DIR=lib
set JAVAFX_MODULES=javafx.controls,javafx.fxml

if not exist "%LIB_DIR%" (
  echo ERROR: JavaFX lib folder not found: %LIB_DIR%
  exit /b 1
)

java --module-path "%LIB_DIR%" --add-modules %JAVAFX_MODULES% -cp out cinema.Main
if errorlevel 1 (
  echo Runtime failed.
  exit /b 1
)
