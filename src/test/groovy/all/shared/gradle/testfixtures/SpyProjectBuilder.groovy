//  Copyright (c) 2018 Gonzalo Müller Bravo.
//  Licensed under the MIT License (MIT), see LICENSE.txt
package all.shared.gradle.testfixtures

import groovy.transform.CompileStatic

import org.gradle.api.Project
import org.gradle.api.internal.plugins.PluginManagerInternal
import org.gradle.api.plugins.PluginContainer
import org.gradle.api.logging.Logger
import org.gradle.testfixtures.ProjectBuilder

import static org.mockito.Mockito.doReturn
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.spy

@CompileStatic
final class SpyProjectBuilder {
  public static final ProjectBuilder builder = ProjectBuilder.builder()

  private SpyProjectBuilder() { }

  @SuppressWarnings(['UseOnlyMockOrSpyPrefixOnTestFiles', 'UnnecessaryGetter'])
  static Project build(final ProjectBuilder builder) {
    final Project spyProject = spy(builder.build())

    doReturn(mock(PluginManagerInternal))
      .when(spyProject)
      .getPluginManager()
    doReturn(mock(PluginContainer))
      .when(spyProject)
      .getPlugins()
    doReturn(mock(Logger))
      .when(spyProject)
      .getLogger()

    spyProject
  }

  static Project build() {
    build(builder)
  }
}
