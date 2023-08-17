# Release Test

This document lists certain usecases which should be tested before performing a release. If not noted otherwise, each test should be performed on each supported platform.

## T-INSTALL : install LogoRRR

- install LogoRRR on all supported platforms
- start LogoRRR
- delete home directory for applications, start LogoRRR

## T-START-DESKTOP: start app from desktop

#### Action

- Start app via double click from desktop

#### Expected

- App starts 'normally', not opening any files.

## T-FILTER-DESELECT: deselect all filters

#### Prerequisite

- A log file is loaded

#### Action

- disable all filters

#### Expected

- No entry should be shown in text view if all filters are disabled

## T-FILTER-DELETE: delete all filters

#### Prerequisite

- A log file is loaded

#### Action

- delete all filters

#### Expected

- all entries should be shown
- 'Unclassified' should be shown with '100%'

## T-FILTER-ADD: add a filter

#### Prerequisite

- A log file is loaded

#### Action

- enter a search string, apply a color to it

#### Expected

- a new 'search tag' appears with entered text
- to the left, all entries which contain given text are highlighted in selected color

## T-FILTER-DISABLE: disable a filter

#### Prerequisite

- a filter exists

#### Action

- disable a filter by 'untoggling' it

#### Expected

- no log entry with a matching text should be shown, neither left in graphical view nor to the right in the text view

## T-LOGFILE-ADD: add a second log file

#### Prerequisite

- a log file is already open

#### Action

- drag'n drop a second (third ...) log file into the application

#### Expected

- a new tab should be opened for each added log file

