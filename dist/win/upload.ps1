# parameters
param (
    [string]$RemoteHost,
    [string]$RemoteUser,
    [string]$LocalFile,
    [string]$RemoteDir,
    [string]$PrivateKeyPath
)

# 1. Resolve Path (Handles relative paths from Maven)
$LocalFile = Resolve-Path $LocalFile
$PrivateKeyPath = Resolve-Path $PrivateKeyPath

# 2. Fix Private Key Permissions (Required for OpenSSH on Windows)
# This mimics 'chmod 600' by removing all users except the current owner
icacls "$PrivateKeyPath" /c /t /inheritance:d | Out-Null
icacls "$PrivateKeyPath" /c /t /remove "Everyone" "Users" "Authenticated Users" | Out-Null
$currentUser = "$env:COMPUTERNAME\$env:USERNAME"
icacls "$PrivateKeyPath" /c /t /grant:r "${currentUser}:F" | Out-Null

# 3. Execute the Upload
# We use -o StrictHostKeyChecking=no to prevent the build from hanging on a "Trust this host?" prompt
Write-Host "🚀 Uploading $LocalFile to $RemoteHost..."
scp -i "$PrivateKeyPath" -o "StrictHostKeyChecking=no" "$LocalFile" "${RemoteUser}@${RemoteHost}:${RemoteDir}"

# 4. Return the exit code to Maven
if ($LASTEXITCODE -ne 0) {
    Write-Error "❌ SCP Upload failed."
    exit $LASTEXITCODE
}

exit 0