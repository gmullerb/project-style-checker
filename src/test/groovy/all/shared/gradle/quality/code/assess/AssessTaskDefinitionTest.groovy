//  Copyright (c) 2018 Gonzalo Müller Bravo.
//  Licensed under the MIT License (MIT), see LICENSE.txt
package all.shared.gradle.quality.code.assess

import groovy.transform.CompileStatic

import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
class AssessTaskDefinitionTest {
  @Test
  void shouldCreateAAssessTaskDefinition() {
    final AssessTaskDefinition definition = new AssessTaskDefinition(Object, 'theName')

    assertEquals(Object, definition.type)
    assertEquals('theName', definition.name)
  }
}
