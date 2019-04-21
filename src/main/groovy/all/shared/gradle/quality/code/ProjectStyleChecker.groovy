//  Copyright (c) 2018 Gonzalo Müller Bravo.
//  Licensed under the MIT License (MIT), see LICENSE.txt
package all.shared.gradle.quality.code

import all.shared.gradle.file.FileListerExtension
import all.shared.gradle.file.FileListerPlugin
import all.shared.gradle.quality.code.assess.AssessTaskDefinition
import all.shared.gradle.quality.code.assess.common.CreateAssessCommonAction
import all.shared.gradle.quality.code.assess.gradle.CreateAssessGradleAction
import all.shared.gradle.quality.code.assess.gradle.LogCodeNarcReportAction

import groovy.transform.CompileStatic

import org.gradle.api.Action
import org.gradle.api.Task
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.quality.CheckstylePlugin
import org.gradle.api.plugins.quality.CodeNarc
import org.gradle.api.plugins.quality.CodeNarcExtension
import org.gradle.api.plugins.quality.CodeNarcPlugin

@CompileStatic
class ProjectStyleChecker {
  public static final String LOG_CODENARC_MAIN_REPORT_TASK = 'logForCodeNarcMain'
  public static final String LOG_CODENARC_TEST_REPORT_TASK = 'logForCodeNarcTest'

  final Project project

  ProjectStyleChecker(final Project project) {
    this.project = project
  }

  private void addPlugin(final String extensionName, Plugin<?> plugin) {
    if (project.extensions.findByName(extensionName) == null) {
      plugin.apply(project)
      project.logger.debug('project-style-check extension applied {} plugin', plugin.class)
    }
  }

  private <T extends Task> void addTask(
      final AssessTaskDefinition<T> taskDefinition,
      final Action<T> configuration) {
    if (project.tasks.findByPath(taskDefinition.name) == null) {
      project.tasks.create(taskDefinition.name, taskDefinition.type, configuration)
      project.logger.debug('Added {} task', taskDefinition.name)
    }
  }

  private void addLogCodenarcReportTasks(final String logReportTaskName, final String taskName) {
    final CodeNarc task = (CodeNarc) project.tasks.findByPath(taskName)
    if (task) {
      LogCodeNarcReportAction.addLogReportTask(logReportTaskName, task)
    }
    else {
      project.tasks.whenTaskAdded { final Task addedTask ->
        if (addedTask.name == taskName) {
          LogCodeNarcReportAction.addLogReportTask(logReportTaskName, (CodeNarc) addedTask)
        }
      }
    }
  }

  private void establishCodenarcConfig(final CodeNarcExtension extension, final ProjectStyleCheckerExtension config) {
    extension.config = config.gradle.config
    project.logger.debug('Set codenarc config filled with projectStyleChecker')
  }

  ProjectStyleCheckerExtension addExtension(final String extensionName) {
    if (project.extensions.findByName(extensionName) == null) {
      project.extensions.create(extensionName, ProjectStyleCheckerExtension)
    }
  }

  void addRequiredPlugins() {
    addPlugin(BaseStyleConfigWrapperPlugin.EXTENSION_NAME, new BaseStyleConfigWrapperPlugin())
    addPlugin(FileListerPlugin.EXTENSION_NAME, new FileListerPlugin())
    addPlugin('checkstyle', new CheckstylePlugin())
    addPlugin('codenarc', new CodeNarcPlugin())
  }

  void fillExtensionConfigs(final ProjectStyleCheckerExtension config) {
    final BaseStyleConfigWrapperExtension baseStyleConfig = (BaseStyleConfigWrapperExtension) project.extensions
      .findByName(BaseStyleConfigWrapperPlugin.EXTENSION_NAME)
    config.common.config = baseStyleConfig.common.checkstyleConfig
    config.gradle.config = baseStyleConfig.back.codenarcConfig
    project.logger.debug('project-style-check extension filled with {}', BaseStyleConfigWrapperPlugin.EXTENSION_NAME)
  }

  void fillExtensionFileTree(final ProjectStyleCheckerExtension config) {
    config.common.fileTree = config.gradle.fileTree = ((FileListerExtension) project.extensions
      .findByName(FileListerPlugin.EXTENSION_NAME))
      .obtainPartialFileTree()
    project.logger.debug('project-style-check extension filled with {}', FileListerPlugin.EXTENSION_NAME)
  }

  void establishCodenarcSettings(final ProjectStyleCheckerExtension config) {
    final CodeNarcExtension extension = (CodeNarcExtension) project.extensions.findByName('codenarc')
    establishCodenarcConfig(extension, config)
  }

  /**
   * Calls all extensions' configuration methods. <br/>
   * fillAllExtensions(config) = fillExtensionConfigs(config) + fillExtensionFileTree(config) +
   * establishCodenarcSettings(config)
   */
  void fillAllExtensions(final ProjectStyleCheckerExtension config) {
    fillExtensionConfigs(config)
    fillExtensionFileTree(config)
    establishCodenarcSettings(config)
  }

  void addTasks(final ProjectStyleCheckerExtension assessTaskConfig) {
    addTask(CreateAssessCommonAction.ASSESS_TASK_DEFINITION, new CreateAssessCommonAction(assessTaskConfig.common))
    addTask(CreateAssessGradleAction.ASSESS_TASK_DEFINITION, new CreateAssessGradleAction(assessTaskConfig.gradle))
    addLogCodenarcReportTasks(LOG_CODENARC_MAIN_REPORT_TASK, 'codenarcMain')
    addLogCodenarcReportTasks(LOG_CODENARC_TEST_REPORT_TASK, 'codenarcTest')
  }
}
