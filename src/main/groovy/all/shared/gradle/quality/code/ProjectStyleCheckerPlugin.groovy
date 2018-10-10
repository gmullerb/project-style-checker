//  Copyright (c) 2018 Gonzalo Müller Bravo.
//  Licensed under the MIT License (MIT), see LICENSE.txt
package all.shared.gradle.quality.code

import groovy.transform.CompileStatic

import org.gradle.api.Plugin
import org.gradle.api.Project

@CompileStatic
class ProjectStyleCheckerPlugin implements Plugin<Project> {
  public static final String EXTENSION_NAME = 'projectStyleChecker'

  static final boolean complement(final ProjectStyleChecker projectStyleChecker) {
    final ProjectStyleCheckerExtension config = projectStyleChecker.addExtension(EXTENSION_NAME)
    if (config != null) {
      projectStyleChecker.project.logger.debug('Added project-style-check extension')
      projectStyleChecker.addRequiredPlugins()
      projectStyleChecker.fillAllExtensions(config)
      projectStyleChecker.addTasks(config)
      true
    }
    else {
      projectStyleChecker.project.logger.error('Couldn\'t add project-style-check extension')
      false
    }
  }

  void apply(final Project project) {
    complement(new ProjectStyleChecker(project))
  }
}
