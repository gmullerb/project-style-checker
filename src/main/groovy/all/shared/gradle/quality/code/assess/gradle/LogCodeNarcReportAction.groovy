//  Copyright (c) 2018 Gonzalo Müller Bravo.
//  Licensed under the MIT License (MIT), see LICENSE.txt
package all.shared.gradle.quality.code.assess.gradle

import groovy.transform.CompileStatic

import org.gradle.api.Action
import org.gradle.api.Task
import org.gradle.api.plugins.quality.CodeNarc

@CompileStatic
class LogCodeNarcReportAction implements Action<Task> {
  private final CodeNarc taskToReport

  LogCodeNarcReportAction(final CodeNarc taskToReport) {
    this.taskToReport = taskToReport
  }

  static final boolean addLogReportTask(
      final String logReportTaskName,
      final CodeNarc task) {
    if (task.project.tasks.findByPath(logReportTaskName) == null) {
      task.reports.text.enabled = true
      task.finalizedBy(task.project.tasks
        .create([
          name: logReportTaskName,
          action: new LogCodeNarcReportAction(task),
          group: 'Report',
          description: "Logs report for ${task.name} task"]))
      task.logger.debug('Added {} task', logReportTaskName)
      true
    }
    else {
      task.logger.debug('There is already a {} task defined, it not possible to log report', logReportTaskName)
      false
    }
  }

  void execute(final Task thisTask) {
    final File reportFile = thisTask.project.file(taskToReport.reports.text.destination)
    if (reportFile.exists()) {
      thisTask.logger.debug('Logging CodeNarc report')
      reportFile.readLines()
        .findAll { String line -> line.matches('(^File:.*)|(^\\s*Violation:.*)') }
        *.replaceFirst('Src=\\[.*', '')
        .each { thisTask.logger.quiet(it as String) }
      thisTask.logger.debug('CodeNarc report logged for {} task', taskToReport)
    }
    else {
      thisTask.logger.error('Enable text report for {} task to be able to log report: "reports.text.enabled = true"', taskToReport)
    }
  }
}
