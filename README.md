# Project Style Checker

[![license](https://img.shields.io/github/license/mashape/apistatus.svg)](/LICENSE.txt) 
[![Download](https://api.bintray.com/packages/gmullerb/all.shared.gradle/project-style-checker/images/download.svg)](https://bintray.com/gmullerb/all.shared.gradle/project-style-checker/_latestVersion) 
[![coverage report](https://gitlab.com/gmullerb/project-style-checker/badges/master/coverage.svg)](https://gitlab.com/gmullerb/project-style-checker/commits/master) 

**A Gradle plugin that offers a small set of Gradle's tasks for checking file style for all files and for checking code style of Gradle's build code**.

This project is licensed under the terms of the [MIT license](/LICENSE.txt).
__________________

## Quick Start

1 . Apply the plugin:

`build.gradle`:

```gradle
 plugins {
   id 'all.shared.gradle.project-style-checker' version '1.0.3'
 }
```

2 . Add `assessCommon` and `assessGradle` tasks, usually as default tasks:

`build.gradle`:

```gradle
  defaultTasks 'assessCommon', 'assessGradle', ..
```

3 . Jump to [Using/Configuration](#Using/Configuration), for customization or digging on How it works.
__________________

## Goals

* Have a task for checking all files, not only code files, with some basic but common rules.
* Have a task for checking Gradle's code.
* Allow to log to console of checking report.
* Automatically apply all the required plugins.
* Allow to easily configure versions.

## Features

* Offers a small set of tasks:

  * `assessCommon`: checks all the specified files with the [Common Checkstyle's set](https://github.com/gmullerb/base-style-config/tree/master/config/common/common-checks.xml) from [base-style-config project](https://github.com/gmullerb/base-style-config).
  * `assessGradle`: checks all the Gradle files (`.gradle`) from the specified file tree with [Codenarc's set](https://github.com/gmullerb/base-style-config/tree/master/config/gradle/gradle-rules.groovy) from [base-style-config project](https://github.com/gmullerb/base-style-config).
    * Task automatically filters the received file tree to get the `.gradle` files.

* Adds Tasks that show a summary of the CodeNarc report to console:
  * `logForAssessGradle`[1].
  * `logForCodeNarcMain` [2].
  * `logForCodeNarcTest` [2].

* Applies the required plugins to the project:

  * [Base Style Configuration Wrapper plugin](https://github.com/gmullerb/base-style-config-wrapper).
    * which will set some default configuration the Coding Style Tools.
  * [File Lister plugin ](https://github.com/gmullerb/file-lister).
  * [Checkstyle plugin](https://docs.gradle.org/current/userguide/checkstyle_plugin.html).
  * [CodeNarc plugin](https://docs.gradle.org/current/userguide/codenarc_plugin.html).

* Sets CodeNarc configuration to point to [Base Style Configuration](https://github.com/gmullerb/base-style-config).

* Allows to indicate the version of:
  * [Base Style Configuration](https://github.com/gmullerb/base-style-config).
  * [Checkstyle](http://checkstyle.sourceforge.net).
  * [CodeNarc](http://codenarc.sourceforge.net).

> [1] Fails for exactly Gradle version 5.2, use any version (after or before) except 5.2.  
> [2] Added if the `codenarcMain` and/or `codenarcTest` tasks are present in the project.
__________________

## Using/Configuration

### Prerequisites

* None

### Gradle configuration

1 . Apply the plugin [1]:

```gradle
 plugins {
   id 'all.shared.gradle.project-style-checker' version '1.0.3'
 }
```

> [1]: This is the only configuration step required to use this plugin.

### Plugin configuration [Optional]

2 . If required, use the plugin's extension, `projectStyleChecker`, which have two fields [1]:

* `common`: has the `assessCommon` configuration.
* `gradle`: has the `assessGradle` configuration.

Both have same fields:

* `config`: which correspond with the `config` field of [`CodeNarc task`](https://docs.gradle.org/current/dsl/org.gradle.api.plugins.quality.CodeNarc.html) or [`Checkstyle task`](https://docs.gradle.org/current/dsl/org.gradle.api.plugins.quality.Checkstyle.html) , basically the Code style set of rules.
  * By default, use rules defined by [Base Style Configuration Wrapper plugin](https://github.com/gmullerb/base-style-config-wrapper).
* `fileTree`: which indicate the file tree to explore when checking.
  * By default, all file tree is checked, using default exclusions of [File Lister plugin exclusions](https://github.com/gmullerb/file-lister#features).

E.G.:

```gradle
 projectStyleChecker {
   common {
     config = ..
     fileTree = ..
   }
   gradle {
     config = ..
     fileTree = ..
   }
 }
```

> [1] **All these values are filled by default with values obtain from [Base Style Configuration Wrapper plugin](https://github.com/gmullerb/base-style-config-wrapper) and [File Lister plugin ](https://github.com/gmullerb/file-lister)**

#### Convention over Configuration [Optional]

3 . Use `BASE_STYLE_CONFIG_VERSION` to establish the version of [Base Style Configuration](https://github.com/gmullerb/base-style-config) to be used:
  
* If not set, then last version will be used.

[`gradle.properties`](gradle.properties):

```properties
 BASE_STYLE_CONFIG_VERSION=2.0.1
```

4 . Use `CHECKSTYLE_VERSION` to establish the version of [Checkstyle](http://checkstyle.sourceforge.net) to be used:
  
* Use to check all files.
* If not set, Gradle's default version will be used.
  * At the present, should be set in order to be compatible with the set of rules defined by [Base Style Configuration](https://github.com/gmullerb/base-style-config)

[`gradle.properties`](gradle.properties):

```properties
 CHECKSTYLE_VERSION=8.18
```

5 . Use `CODENARC_VERSION` to establish the version of [CodeNarc](http://codenarc.sourceforge.net) to be used:

* Use to check Gradle files.
* If not set, Gradle's default version will be used.
  * At the present, should be set in order to be compatible with the set of rules defined by [Base Style Configuration](https://github.com/gmullerb/base-style-config)

[`gradle.properties`](gradle.properties):

```properties
 CODENARC_VERSION=1.3
```

> [1] This is inherited/done by the [Base Style Configuration Wrapper plugin](https://github.com/gmullerb/base-style-config-wrapper).

### Assessing files

* To assess all files use Gradle task: `assessCommon`.
* To assess Gradle's code use Gradle task: `assessGradle`.
* To assess CodeNarc's code use Gradle task: `codenarcMain`.
* To assess CodeNarc's code use Gradle task: `codenarcTest`.
__________________

## Extending/Developing

### Prerequisites

* [Java](http://www.oracle.com/technetwork/java/javase/downloads).
* [Git](https://git-scm.com/downloads) (only if you are going to clone the project).

### Getting it

Clone or download the project[1], in the desired folder execute:

```sh
git clone https://github.com/gmullerb/project-style-checker
```

> [1] [Cloning a repository](https://help.github.com/articles/cloning-a-repository/)

### Set up

* **No need**, only download and run (It's Gradle! Yes!).

#### Make it your own

  1. Remove the Git Origin: `git remote remove origin`.
  2. Add your Git origin: `git remote add origin https://gitlab.com/yourUser/yourRepo.git`.
  3. Remove the License for 'All rights reserved' projects, or Modify the License for your needs.
  4. Change the `all.shared.gradle.project-style-checker.properties` file name to your plugin Id, e.g. `some.pluginId.properties`.

### Building it

* To build it:
  * `gradlew`: this will run default task, or
  * `gradlew build`.

* To assess files:
  * `gradlew assessCommon`: will check common style of files.
  * `gradlew assessGradle`: will check code style of Gradle's.
  * `gradlew codenarcMain`: will check code style of Groovy's source files.
  * `gradlew codenarcTest`: will check code style of Groovy's test files.
  * `assemble` task depends on these four tasks.

* To test code: `gradlew test`

* To get all the tasks for the project: `gradlew tasks --all`

### Folders structure

```
  /src
    /main
      /groovy
    /test
      /groovy
```

- `src/main/groovy`: Source code files.
  - [`CreateAssessCommonAction`](src/main/groovy/all/shared/gradle/quality/code/assess/common/CreateAssessCommonAction.groovy) + [`PrepareAssessCommonAction`](src/main/groovy/all/shared/gradle/quality/code/assess/common/PrepareAssessCommonAction.groovy) define the `assessCommon` task.
  - [`CreateAssessGradleAction`](src/main/groovy/all/shared/gradle/quality/code/assess/gradle/CreateAssessGradleAction.groovy) + [`PrepareAssessGradleAction`](src/main/groovy/all/shared/gradle/quality/code/assess/gradle/PrepareAssessGradleAction.groovy) define the `assessGradle` task.
  - [`LogCodeNarcReportAction`](src/main/groovy/all/shared/gradle/quality/code/assess/gradle/LogCodeNarcReportAction.groovy) is the action for logging report of a CodeNarc task.
  - [`ProjectStyleChecker`](src/main/groovy/all/shared/gradle/quality/code/ProjectStyleChecker.groovy) is where all the magic happens (where plugins are added, where tasks are added, etc.).
    - If using this class directly, then prefer `fillAllExtensions`, over `fillExtensionConfigs`, `fillExtensionFileTree`, `establishCheckstyleVersion` & `establishCodenarcSettings`, see [`ProjectStyleCheckerPlugin`](src/main/groovy/all/shared/gradle/quality/code/ProjectStyleCheckerPlugin.groovy)
- `src/test/groovy`: Test code files[1].

> [1] Tests are done with [JUnit](http://junit.org) and [Mockito](http://javadoc.io/page/org.mockito/mockito-core/latest/org/mockito/Mockito.html).

### Convention over Configuration

All `all.shared.gradle` plugins define:

* _PluginName_**Plugin**: which contains the class implements `Plugin` interface.
* _PluginName_**Extension**: which represent the extension of the plugin.
* If Tasks are define, then their names will be _TaskName_**Task**.
* If Actions are define, then their names will be _ActionName_**Action**.

All `all.shared.gradle` plugins have two **`static`** members:

* `String EXTENSION_NAME`: This will have the name of the extension that the plugin add.
  * if the plugin does not add an extension the this field will not exist.

* `String TASK_NAME`: This will have the name of the **unique** task that the plugin add.
  * if the plugin does not add a task or add more than one task, then this field will not exist.

* `boolean complement(final ..)`: will apply the plugin and return true if successful, false otherwise.
  * this methods is **exactly equivalent to the instance `apply` method**, but without instantiate the class if not required.

Both may be useful when applying the plugin when creating custom plugins.

All `all.shared.gradle` plugins "silently" fail when the extension can not be added.

## Documentation

* [`CHANGELOG.md`](CHANGELOG.md): add information of notable changes for each version here, chronologically ordered [1].

> [1] [Keep a Changelog](http://keepachangelog.com)

## License

[MIT License](/LICENSE.txt)
__________________

## Remember

* Use code style verification tools => Encourages Best Practices, Efficiency, Readability and Learnability.
* Start testing early => Encourages Reliability and Maintainability.
* Code Review everything => Encourages Functional suitability, Performance Efficiency and Teamwork.

## Additional words

Don't forget:

* **Love what you do**.
* **Learn everyday**.
* **Learn yourself**.
* **Share your knowledge**.
* **Learn from the past, dream on the future, live and enjoy the present to the max!**.

At life:

* Let's act, not complain.
* Be flexible.

At work:

* Let's give solutions, not questions.
* Aim to simplicity not intellectualism.
