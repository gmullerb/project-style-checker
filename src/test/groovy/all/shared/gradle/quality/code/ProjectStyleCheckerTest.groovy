//  Copyright (c) 2018 Gonzalo Müller Bravo.
//  Licensed under the MIT License (MIT), see LICENSE.txt
package all.shared.gradle.quality.code

import all.shared.gradle.file.FileListerExtension
import all.shared.gradle.file.FileListerPlugin
import all.shared.gradle.quality.code.assess.common.CreateAssessCommonAction
import all.shared.gradle.quality.code.assess.gradle.CreateAssessGradleAction
import all.shared.gradle.quality.code.config.GroovyCodeStyleConfig
import all.shared.gradle.quality.code.config.CommonCodeStyleConfig
import all.shared.gradle.testfixtures.SpyProjectFactory

import groovy.transform.CompileStatic

import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.plugins.quality.Checkstyle
import org.gradle.api.plugins.quality.CheckstyleExtension
import org.gradle.api.plugins.quality.CheckstylePlugin
import org.gradle.api.plugins.quality.CodeNarc
import org.gradle.api.plugins.quality.CodeNarcExtension
import org.gradle.api.plugins.quality.CodeNarcPlugin
import org.gradle.api.resources.TextResource

import org.junit.jupiter.api.Test

import org.mockito.InOrder

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertFalse
import static org.junit.jupiter.api.Assertions.assertNotNull
import static org.junit.jupiter.api.Assertions.assertNull
import static org.junit.jupiter.api.Assertions.assertTrue

import static org.mockito.Matchers.any
import static org.mockito.Matchers.anyString
import static org.mockito.Matchers.eq
import static org.mockito.Mockito.doNothing
import static org.mockito.Mockito.doReturn
import static org.mockito.Mockito.inOrder
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.never
import static org.mockito.Mockito.spy
import static org.mockito.Mockito.verify

@CompileStatic
class ProjectStyleCheckerTest {
  private final Project spyProject = SpyProjectFactory.build()
  private final ProjectStyleChecker projectStyleChecker = new ProjectStyleChecker(spyProject)

  @Test
  void shouldAddExtension() {
    projectStyleChecker.addExtension(ProjectStyleCheckerPlugin.EXTENSION_NAME)

    assertTrue(spyProject.properties[ProjectStyleCheckerPlugin.EXTENSION_NAME] instanceof ProjectStyleCheckerExtension)
  }

  @Test
  void shouldNotAddExtensionWhenExtensionNameNotAvailable() {
    spyProject.extensions.add(ProjectStyleCheckerPlugin.EXTENSION_NAME, 'someValue')

    projectStyleChecker.addExtension(ProjectStyleCheckerPlugin.EXTENSION_NAME)
  }

  @Test
  void shouldAddRequiredPlugins() {
    projectStyleChecker.addRequiredPlugins()

    verify(spyProject.logger)
      .debug(eq('project-style-check extension applied {} plugin'), eq(BaseStyleConfigWrapperPlugin))
    verify(spyProject.logger)
      .debug(eq('project-style-check extension applied {} plugin'), eq(FileListerPlugin))
    verify(spyProject.logger)
      .debug(eq('project-style-check extension applied {} plugin'), eq(CheckstylePlugin))
    verify(spyProject.logger)
      .debug(eq('project-style-check extension applied {} plugin'), eq(CodeNarcPlugin))
  }

  @Test
  void shouldNotAddRequiredPlugins() {
    spyProject.extensions.add(BaseStyleConfigWrapperPlugin.EXTENSION_NAME, mock(BaseStyleConfigWrapperExtension))
    spyProject.extensions.add(FileListerPlugin.EXTENSION_NAME, mock(FileListerExtension))
    spyProject.extensions.add('checkstyle', mock(CheckstyleExtension))
    spyProject.extensions.add('codenarc', mock(CodeNarcExtension))

    projectStyleChecker.addRequiredPlugins()

    verify(spyProject.logger, never())
      .debug(eq('project-style-check extension applied {} plugin'), any(Class))
  }

