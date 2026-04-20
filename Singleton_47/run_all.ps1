# run_all.ps1 - Setup and run all Singleton implementations
# Usage: powershell -ExecutionPolicy Bypass -File run_all.ps1

Set-Location $PSScriptRoot

Write-Host ""
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "  Singleton Pattern - MongoDB Connection Manager" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan

# Step 1: Create venv if missing
if (Test-Path ".\venv\Scripts\python.exe") {
    Write-Host "`n[1/3] Virtual environment already exists - skipping." -ForegroundColor Green
} else {
    Write-Host "`n[1/3] Creating virtual environment..." -ForegroundColor Yellow
    python -m venv venv
    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: Failed to create venv. Is Python installed and on PATH?" -ForegroundColor Red
        exit 1
    }
    Write-Host "      venv created successfully." -ForegroundColor Green
}

# Step 2: Install dependencies
Write-Host "`n[2/3] Installing dependencies..." -ForegroundColor Yellow
.\venv\Scripts\pip install -r requirements.txt --quiet
Write-Host "      Dependencies ready." -ForegroundColor Green

# Step 3: Run all files
Write-Host "`n[3/3] Running all Singleton implementations..." -ForegroundColor Yellow

$scripts = @(
    "Eager_Initialization.py",
    "Lazy_Initialization.py",
    "Synchronized_method.py",
    "Double_Checked_Locking.py",
    "Bill_Pugh_Implementation.py",
    "Enum_Based_Singleton.py"
)

foreach ($script in $scripts) {
    Write-Host ""
    Write-Host "------------------------------------------------------------" -ForegroundColor DarkGray
    Write-Host "  Running: $script" -ForegroundColor Magenta
    Write-Host "------------------------------------------------------------" -ForegroundColor DarkGray
    .\venv\Scripts\python $script
}

Write-Host ""
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "  All done!" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan
