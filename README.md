# LogoRRR

LogoRRR is a simple utility to display a logfile and visualise certain classes of events. The main target audience are first or second level support engineers who need a tool to find patterns in log files.

## Screenshot
![Screenshot of LogoRRR, version 21.3.2](docs/releases/21.3.2/screenshot-21.3.2.png?raw=true)

## Basic usage

![Shows basic functionality of LogoRRR as animated gif](docs/releases/21.3.1/screencast-21.3.1.gif?raw=true)

For example, ERROR events are visualized as red rectangles, TRACE events as grey rectangles, INFO events as green ones etc. 

You can start LogoRRR via double click from the desktop. Add a log file simply via drag'n drop.

## Technology

See [Technology](Technology.md) for a short summary of used technologies.

## Installation 

You can give it a try by downloading a [prebuilt installer for LogoRRR](https://github.com/rladstaetter/LogoRRR/releases/tag/21.3.2) from the releases page. There are installer for Windows and MacOs available.

**Please note**: Because LogoRRR is not digitally signed you are going to see warnings when trying to download, install or start the application. This will not change until it is digitally signed. 

This situation will be better when there is an installation via [windows app store](https://github.com/rladstaetter/LogoRRR/issues/29) and [apple app store](https://github.com/rladstaetter/LogoRRR/issues/30) are available. 


## Features

- Drag and drop log files to application to visualize/view them
- Handle multiple log files in parallel
- Filter log files for entries interactively
- Basic search functionality
- Unix `tail -f` like functionality to watch ongoing events
- Windows Installer 
- MacOs installer


## Sponsoring

Of course, if you find this project useful, **please consider to donate to this project.** For this reason I've set up a page at [buymeacoffee](https://www.buymeacoffee.com/rladstaetter).

Alternatively, just hit the 'star' here on github, or drop me a line at twitter: [@logorrr](https://www.twitter.com/logorrr/). 

A big shoutout goes to [@TheJeed](https://twitter.com/TheJeed) for sponsoring this project and boosting my motivation to continue working on it. Thank you!

I want to thank my employer [NEXTSENSE](https://www.nextsense-worldwide.com/) as well for generously providing vital infrastructure to create this application. 

## License

This software is licensed under Apache-2 License.

## Development information

If you want to build LogoRRR from source code read this document: [Build instructions](BuildInstructions.md)