//  Copyright (c) 2018 Gonzalo Müller Bravo.
//  Licensed under the MIT License (MIT), see LICENSE.txt
package all.shared.gradle.quality.code.assess

import groovy.transform.CompileStatic

import org.gradle.api.Task

@CompileStatic
final class AssessTaskDefinition<T extends Task> {
  final String name
  final Class<T> type

  AssessTaskDefinition(
      final Class<T> type,
      final String name) {
    this.name = name
    this.type = type
  }
}
