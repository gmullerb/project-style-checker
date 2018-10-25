//  Copyright (c) 2018 Gonzalo Müller Bravo.
//  Licensed under the MIT License (MIT), see LICENSE.txt
package all.shared.gradle.quality.code.assess.common

import all.shared.gradle.quality.code.assess.AssessTaskConfig
import all.shared.gradle.testfixtures.SpyProjectFactory

import groovy.transform.CompileStatic

import org.gradle.api.logging.Logger
import org.gradle.api.file.FileTree
import org.gradle.api.plugins.quality.Checkstyle
import org.gradle.api.resources.TextResource

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertNull

import static org.mockito.Matchers.any
import static org.mockito.Matchers.anyString
import static org.mockito.Matchers.eq
import static org.mockito.Mockito.doReturn
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.never
import static org.mockito.Mockito.spy
import static org.mockito.Mockito.verify

@CompileStatic
class PrepareAssessCommonActionTest {
  private final TextResource mockCommonConfig = mock(TextResource)
  private final AssessTaskConfig setting = new AssessTaskConfig()
  private final FileTree mockFileTree = mock(FileTree)
  private final Checkstyle spyTask = spy(SpyProjectFactory.builder.build()
    .tasks.create('testCheckstyle', Checkstyle))
  private final Logger mockLogger = mock(Logger)
  private final PrepareAssessCommonAction action = new PrepareAssessCommonAction(setting)

  @BeforeEach
  void beforeEachTest() {
    doReturn(mockLogger)
      .when(spyTask)
      .getLogger()
  }

  @Test
  void shouldExecuteAction() {
    setting.config = mockCommonConfig
    setting.fileTree = mockFileTree

    action.execute(spyTask)

    assertEquals(mockCommonConfig, spyTask.config)
    verify(spyTask)
      .setSource(eq(mockFileTree))
    verify(mockLogger, never())
      .debug(anyString())
  }

  @Test
  void shouldExecuteActionWhenNullValues() {
    action.execute(spyTask)

    assertNull(spyTask.config)
    verify(spyTask, never())
      .setSource(eq(mockFileTree))
  }

  @Test
  void shouldLogWhenEnabled() {
    doReturn(mockFileTree)
      .when(spyTask)
      .getSource()
    doReturn(true)
      .when(mockLogger)
      .isDebugEnabled()

    action.execute(spyTask)

    verify(mockFileTree)
      .visit(any(Closure))
  }

}
