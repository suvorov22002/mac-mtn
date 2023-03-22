@ECHO OFF
CHCP 65001

REM SET /P commitMsg=Veuillez entrer un message pour le commit de ce projet: 
REM ECHO "%commitMsg%"
REM IF "%commitMsg%"=="" GOTO Error
IF "%time:~0,1%" LSS "1" (
   SET DATETIME=%date:~6,4%_%date:~3,2%_%date:~0,2%_0%time:~1,1%_%time:~3,2%_%time:~6,2%
) ELSE (
   SET DATETIME=%date:~6,4%_%date:~3,2%_%date:~0,2%_%time:~0,2%_%time:~3,2%_%time:~6,2%
)

ECHO %DATETIME%

git status
git add .
git commit -m "Version_au "_%DATETIME%
git push origin dev 

GOTO Exit
:Error
ECHO Vous n'avez pas entré de message pour ce commit! Veuillez re-essayer!!!
:Exit
ECHO Vous avez mis a jour votre projet avec succès ! Bye bye!!!