//  Copyright (c) 2018 Gonzalo Müller Bravo.
//  Licensed under the MIT License (MIT), see LICENSE.txt
package all.shared.gradle.quality.code

import all.shared.gradle.quality.code.assess.AssessTaskConfig

import groovy.transform.CompileStatic

@CompileStatic
class ProjectStyleCheckerExtension {
  final AssessTaskConfig common = new AssessTaskConfig()
  final AssessTaskConfig gradle = new AssessTaskConfig()
}
