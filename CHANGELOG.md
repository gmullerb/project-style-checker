# Project Style Checker Change Log

## 1.0.3 - April 2019

* Fixes the issue: the tasks `logForCodeNarcMain` and `logForCodeNarcTest` were not being added, unless this plugin was applied after plugins with `codenarcMain` and/or `codenarcTest` tasks.
* Adds Gitlab CI.
* Updates README file.

## October 2018

* Extracts `SpyProjectBuilder` to its own project,[spy-project-factory](https://github.com/gmullerb/spy-project-factory), in order to be used by other projects.
* Upgrades base-style-config to version 1.0.7.
  * Does some code changes to follow new version.
