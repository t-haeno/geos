setlocal
@echo off

rem ���̃t�@�C�������݂���t�H���_
set cur=%~dp0
cd /D %cur%

rem ���̓t�@�C��
set input=%1
rem �o�̓t�@�C��(����������������ƔC�ӂ̃p�X��csv���o�͂ł��܂�)
set output=%input%.csv
set CLASSPATH=thh-geotools-0.1.jar

cd lib
for %%f in (*.jar) do  (
	call "%cur%/_cp.bat" lib/%%f
)
cd %cur%

rem ���C���N���X
set mc=net.thh.geo.tools.Shp2Csv


java -Xmx2048m -Dfile.encoding=MS932 %mc% -i %input% -o %output%
echo %output%���o�͊���
pause;
endlocal