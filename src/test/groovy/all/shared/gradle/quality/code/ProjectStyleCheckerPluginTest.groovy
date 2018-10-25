//  Copyright (c) 2018 Gonzalo Müller Bravo.
//  Licensed under the MIT License (MIT), see LICENSE.txt
package all.shared.gradle.quality.code

import all.shared.gradle.testfixtures.SpyProjectFactory

import groovy.transform.CompileStatic

import org.gradle.api.Project

import org.junit.jupiter.api.Test

import org.mockito.InOrder

import static org.junit.jupiter.api.Assertions.assertFalse
import static org.junit.jupiter.api.Assertions.assertTrue

import static org.mockito.Matchers.any
import static org.mockito.Matchers.eq
import static org.mockito.Mockito.doNothing
import static org.mockito.Mockito.inOrder
import static org.mockito.Mockito.never
import static org.mockito.Mockito.spy
import static org.mockito.Mockito.verify

@CompileStatic
class ProjectStyleCheckerPluginTest {
  private final Project spyProject = SpyProjectFactory.build()

  @Test
  void shouldComplement() {
    final ProjectStyleChecker mockProjectStyleChecker = spy(new ProjectStyleChecker(spyProject))
    final InOrder order = inOrder(mockProjectStyleChecker)
    doNothing()
      .when(mockProjectStyleChecker)
      .addRequiredPlugins()
    doNothing()
      .when(mockProjectStyleChecker)
      .fillAllExtensions(any(ProjectStyleCheckerExtension))
    doNothing()
      .when(mockProjectStyleChecker)
      .addTasks(any(ProjectStyleCheckerExtension))

    final boolean result = ProjectStyleCheckerPlugin.complement(mockProjectStyleChecker)

    assertTrue(result)
    order.verify(mockProjectStyleChecker)
      .addRequiredPlugins()
    order.verify(mockProjectStyleChecker)
      .fillAllExtensions(any(ProjectStyleCheckerExtension))
    order.verify(mockProjectStyleChecker)
      .addTasks(any(ProjectStyleCheckerExtension))
    verify(spyProject.logger)
      .debug(eq('Added project-style-check extension'))
  }

  @Test
  void shouldNotComplement() {
    spyProject.extensions.add(ProjectStyleCheckerPlugin.EXTENSION_NAME, 'someValue')
    final ProjectStyleChecker spyProjectStyleChecker = spy(new ProjectStyleChecker(spyProject))

    final boolean result = ProjectStyleCheckerPlugin.complement(spyProjectStyleChecker)

    assertFalse(result)
    verify(spyProjectStyleChecker, never())
      .addRequiredPlugins()
    verify(spyProjectStyleChecker, never())
      .fillAllExtensions(any(ProjectStyleCheckerExtension))
    verify(spyProjectStyleChecker, never())
      .addTasks(any(ProjectStyleCheckerExtension))
    verify(spyProject.logger)
      .error(eq('Couldn\'t add project-style-check extension'))
  }

  @Test
  void shouldApplyPlugin() {
    spyProject.extensions.add(ProjectStyleCheckerPlugin.EXTENSION_NAME, 'someValue')
    final ProjectStyleCheckerPlugin plugin = new ProjectStyleCheckerPlugin()

    plugin.apply(spyProject)

    verify(spyProject.logger)
      .error(eq('Couldn\'t add project-style-check extension'))
  }
}
