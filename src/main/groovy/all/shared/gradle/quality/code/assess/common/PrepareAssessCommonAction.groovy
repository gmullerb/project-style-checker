//  Copyright (c) 2018 Gonzalo Müller Bravo.
//  Licensed under the MIT License (MIT), see LICENSE.txt
package all.shared.gradle.quality.code.assess.common

import all.shared.gradle.quality.code.assess.AssessTaskConfig

import groovy.transform.CompileStatic

import org.gradle.api.Action
import org.gradle.api.plugins.quality.Checkstyle

@CompileStatic
class PrepareAssessCommonAction implements Action<Checkstyle> {
  private final AssessTaskConfig setting

  PrepareAssessCommonAction(final AssessTaskConfig setting) {
    this.setting = setting
  }

  void execute(final Checkstyle task) {
    if (setting.config != null) {
      task.config = setting.config
    }
    if (setting.fileTree != null) {
      task.source = setting.fileTree
    }
    if (task.logger.debugEnabled) {
      task.source.visit { task.logger.debug "$task.name checking style of $it" }
    }
  }
}
