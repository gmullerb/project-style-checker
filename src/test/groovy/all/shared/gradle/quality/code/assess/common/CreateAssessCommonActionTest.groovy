//  Copyright (c) 2018 Gonzalo Müller Bravo.
//  Licensed under the MIT License (MIT), see LICENSE.txt
package all.shared.gradle.quality.code.assess.common

import all.shared.gradle.quality.code.assess.AssessTaskConfig
import all.shared.gradle.testfixtures.SpyProjectFactory

import groovy.transform.CompileStatic

import org.gradle.api.Project
import org.gradle.api.plugins.quality.Checkstyle

import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertFalse
import static org.junit.jupiter.api.Assertions.assertTrue

@CompileStatic
class CreateAssessCommonActionTest {
  private final AssessTaskConfig config = new AssessTaskConfig()
  private final Project testProject = SpyProjectFactory.builder.build()
  private final Checkstyle task = testProject.tasks.create('testCheckstyle', Checkstyle)
  private final CreateAssessCommonAction action = new CreateAssessCommonAction(config)

  @Test
  void shouldExecuteAction() {
    final boolean isEmpty = !new File(testProject.projectDir.path, 'createAssessCommonActionTest')
      .createNewFile()

    action.execute(task)

    assertTrue(task.classpath.isEmpty())
    assertEquals(isEmpty, task.source.isEmpty())
    assertEquals('Run Common Checkstyle analysis for all files.', task.description)
    assertEquals('Assessment', task.group)
    assertEquals(2, task.actions.size())
  }

  @Test
  void shouldExecuteActionWithDummyFolder() {
    final File dummyDir = new File(testProject.projectDir.path, 'dummy')
    dummyDir.mkdir()
    new File(dummyDir, 'createAssessCommonActionTest')
      .createNewFile()

    action.execute(task)

    assertTrue(task.classpath.isEmpty())
    assertFalse(task.source.isEmpty())
  }
}