  @Test
  void shouldFillExtensionConfigs() {
    final TextResource mockCodenarcConfig = mock(TextResource)
    final TextResource mockCheckstyleConfig = mock(TextResource)
    final CommonCodeStyleConfig commonConfig = new CommonCodeStyleConfig(mockCheckstyleConfig)
    final GroovyCodeStyleConfig groovyConfig = new GroovyCodeStyleConfig(mockCodenarcConfig)
    final BaseStyleConfigWrapperExtension mockBackStyleConfig = new BaseStyleConfigWrapperExtension(commonConfig, null, groovyConfig, null)
    spyProject.extensions.add(BaseStyleConfigWrapperPlugin.EXTENSION_NAME, mockBackStyleConfig)
    final ProjectStyleCheckerExtension config = new ProjectStyleCheckerExtension()

    projectStyleChecker.fillExtensionConfigs(config)

    assertEquals(mockCheckstyleConfig, config.common.config)
    assertEquals(mockCodenarcConfig, config.gradle.config)
    verify(spyProject.logger)
      .debug(eq('project-style-check extension filled with {}'), eq(BaseStyleConfigWrapperPlugin.EXTENSION_NAME))
  }

  @Test
  void shouldFillExtensionFileTree() {
    final FileListerExtension mockFileLister = new FileListerExtension(spyProject)
    final ConfigurableFileTree mockFileTree = mock(ConfigurableFileTree)
    doReturn(mockFileTree)
      .when(spyProject)
      .fileTree(anyString(), any(Closure))
    final File mockFile = mock(File)
    doReturn(mockFile)
      .when(mockFileTree)
      .getDir()
    doReturn('')
      .when(mockFile)
      .getPath()
    doReturn(mockFileTree)
      .when(mockFileTree)
      .exclude(any(Iterable))
    spyProject.extensions.add(FileListerPlugin.EXTENSION_NAME, mockFileLister)

    final ProjectStyleCheckerExtension config = new ProjectStyleCheckerExtension()

    projectStyleChecker.fillExtensionFileTree(config)

    assertEquals(mockFileTree, config.common.fileTree)
    assertEquals(mockFileTree, config.gradle.fileTree)
    verify(spyProject.logger)
      .debug(eq('project-style-check extension filled with {}'), eq(FileListerPlugin.EXTENSION_NAME))
  }

  @Test
  void shouldEstablishCodenarcSettings() {
    final TextResource mockCodenarcConfig = mock(TextResource)
    final ProjectStyleCheckerExtension config = new ProjectStyleCheckerExtension()
    config.gradle.config = mockCodenarcConfig
    final CodeNarcExtension extension = new CodeNarcExtension(spyProject)
    spyProject.extensions.add('codenarc', extension)

    projectStyleChecker.establishCodenarcSettings(config)

    assertEquals(mockCodenarcConfig, extension.config)
  }

  @Test
  void shouldFillAllExtensions() {
    final ProjectStyleChecker spyProjectStyleChecker = spy(new ProjectStyleChecker(spyProject))
    final InOrder order = inOrder(spyProjectStyleChecker)
    doNothing()
      .when(spyProjectStyleChecker)
      .fillExtensionConfigs(any(ProjectStyleCheckerExtension))
    doNothing()
      .when(spyProjectStyleChecker)
      .fillExtensionFileTree(any(ProjectStyleCheckerExtension))
    doNothing()
      .when(spyProjectStyleChecker)
      .establishCodenarcSettings(any(ProjectStyleCheckerExtension))

    spyProjectStyleChecker.fillAllExtensions(new ProjectStyleCheckerExtension())

    order.verify(spyProjectStyleChecker)
      .fillExtensionConfigs(any(ProjectStyleCheckerExtension))
    order.verify(spyProjectStyleChecker)
      .fillExtensionFileTree(any(ProjectStyleCheckerExtension))
    order.verify(spyProjectStyleChecker)
      .establishCodenarcSettings(any(ProjectStyleCheckerExtension))
  }

