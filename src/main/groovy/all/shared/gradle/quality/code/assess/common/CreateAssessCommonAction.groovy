//  Copyright (c) 2018 Gonzalo Müller Bravo.
//  Licensed under the MIT License (MIT), see LICENSE.txt
package all.shared.gradle.quality.code.assess.common

import all.shared.gradle.quality.code.assess.AssessTaskDefinition
import all.shared.gradle.quality.code.assess.AssessTaskConfig

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.plugins.quality.Checkstyle

@CompileStatic
class CreateAssessCommonAction implements Action<Checkstyle> {
  public static final AssessTaskDefinition ASSESS_TASK_DEFINITION =
    new AssessTaskDefinition(
      Checkstyle,
      'assessCommon')

  private final AssessTaskConfig setting

  CreateAssessCommonAction(final AssessTaskConfig setting) {
    this.setting = setting
  }

  void execute(final Checkstyle task) {
    // Checkstyle task settings
    task.classpath = task.project.files('dummy').filter { false } // Required by Checkstyle, Not required for Checker modules
    task.source = task.project.files('.')  // Required by SourceTask in order to run
    // gradle task settings
    task.description = 'Run Common Checkstyle analysis for all files.'
    task.group = 'Assessment'
    task.doFirst((Action) new PrepareAssessCommonAction(setting))
  }
}
