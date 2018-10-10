//  Copyright (c) 2018 Gonzalo Müller Bravo.
//  Licensed under the MIT License (MIT), see LICENSE.txt
package all.shared.gradle.quality.code.assess

import groovy.transform.CompileStatic

import org.gradle.api.file.FileTree
import org.gradle.api.resources.TextResource

@CompileStatic
class AssessTaskConfig {
  FileTree fileTree
  TextResource config
}
