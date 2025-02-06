@echo off
setlocal enabledelayedexpansion

REM Loop through tests 0 to 8
for %%i in (0 1 2 3 4 5 6 7 8) do (
    echo Running test%%i...
    REM Run the program using tests\test%%i.minc, redirecting output and errors appropriately
    "C:\Program Files\Java\jdk-17.0.2\bin\java.exe" Program tests\test%%i.minc > temp_output.txt 2>nul

    REM Compare the temporary output with the expected solution file in the solutions folder
    fc /b temp_output.txt solutions\testsolu%%i.txt >nul

    if errorlevel 1 (
        echo Test%%i: FAIL
    ) else (
        echo Test%%i: PASS
    )
    
    REM Delete the temporary file
    del temp_output.txt
)

endlocal