//  Copyright (c) 2018 Gonzalo Müller Bravo.
//  Licensed under the MIT License (MIT), see LICENSE.txt
package all.shared.gradle.quality.code.assess.gradle

import all.shared.gradle.quality.code.assess.AssessTaskDefinition
import all.shared.gradle.quality.code.assess.AssessTaskConfig

import groovy.transform.CompileStatic

import org.gradle.api.Action
import org.gradle.api.plugins.quality.CodeNarc

@CompileStatic
class CreateAssessGradleAction implements Action<CodeNarc> {
  public static final AssessTaskDefinition ASSESS_TASK_DEFINITION =
    new AssessTaskDefinition(
      CodeNarc,
      'assessGradle')
  public static final String LOG_REPORT_TASK = 'logForAssessGradle'

  private final AssessTaskConfig setting

  CreateAssessGradleAction(final AssessTaskConfig setting) {
    this.setting = setting
  }

  void execute(final CodeNarc task) {
    // CodeNarc task settings
    task.source = task.project.files('.')  // Required by SourceTask in order to run
    // gradle task settings
    task.description = 'Run Codenarc analysis for all gradle files.'
    task.group = 'Assessment'
    task.doFirst((Action) new PrepareAssessGradleAction(setting))
    LogCodeNarcReportAction.addLogReportTask(LOG_REPORT_TASK, task)
  }
}
