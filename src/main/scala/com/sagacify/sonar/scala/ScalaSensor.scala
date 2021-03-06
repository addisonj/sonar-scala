package com.sagacify.sonar.scala

import scala.io.Source
import scala.collection.JavaConversions._

import org.sonar.api.batch.fs.FileSystem
import org.sonar.api.batch.Sensor
import org.sonar.api.batch.SensorContext
import org.sonar.api.measures.{CoreMetrics => CM}
import org.sonar.api.resources.Project


class ScalaSensor(scala: Scala, fs: FileSystem) extends Sensor {

  def shouldExecuteOnProject(project: Project): Boolean = {
    return fs.hasFiles(fs.predicates().hasLanguage(scala.getKey()));
  }

  def analyse(project: Project, context: SensorContext): Unit = {

    val charset = fs.encoding().toString()
    val version = "2.11.8"

    val inputFiles = fs.inputFiles(fs.predicates().hasLanguage(scala.getKey()))

    inputFiles.foreach{ inputFile =>
      context.saveMeasure(inputFile, CM.FILES, 1.0);

      val sourceCode = Source.fromFile(inputFile.file, charset).mkString
      val tokens = Scala.tokenize(sourceCode, version)

      context.saveMeasure(inputFile,
                          CM.COMMENT_LINES,
                          Measures.count_comment_lines(tokens))
      context.saveMeasure(inputFile,
                          CM.NCLOC,
                          Measures.count_ncloc(tokens))

      // context.saveMeasure(input, CM.CLASSES, classes)
      // context.saveMeasure(input, CM.FUNCTIONS, methods)
      // context.saveMeasure(input, CM.ACCESSORS, accessors)
      // context.saveMeasure(input, CM.COMPLEXITY_IN_FUNCTIONS, complexityInMethods)
      // context.saveMeasure(input, CM.COMPLEXITY_IN_CLASSES, fileComplexity)
      // context.saveMeasure(input, CM.COMPLEXITY, fileComplexity)
      // context.saveMeasure(input, CM.PUBLIC_API, publicApiChecker.getPublicApi())
      // context.saveMeasure(input, CM.PUBLIC_DOCUMENTED_API_DENSITY, publicApiChecker.getDocumentedPublicApiDensity())
      // context.saveMeasure(input, CM.PUBLIC_UNDOCUMENTED_API, publicApiChecker.getUndocumentedPublicApi())

    }
  }
}

