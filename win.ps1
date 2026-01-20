Write-Host "Building a clean LogoRRR Windows Installer and zip distribution" -ForegroundColor Green

# clean everything
.\mvnw.cmd clean -pl "app.logorrr.dist.win:installer,app.logorrr.dist.win:app-image" -am -B -T1C

# Run Maven build
# Using --batch-mode (-B) for cleaner logs
.\mvnw.cmd clean package -pl "app.logorrr.dist.win:installer,app.logorrr.dist.win:app-image" -am -B -T1C

# Check if Maven build was successful before opening explorer
if ($LASTEXITCODE -eq 0) {
    Write-Host "Execute LogoRRR installer (Run as Administrator ...)" -ForegroundColor Yellow
    
    $installerPath = ".\dist\win\installer\target\installer\"
    
    # Check if the directory exists before trying to open it
    if (Test-Path $installerPath) {
        Invoke-Item $installerPath
    } else {
        Write-Warning "Path not found: $installerPath"
    }
} else {
    Write-Error "Maven build failed. Installer folder will not be opened."
    exit $LASTEXITCODE
}