  @Test
  void shouldAddTasksWhenAlreadyExists() {
    spyProject.tasks.create('codenarcMain', CodeNarc)
    spyProject.tasks.create('codenarcTest', CodeNarc)

    projectStyleChecker.addTasks(new ProjectStyleCheckerExtension())

    assertTrue(spyProject.tasks.getByPath(":$CreateAssessCommonAction.ASSESS_TASK_DEFINITION.name") instanceof Checkstyle)
    assertTrue(spyProject.tasks.getByPath(":$CreateAssessGradleAction.ASSESS_TASK_DEFINITION.name") instanceof CodeNarc)
    assertNotNull(spyProject.tasks.findByPath(":$CreateAssessGradleAction.LOG_REPORT_TASK"))
    assertNotNull(spyProject.tasks.findByPath(":$ProjectStyleChecker.LOG_CODENARC_MAIN_REPORT_TASK"))
    assertNotNull(spyProject.tasks.findByPath(":$ProjectStyleChecker.LOG_CODENARC_TEST_REPORT_TASK"))
    verify(spyProject.logger)
      .debug(eq('Added {} task'), eq(CreateAssessCommonAction.ASSESS_TASK_DEFINITION.name))
    verify(spyProject.logger)
      .debug(eq('Added {} task'), eq(CreateAssessGradleAction.ASSESS_TASK_DEFINITION.name))
  }

  @Test
  void shouldAddTasksWhenAdded() {
    projectStyleChecker.addTasks(new ProjectStyleCheckerExtension())

    spyProject.tasks.create('codenarcMain', CodeNarc)
    spyProject.tasks.create('codenarcTest', CodeNarc)

    assertTrue(spyProject.tasks.getByPath(":$CreateAssessCommonAction.ASSESS_TASK_DEFINITION.name") instanceof Checkstyle)
    assertTrue(spyProject.tasks.getByPath(":$CreateAssessGradleAction.ASSESS_TASK_DEFINITION.name") instanceof CodeNarc)
    assertNotNull(spyProject.tasks.findByPath(":$CreateAssessGradleAction.LOG_REPORT_TASK"))
    assertNotNull(spyProject.tasks.findByPath(":$ProjectStyleChecker.LOG_CODENARC_MAIN_REPORT_TASK"))
    assertNotNull(spyProject.tasks.findByPath(":$ProjectStyleChecker.LOG_CODENARC_TEST_REPORT_TASK"))
    verify(spyProject.logger)
      .debug(eq('Added {} task'), eq(CreateAssessCommonAction.ASSESS_TASK_DEFINITION.name))
    verify(spyProject.logger)
      .debug(eq('Added {} task'), eq(CreateAssessGradleAction.ASSESS_TASK_DEFINITION.name))
  }

  @Test
  void shouldNotAddTasks() {
    spyProject.tasks.create(CreateAssessCommonAction.ASSESS_TASK_DEFINITION.name)
    spyProject.tasks.create(CreateAssessGradleAction.ASSESS_TASK_DEFINITION.name)

    projectStyleChecker.addTasks(new ProjectStyleCheckerExtension())

    assertFalse(spyProject.tasks.getByPath(":$CreateAssessCommonAction.ASSESS_TASK_DEFINITION.name") instanceof Checkstyle)
    assertFalse(spyProject.tasks.getByPath(":$CreateAssessGradleAction.ASSESS_TASK_DEFINITION.name") instanceof CodeNarc)
    assertNull(spyProject.tasks.findByPath(":$ProjectStyleChecker.LOG_CODENARC_MAIN_REPORT_TASK"))
    assertNull(spyProject.tasks.findByPath(":$ProjectStyleChecker.LOG_CODENARC_TEST_REPORT_TASK"))
  }
}
