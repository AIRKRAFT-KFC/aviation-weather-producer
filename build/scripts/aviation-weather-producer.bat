@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%"=="" @echo off
@rem ##########################################################################
@rem
@rem  aviation-weather-producer startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
@rem This is normally unused
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and AVIATION_WEATHER_PRODUCER_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if %ERRORLEVEL% equ 0 goto execute

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\aviation-weather-producer-1.0.0-plain.jar;%APP_HOME%\lib\spring-boot-starter-web-3.1.0.jar;%APP_HOME%\lib\spring-boot-starter-webflux-3.1.0.jar;%APP_HOME%\lib\spring-boot-starter-quartz-3.1.0.jar;%APP_HOME%\lib\spring-boot-starter-validation-3.1.0.jar;%APP_HOME%\lib\spring-boot-starter-actuator-3.1.0.jar;%APP_HOME%\lib\spring-boot-starter-json-3.1.0.jar;%APP_HOME%\lib\spring-boot-starter-3.1.0.jar;%APP_HOME%\lib\spring-kafka-3.0.7.jar;%APP_HOME%\lib\kafka-clients-3.5.0.jar;%APP_HOME%\lib\jackson-dataformat-xml-2.15.0.jar;%APP_HOME%\lib\jackson-annotations-2.15.0.jar;%APP_HOME%\lib\jackson-datatype-jdk8-2.15.0.jar;%APP_HOME%\lib\jackson-module-parameter-names-2.15.0.jar;%APP_HOME%\lib\jackson-core-2.15.0.jar;%APP_HOME%\lib\spring-boot-actuator-autoconfigure-3.1.0.jar;%APP_HOME%\lib\jackson-datatype-jsr310-2.15.0.jar;%APP_HOME%\lib\jackson-databind-2.15.0.jar;%APP_HOME%\lib\jaxb-runtime-2.3.1.jar;%APP_HOME%\lib\jaxb-api-2.3.1.jar;%APP_HOME%\lib\spring-boot-configuration-processor-3.1.0.jar;%APP_HOME%\lib\micrometer-registry-prometheus-1.11.0.jar;%APP_HOME%\lib\spring-boot-autoconfigure-3.1.0.jar;%APP_HOME%\lib\spring-boot-actuator-3.1.0.jar;%APP_HOME%\lib\spring-boot-3.1.0.jar;%APP_HOME%\lib\spring-boot-starter-logging-3.1.0.jar;%APP_HOME%\lib\spring-boot-starter-tomcat-3.1.0.jar;%APP_HOME%\lib\jakarta.annotation-api-2.1.1.jar;%APP_HOME%\lib\spring-webmvc-6.0.9.jar;%APP_HOME%\lib\spring-webflux-6.0.9.jar;%APP_HOME%\lib\spring-web-6.0.9.jar;%APP_HOME%\lib\spring-context-support-6.0.9.jar;%APP_HOME%\lib\spring-context-6.0.9.jar;%APP_HOME%\lib\spring-messaging-6.0.9.jar;%APP_HOME%\lib\spring-tx-6.0.9.jar;%APP_HOME%\lib\spring-aop-6.0.9.jar;%APP_HOME%\lib\spring-beans-6.0.9.jar;%APP_HOME%\lib\spring-expression-6.0.9.jar;%APP_HOME%\lib\spring-core-6.0.9.jar;%APP_HOME%\lib\snakeyaml-1.33.jar;%APP_HOME%\lib\spring-boot-starter-reactor-netty-3.1.0.jar;%APP_HOME%\lib\spring-retry-2.0.1.jar;%APP_HOME%\lib\micrometer-core-1.11.0.jar;%APP_HOME%\lib\micrometer-observation-1.11.0.jar;%APP_HOME%\lib\jsr305-3.0.2.jar;%APP_HOME%\lib\zstd-jni-1.5.5-1.jar;%APP_HOME%\lib\lz4-java-1.8.0.jar;%APP_HOME%\lib\snappy-java-1.1.10.0.jar;%APP_HOME%\lib\quartz-2.3.2.jar;%APP_HOME%\lib\logback-classic-1.4.7.jar;%APP_HOME%\lib\log4j-to-slf4j-2.20.0.jar;%APP_HOME%\lib\jul-to-slf4j-2.0.7.jar;%APP_HOME%\lib\slf4j-api-2.0.7.jar;%APP_HOME%\lib\woodstox-core-6.5.1.jar;%APP_HOME%\lib\stax2-api-4.2.1.jar;%APP_HOME%\lib\javax.activation-api-1.2.0.jar;%APP_HOME%\lib\txw2-4.0.2.jar;%APP_HOME%\lib\istack-commons-runtime-4.1.1.jar;%APP_HOME%\lib\stax-ex-2.1.0.jar;%APP_HOME%\lib\FastInfoset-2.1.0.jar;%APP_HOME%\lib\tomcat-embed-el-10.1.8.jar;%APP_HOME%\lib\hibernate-validator-8.0.0.Final.jar;%APP_HOME%\lib\simpleclient_common-0.16.0.jar;%APP_HOME%\lib\spring-jcl-6.0.9.jar;%APP_HOME%\lib\tomcat-embed-websocket-10.1.8.jar;%APP_HOME%\lib\tomcat-embed-core-10.1.8.jar;%APP_HOME%\lib\reactor-netty-http-1.1.7.jar;%APP_HOME%\lib\reactor-netty-core-1.1.7.jar;%APP_HOME%\lib\reactor-core-3.5.6.jar;%APP_HOME%\lib\micrometer-commons-1.11.0.jar;%APP_HOME%\lib\fastdoubleparser-0.8.0.jar;%APP_HOME%\lib\jakarta.activation-api-2.1.2.jar;%APP_HOME%\lib\mchange-commons-java-0.2.15.jar;%APP_HOME%\lib\jakarta.validation-api-3.0.2.jar;%APP_HOME%\lib\jboss-logging-3.5.0.Final.jar;%APP_HOME%\lib\classmate-1.5.1.jar;%APP_HOME%\lib\HdrHistogram-2.1.12.jar;%APP_HOME%\lib\LatencyUtils-2.0.3.jar;%APP_HOME%\lib\simpleclient-0.16.0.jar;%APP_HOME%\lib\logback-core-1.4.7.jar;%APP_HOME%\lib\log4j-api-2.20.0.jar;%APP_HOME%\lib\netty-codec-http2-4.1.92.Final.jar;%APP_HOME%\lib\netty-handler-proxy-4.1.92.Final.jar;%APP_HOME%\lib\netty-codec-http-4.1.92.Final.jar;%APP_HOME%\lib\netty-resolver-dns-native-macos-4.1.92.Final-osx-x86_64.jar;%APP_HOME%\lib\netty-resolver-dns-classes-macos-4.1.92.Final.jar;%APP_HOME%\lib\netty-resolver-dns-4.1.92.Final.jar;%APP_HOME%\lib\netty-transport-native-epoll-4.1.92.Final-linux-x86_64.jar;%APP_HOME%\lib\reactive-streams-1.0.4.jar;%APP_HOME%\lib\simpleclient_tracer_otel-0.16.0.jar;%APP_HOME%\lib\simpleclient_tracer_otel_agent-0.16.0.jar;%APP_HOME%\lib\netty-handler-4.1.92.Final.jar;%APP_HOME%\lib\netty-codec-dns-4.1.92.Final.jar;%APP_HOME%\lib\netty-codec-socks-4.1.92.Final.jar;%APP_HOME%\lib\netty-codec-4.1.92.Final.jar;%APP_HOME%\lib\netty-transport-classes-epoll-4.1.92.Final.jar;%APP_HOME%\lib\netty-transport-native-unix-common-4.1.92.Final.jar;%APP_HOME%\lib\netty-transport-4.1.92.Final.jar;%APP_HOME%\lib\netty-buffer-4.1.92.Final.jar;%APP_HOME%\lib\netty-resolver-4.1.92.Final.jar;%APP_HOME%\lib\netty-common-4.1.92.Final.jar;%APP_HOME%\lib\simpleclient_tracer_common-0.16.0.jar


@rem Execute aviation-weather-producer
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %AVIATION_WEATHER_PRODUCER_OPTS%  -classpath "%CLASSPATH%" com.aviation.weather.AviationWeatherProducerApplication %*

:end
@rem End local scope for the variables with windows NT shell
if %ERRORLEVEL% equ 0 goto mainEnd

:fail
rem Set variable AVIATION_WEATHER_PRODUCER_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
set EXIT_CODE=%ERRORLEVEL%
if %EXIT_CODE% equ 0 set EXIT_CODE=1
if not ""=="%AVIATION_WEATHER_PRODUCER_EXIT_CONSOLE%" exit %EXIT_CODE%
exit /b %EXIT_CODE%

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
