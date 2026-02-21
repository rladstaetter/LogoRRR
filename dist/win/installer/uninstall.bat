rem get-wmiobject Win32_Product > installed_apps.txt

msiexec /x {f27d1340-33ab-30a2-a9cd-f58ba17f3581} /L*V uninstall_log-25.1.0.txt
msiexec /x {5b413bc5-35cb-3793-b588-2518728ca704} /L*V uninstall_log-26.1.0.txt
msiexec /x {8a715c03-03a8-3b20-86f0-8697d4b9db26} /L*V uninstall_log-26.1.1.txt
