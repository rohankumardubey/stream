/*
 * Copyright (c) 2019 The StreamX Project
 * <p>
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.streamxhub.streamx.common.util

import org.yaml.snakeyaml.Yaml

import java.io._
import java.util.{Properties, Scanner, LinkedHashMap => JavaLinkedMap}
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.collection.mutable.{Map => MutableMap}

/**
 * @author benjobs
 */
object PropertiesUtils {

  def readFile(filename: String): String = {
    val file = new File(filename)
    require(file.exists(), s"file $file does not exist")
    require(file.isFile, s"file $file is not a normal file")
    val scanner = new Scanner(file)
    val buffer = new StringBuilder
    while (scanner.hasNextLine) {
      buffer.append(scanner.nextLine()).append("\r\n")
    }
    scanner.close()
    buffer.toString()
  }

  private[this] def eachAppendYamlItem(prefix: String, k: String, v: Any, proper: collection.mutable.Map[String, String]): Map[String, String] = {
    v match {
      case map: JavaLinkedMap[String, Any] =>
        map.flatMap(x => {
          prefix match {
            case "" => eachAppendYamlItem(k, x._1, x._2, proper)
            case other => eachAppendYamlItem(s"$other.$k", x._1, x._2, proper)
          }
        }).toMap
      case text =>
        val value = text match {
          case null => ""
          case other => other.toString
        }
        prefix match {
          case "" => proper += k -> value
          case other => proper += s"$other.$k" -> value
        }
        proper.toMap
    }
  }

  def fromYamlText(text: String): Map[String, String] = {
    try {
      val map = MutableMap[String, String]()
      new Yaml()
        .load(text)
        .asInstanceOf[java.util.Map[String, Map[String, Any]]]
        .flatMap(x => eachAppendYamlItem("", x._1, x._2, map)).toMap
    } catch {
      case e: IOException => throw new IllegalArgumentException(s"Failed when loading conf error:", e)
    }
  }

  def fromPropertiesText(conf: String): Map[String, String] = {
    try {
      val properties = new Properties()
      properties.load(new StringReader(conf))
      properties.stringPropertyNames().asScala.map(k => (k, properties.getProperty(k).trim)).toMap
    } catch {
      case e: IOException => throw new IllegalArgumentException(s"Failed when loading properties ", e)
    }
  }

  /** Load Yaml present in the given file. */
  def fromYamlFile(filename: String): Map[String, String] = {
    val file = new File(filename)
    require(file.exists(), s"Yaml file $file does not exist")
    require(file.isFile, s"Yaml file $file is not a normal file")
    val inputStream: InputStream = new FileInputStream(file)
    try {
      val map = MutableMap[String, String]()
      new Yaml()
        .load(inputStream)
        .asInstanceOf[java.util.Map[String, Map[String, Any]]]
        .flatMap(x => eachAppendYamlItem("", x._1, x._2, map)).toMap
    } catch {
      case e: IOException => throw new IllegalArgumentException(s"Failed when loading properties from $filename", e)
    } finally {
      inputStream.close()
    }
  }

  /** Load properties present in the given file. */
  def fromPropertiesFile(filename: String): Map[String, String] = {
    val file = new File(filename)
    require(file.exists(), s"Properties file $file does not exist")
    require(file.isFile, s"Properties file $file is not a normal file")

    val inReader = new InputStreamReader(new FileInputStream(file), "UTF-8")
    try {
      val properties = new Properties()
      properties.load(inReader)
      properties.stringPropertyNames().asScala.map(k => (k, properties.getProperty(k).trim)).toMap
    } catch {
      case e: IOException => throw new IllegalArgumentException(s"Failed when loading properties from $filename", e)
    } finally {
      inReader.close()
    }
  }

  /** Load Yaml present in the given file. */
  def fromYamlFile(inputStream: InputStream): Map[String, String] = {
    require(inputStream != null, s"Properties inputStream  must be not null")
    try {
      val map = MutableMap[String, String]()
      new Yaml()
        .load(inputStream)
        .asInstanceOf[java.util.Map[String, Map[String, Any]]]
        .flatMap(x => eachAppendYamlItem("", x._1, x._2, map)).toMap
    } catch {
      case e: IOException => throw new IllegalArgumentException(s"Failed when loading yaml from inputStream", e)
    } finally {
      inputStream.close()
    }
  }

  /** Load properties present in the given file. */
  def fromPropertiesFile(inputStream: InputStream): Map[String, String] = {
    require(inputStream != null, s"Properties inputStream  must be not null")
    try {
      val properties = new Properties()
      properties.load(inputStream)
      properties.stringPropertyNames().asScala.map(k => (k, properties.getProperty(k).trim)).toMap
    } catch {
      case e: IOException => throw new IllegalArgumentException(s"Failed when loading properties from inputStream", e)
    }
  }

}
