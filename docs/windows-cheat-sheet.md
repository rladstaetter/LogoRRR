# Windows Cheat sheet

## Processes

### Deployment to Windows App Store

* Make sure your installer is properly signed!
* Before submitting, make sure you registered the binary as 'safe' for Windows Defender

1. Download the file using Microsoft Edge browser.
3. When informed that the download was blocked by Smartscreen select the blocked file and click "Report this file as safe | I am the owner or representative of this website and I want to report an incorrect warning about it "
3. Fill out and submit the form. You will receive an email confirming receipt of your report.
4. 24 hours after sending the report confirm that the file no longer displays a Smartscreen warning before resubmitting the product for certification.


## Command line commands

Some commands are handy for development on windows:

| Command                           | Description                          |
|-----------------------------------|--------------------------------------|
| `wmic product LogoRRR uninstall`  | (as Administrator) uninstall LogoRRR |
