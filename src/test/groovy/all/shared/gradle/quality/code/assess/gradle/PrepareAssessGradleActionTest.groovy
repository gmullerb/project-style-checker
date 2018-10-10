//  Copyright (c) 2018 Gonzalo Müller Bravo.
//  Licensed under the MIT License (MIT), see LICENSE.txt
package all.shared.gradle.quality.code.assess.gradle

import all.shared.gradle.quality.code.assess.AssessTaskConfig
import all.shared.gradle.testfixtures.SpyProjectBuilder

import groovy.transform.CompileStatic

import org.gradle.api.logging.Logger
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileTree
import org.gradle.api.plugins.quality.CodeNarc
import org.gradle.api.resources.TextResource

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable

import static org.junit.jupiter.api.Assertions.assertAll
import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertFalse
import static org.junit.jupiter.api.Assertions.assertNull
import static org.junit.jupiter.api.Assertions.assertTrue

import static org.mockito.Matchers.any
import static org.mockito.Matchers.anyString
import static org.mockito.Matchers.eq
import static org.mockito.Matchers.argThat
import static org.mockito.Mockito.doReturn
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.never
import static org.mockito.Mockito.spy
import static org.mockito.Mockito.verify

@CompileStatic
class PrepareAssessGradleActionTest {
  private final TextResource mockCodenarcConfig = mock(TextResource)
  private final AssessTaskConfig setting = new AssessTaskConfig()
  private final FileTree mockFileTree = mock(FileTree)
  private final CodeNarc spyTask = spy(SpyProjectBuilder.builder.build().tasks.create('testCodeNarc', CodeNarc))
  private final Logger mockLogger = mock(Logger)
  private final PrepareAssessGradleAction action = new PrepareAssessGradleAction(setting)

  @BeforeEach
  void beforeEachTest() {
    doReturn(mockLogger)
      .when(spyTask)
      .getLogger()
  }

  @Test
  void shouldExecuteAction() {
    final FileCollection mockFiles = mock(FileCollection)
    doReturn(mockFiles)
      .when(mockFileTree)
      .filter(any(Closure))
    setting.config = mockCodenarcConfig
    setting.fileTree = mockFileTree

    action.execute(spyTask)

    assertEquals(mockCodenarcConfig, spyTask.config)
    verify(spyTask).setSource(eq(mockFiles))
    verify(mockLogger, never()).debug(anyString())
  }

  @Test
  void shouldExecuteActionWhenNullValues() {
    action.execute(spyTask)

    assertNull(spyTask.config)
    verify(spyTask, never()).setSource(eq(mockFileTree))
  }

  @Test
  void shouldFilterFileTree() {
    setting.fileTree = mockFileTree

    action.execute(spyTask)

    verify(mockFileTree).filter((Closure<File>) argThat { Closure<File> closure ->
      final File mockFile = mock(File)
      assertAll([
      {
        doReturn('theName')
          .when(mockFile)
          .getName()

        final boolean match = closure(mockFile)

        assertFalse(match)
      } as Executable,
      {
        doReturn('theName.ext')
          .when(mockFile)
          .getName()

        final boolean match = closure(mockFile)

        assertFalse(match)
      } as Executable,
      {
        doReturn('theName.gradle1')
          .when(mockFile)
          .getName()

        final boolean match = closure(mockFile)

        assertFalse(match)
      } as Executable,
      {
        doReturn('theName.gradle')
          .when(mockFile)
          .getName()

        final boolean match = closure(mockFile)

        assertTrue(match)
      } as Executable,
      {
        doReturn('somePath.theName.gradle')
          .when(mockFile)
          .getName()

        final boolean match = closure(mockFile)

        assertTrue(match)
      } as Executable])
      true
    })
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

    verify(mockFileTree).visit(any(Closure))
  }
}
