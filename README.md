# LogoRRR

![Screenshot](screencast.gif)

LogoRRR is a simple utility to display a logfile and visualise certain classes of events. 

For example, ERROR events are visualized as red rectangles, TRACE events as grey rectangles, INFO events as green ones etc. 

## Installation 

You can give it a try by downloading a [prebuilt installer for LogoRRR](https://github.com/rladstaetter/LogoRRR/releases/tag/21.2.4) from the releases page. There are binaries for Windows and MacOs available.

### Windows

As of Release 21.3.0, there exists an Installer for this application, which can be found on the releases page. 

**Please note**: Because LogoRRR is not digitally signed you are going to see warnings when trying to download, install or start the application. This will not change until it is digitally signed. However, for this project no such certificate exists yet.

### MacOsX

For Mac Users, quite some motivation is needed at the moment to get the application to run.

You'll have to give the application rights to be executed which can be done in a terminal with the command

    chmod 755 app.logorrrr

Afterwards you have to specify in your preferences menu that you allow to start this application. Finally then you can start it via command line. 

## Features

- Drag and drop log files to application to visualize/view them
- Handle multiple log files in parallel
- Filter log files for entries interactively
- Basic search functionality
- Unix `tail -f` like functionality to watch ongoing events
- Windows Installer

## Usage

You can start LogoRRR via double click from the desktop. Add a log file simply via drag'n drop.

Alternatively, you can start LogoRRR via command line and provide the path to the log file as command line parameter (or multiple at once if you like).

## Sponsoring

Of course, if you find this project useful, **please consider to donate to this project.** For this reason I've set up a page at [buymeacoffee](https://www.buymeacoffee.com/rladstaetter).

Alternatively, just hit the 'star' here on github, or drop me a line at [twitter](https://www.twitter.com/rladstaetter/). 

A big shoutout goes to [@TheJeed](https://twitter.com/TheJeed) for sponsoring this project and boosting my motivation to continue working on it. Thank you!

I want to thank my employer [NEXTSENSE](https://www.nextsense-worldwide.com/) as well for generously providing vital infrastructure to create this application. 

## License

This software is licensed under Apache-2 License.

## Development information

If you want to build LogoRRR from source code read this document: [Build instructions](BuildInstructions.md)