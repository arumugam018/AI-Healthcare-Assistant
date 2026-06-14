@echo off
echo Starting AI Healthcare Assistant...

if "%GEMINI_API_KEY%"=="" (
    if "%~1"=="" (
        echo.
        echo [WARNING] GEMINI_API_KEY environment variable is not set!
        echo The AI features will not work without it.
        echo To run with a key, use: .\run.bat YOUR_API_KEY
        echo.
    ) else (
        set GEMINI_API_KEY=%~1
        echo Using Gemini API Key provided via command line.
    )
) else (
    echo Using Gemini API Key from environment variable.
)

call .\mvnw.cmd spring-boot:run
