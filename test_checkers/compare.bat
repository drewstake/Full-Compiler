@echo off
:: For checking on Windows
setlocal enabledelayedexpansion
for %%i in (0 1 2 3 4 5 6 7 8) do (
    echo Running test%%i...
    "C:\Program Files\Java\jdk-17.0.2\bin\java.exe" Program tests\test%%i.minc > temp_output.txt 2>nul
    fc /b temp_output.txt solutions\testsolu%%i.txt >nul
    if errorlevel 1 (
        echo Test%%i: FAIL
    ) else (
        echo Test%%i: PASS
    )
    del temp_output.txt
)
endlocal
