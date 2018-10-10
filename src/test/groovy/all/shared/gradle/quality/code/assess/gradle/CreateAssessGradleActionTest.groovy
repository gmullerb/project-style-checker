//  Copyright (c) 2018 Gonzalo Müller Bravo.
//  Licensed under the MIT License (MIT), see LICENSE.txt
package all.shared.gradle.quality.code.assess.gradle

import all.shared.gradle.quality.code.assess.AssessTaskConfig
import all.shared.gradle.testfixtures.SpyProjectBuilder

import groovy.transform.CompileStatic

import org.gradle.api.Project
import org.gradle.api.plugins.quality.CodeNarc

import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
class CreateAssessGradleActionTest {
  @Test
  void shouldExecuteAction() {
    final Project testProject = SpyProjectBuilder.builder.build()
    final AssessTaskConfig config = new AssessTaskConfig()
    final CodeNarc task = testProject.tasks.create('testCodeNarc', CodeNarc)
    final CreateAssessGradleAction action = new CreateAssessGradleAction(config)
    final boolean isEmpty = !new File(testProject.projectDir.path, 'createAssessGradleActionTest').createNewFile()

    action.execute(task)

    assertEquals(isEmpty, task.source.isEmpty())
    assertEquals('Run Codenarc analysis for all gradle files.', task.description)
    assertEquals('Assessment', task.group)
    assertEquals(2, task.actions.size())
  }
}
