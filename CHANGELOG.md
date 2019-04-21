# Project Style Checker Change Log

## 1.0.4 - April 2019

* Removes code quality tools version autoconfiguration, now is done by [Base Style Configuration Wrapper plugin](https://github.com/gmullerb/base-style-config-wrapper).
* Updates README file.

## 1.0.3 - April 2019

* Fixes the issue: the tasks `logForCodeNarcMain` and `logForCodeNarcTest` were not being added, unless this plugin was applied after plugins with `codenarcMain` and/or `codenarcTest` tasks.
* Adds Gitlab CI.
* Updates README file.

## October 2018

* Extracts `SpyProjectBuilder` to its own project,[spy-project-factory](https://github.com/gmullerb/spy-project-factory), in order to be used by other projects.
* Upgrades base-style-config to version 1.0.7.
  * Does some code changes to follow new version.
