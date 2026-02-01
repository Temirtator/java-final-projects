@REM Apache Maven Wrapper
@REM
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM   https://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.

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
