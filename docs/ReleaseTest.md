# Release Test

This document lists certain usecases which should be tested before performing a release. If not noted otherwise, each test should be performed on each supported platform.

## T1 start app without parameters

#### Action

- Start application from terminal without parameters.

#### Expected

- A window should appear which makes clear how to load log files (Currently there are two options: via drag'n drop and by providing a command line parameter).
- Check if correct version is displayed in window title

## T2 start app from desktop

(Currently not working on mac)

#### Action

- Start app via double click from desktop

#### Expected

- App starts 'normally', not opening any files.

## T3 start app with parameters

#### Action

- start app and provide a path to a log file

#### Expected

- App starts and displays log file

## T3.1 start app with parameters 

#### Action

- Like T3, but provide garbage as parameters (non existent file, a binary file for example)

#### Expected

- App starts and displays a warning dialog

## T4 deselect all filters

#### Prerequisite

- A log file is loaded

#### Action
- disable all filters

#### Expected

- No entry should be shown in text view if all filters are disabled


## T5 delete all filters

#### Prerequisite

- A log file is loaded

#### Action

- delete all filters

#### Expected

- all entries should be shown
- 'Unclassified' should be shown with '100%'

## T6 add a filter

#### Prerequisite

- A log file is loaded

#### Action

- enter a search string, apply a color to it

#### Expected

- a new 'search tag' appears with entered text
- to the left, all entries which contain given text are highlighted in selected color

## T7 disable a filter

#### Prerequisite

- a filter exists

#### Action

- disable a filter by 'untoggling' it

#### Expected

- no log entry with a matching text should be shown, neither left in graphical view nor to the right in the text view

## T8 add a second log file

#### Prerequisite

- a log file is already open

#### Action

- drag'n drop a second (third ...) log file into the application

#### Expected

- a new tab should be opened for each added log file

