setlocal
@echo off

rem このファイルが存在するフォルダ
set cur=%~dp0
cd /D %cur%

rem 入力ファイル
set input=%1
rem 出力ファイル(ここを書き換えると任意のパスにcsvを出力できます)
set output=%input%.csv
set CLASSPATH=thh-geotools-0.1.jar

cd lib
for %%f in (*.jar) do  (
	call "%cur%/_cp.bat" lib/%%f
)
cd %cur%

rem メインクラス
set mc=net.thh.geo.tools.Shp2Csv


java -Xmx2048m -Dfile.encoding=MS932 %mc% -i %input% -o %output%
echo %output%を出力完了
pause;
endlocal