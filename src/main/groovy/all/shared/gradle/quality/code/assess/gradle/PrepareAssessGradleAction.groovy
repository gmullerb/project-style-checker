//  Copyright (c) 2018 Gonzalo Müller Bravo.
//  Licensed under the MIT License (MIT), see LICENSE.txt
package all.shared.gradle.quality.code.assess.gradle

import all.shared.gradle.quality.code.assess.AssessTaskConfig

import groovy.transform.CompileStatic

import org.gradle.api.Action
import org.gradle.api.plugins.quality.CodeNarc

@CompileStatic
class PrepareAssessGradleAction implements Action<CodeNarc> {
  private final AssessTaskConfig setting

  PrepareAssessGradleAction(final AssessTaskConfig setting) {
    this.setting = setting
  }

  void execute(final CodeNarc task) {
    if (setting.config) {
      task.config = setting.config
    }
    if (setting.fileTree != null) {
      task.source = setting.fileTree.filter { final File file -> file.name.matches('.*\\.gradle$') }
    }
    if (task.logger.debugEnabled) {
      task.source.visit { task.logger.debug "$task.name checking style of $it" }
    }
  }
}
