//  Copyright (c) 2018 Gonzalo Müller Bravo.
//  Licensed under the MIT License (MIT), see LICENSE.txt
package all.shared.gradle.quality.code.assess.gradle

import all.shared.gradle.testfixtures.SpyProjectBuilder

import groovy.transform.CompileStatic

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.logging.Logger
import org.gradle.api.plugins.quality.CodeNarc
import org.gradle.api.plugins.quality.CodeNarcReports
import org.gradle.api.reporting.SingleFileReport

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.mockito.InOrder

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertFalse
import static org.junit.jupiter.api.Assertions.assertTrue

import static org.mockito.Matchers.any
import static org.mockito.Matchers.anyString
import static org.mockito.Matchers.eq
import static org.mockito.Mockito.doReturn
import static org.mockito.Mockito.inOrder
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.never
import static org.mockito.Mockito.spy
import static org.mockito.Mockito.verify

@CompileStatic
class LogCodeNarcReportActionTest {
  private final Project spyProject = SpyProjectBuilder.build()
  private final CodeNarc spyTaskToReport = spy((CodeNarc) spyProject.tasks.create('testCodeNarc', CodeNarc))
  private final Task mockTask = mock(Task)
  private final Logger mockLogger = mock(Logger)
  private final CodeNarcReports mockReports = mock(CodeNarcReports)
  private final SingleFileReport mockReport = mock(SingleFileReport)

  private File theFile

  @BeforeEach
  void beforeEachTest() {
    doReturn(spyProject)
      .when(mockTask)
      .getProject()
    doReturn(mockLogger)
      .when(spyTaskToReport)
      .getLogger()
    doReturn(mockLogger)
      .when(mockTask)
      .getLogger()
    doReturn(mockReports)
      .when(spyTaskToReport)
      .getReports()
    doReturn(mockReport)
      .when(mockReports)
      .getText()
  }

  @AfterEach
  void afterEachTest() {
    theFile?.delete()
  }

  @Test
  void shouldAddReportTask() {
    final boolean result = LogCodeNarcReportAction.addLogReportTask('someLogReportTask', spyTaskToReport)

    assertTrue(result)
    final Task logReportTask = spyTaskToReport.project.tasks.getByPath('someLogReportTask')
    assertEquals([logReportTask] as Set, spyTaskToReport.finalizedBy.getDependencies(spyTaskToReport))
    assertEquals(1, logReportTask.actions.size())
    assertEquals('Report', logReportTask.group)
    assertEquals('Logs report for testCodeNarc task', logReportTask.description)
    verify(mockReport).setEnabled(eq(true))
    verify(mockLogger).debug(eq('Added {} task'), eq('someLogReportTask'))
  }

  @Test
  void shouldNotAddReportTask() {
    spyProject.tasks.create('someLogReportTask')

    final boolean result = LogCodeNarcReportAction.addLogReportTask('someLogReportTask', spyTaskToReport)

    assertFalse(result)
    verify(mockReport, never()).setEnabled(any(Boolean))
    verify(mockLogger).debug(eq('There is already a {} task defined, it not possible to log report'), eq('someLogReportTask'))
  }

  @Test
  void shouldExecuteAction() {
    theFile = File.createTempFile('logCodeNarcReportActionTest', '')
    theFile.write('Violation:text')
    doReturn(theFile)
      .when(spyProject)
      .file(any())
    final LogCodeNarcReportAction action = new LogCodeNarcReportAction(spyTaskToReport)

    action.execute(mockTask)

    verify(mockLogger).debug(eq('Logging CodeNarc report'))
    verify(mockLogger).quiet(eq('Violation:text'))
    verify(mockLogger, never()).error(anyString(), any())
  }

  @Test
  void shouldExecuteActionWithNoLoggingWhenNoReportFile() {
    final File mockFile = mock(File)
    doReturn(mockFile)
      .when(spyProject)
      .file(any())
    doReturn(false)
      .when(mockFile)
      .exists()
    final LogCodeNarcReportAction action = new LogCodeNarcReportAction(spyTaskToReport)

    action.execute(mockTask)

    verify(mockLogger).error(eq('Enable text report for {} task to be able to log report: "reports.text.enabled = true"'), eq(spyTaskToReport))
    verify(mockLogger, never()).debug(anyString())
    verify(mockLogger, never()).quiet(anyString())
  }

  @Test
  void shouldFilterLinesWhenExecuting() {
    theFile = File.createTempFile('logCodeNarcReportActionTest', '')
    theFile.write('File:text0\nViolation:text1Src=[text1a\n Violation:text2\n  File:text3\nFiletext\n  Violationtext\n' +
      'textViolation\nViolation:text4')
    doReturn(theFile)
      .when(spyProject)
      .file(any())
    final LogCodeNarcReportAction action = new LogCodeNarcReportAction(spyTaskToReport)

    action.execute(mockTask)

    final InOrder orderedMock = inOrder(mockLogger)
    orderedMock.verify(mockLogger).quiet(eq('File:text0'))
    orderedMock.verify(mockLogger).quiet(eq('Violation:text1'))
    orderedMock.verify(mockLogger).quiet(eq(' Violation:text2'))
    orderedMock.verify(mockLogger).quiet(eq('Violation:text4'))
    orderedMock.verify(mockLogger, never()).quiet(anyString())
  }
}
