Write-Host "Building and uploading LogoRRR" -ForegroundColor Cyan

# clean everything
.\mvnw.cmd clean -pl "app.logorrr.dist.win:installer,app.logorrr.dist.win:app-image" -am -B -T1C

# Call the Maven wrapper
.\mvnw.cmd install -pl "app.logorrr.dist.win:installer,app.logorrr.dist.win:app-image" -am -B -T1C

# Check if the build failed and exit with the same code if it did
if ($LASTEXITCODE -ne 0) {
    Write-Host "Build failed with exit code $LASTEXITCODE" -ForegroundColor Red
    exit $LASTEXITCODE
}