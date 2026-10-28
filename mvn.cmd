@echo off
set "JAVA_HOME=C:\Program Files\Java\jdk-17"
set "PATH=%JAVA_HOME%\bin;C:\Users\kaene\.gemini\antigravity\scratch\apache-maven-3.9.9\bin;%PATH%"
call "C:\Users\kaene\.gemini\antigravity\scratch\apache-maven-3.9.9\bin\mvn.cmd" -Dmaven.repo.local=C:\Users\kaene\.gemini\antigravity\scratch\.m2\repository %*
