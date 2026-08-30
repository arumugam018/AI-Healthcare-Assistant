@echo off
echo Starting AI Healthcare Assistant...

if exist .env (
    for /f "usebackq tokens=1,* delims==" %%A in (`type .env`) do (
        if "%%A"=="GEMINI_API_KEY" set GEMINI_API_KEY=%%B
    )
)

if "%GEMINI_API_KEY%"=="" (
    if "%~1"=="" (
        echo.
        echo [WARNING] GEMINI_API_KEY environment variable is not set!
        echo AI features will respond with an API Key requirement error.
        echo To run with a key, use: .\run.bat YOUR_API_KEY
        echo Or create a .env file containing: GEMINI_API_KEY=your_key_here
        echo.
    ) else (
        set GEMINI_API_KEY=%~1
        echo Using Gemini API Key provided via command line.
    )
) else (
    echo Using Gemini API Key from environment variable or .env.
)

call .\mvnw.cmd spring-boot:run
