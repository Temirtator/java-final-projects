@echo off
setlocal
set MAVEN_PROJECTBASEDIR=%~dp0
set WRAPPER_DIR=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper
set WRAPPER_JAR=%WRAPPER_DIR%\maven-wrapper.jar
set WRAPPER_PROPERTIES=%WRAPPER_DIR%\maven-wrapper.properties

if not exist "%WRAPPER_JAR%" (
  for /f "tokens=2 delims==" %%i in ('findstr /r /c:"^wrapperUrl=" "%WRAPPER_PROPERTIES%"') do set WRAPPER_URL=%%i
  if not "%WRAPPER_URL%"=="" (
    powershell -Command "(New-Object Net.WebClient).DownloadFile('%WRAPPER_URL%','%WRAPPER_JAR%')" || exit /b 1
  )
)

if "%JAVA_HOME%"=="" (
  echo Error: JAVA_HOME is not set. >&2
  exit /b 1
)

"%JAVA_HOME%\bin\java.exe" -jar "%WRAPPER_JAR%" -Dmaven.multiModuleProjectDirectory="%MAVEN_PROJECTBASEDIR%" %*
endlocal
