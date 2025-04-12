/*
 * Copyright © 2017-2019 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.cdap.wrangler.api.parser;

public class ByteSizeTest {

  public static void main(String[] args) {
    testByteSizeParsing();
    testByteSizeConversion();
    System.out.println("All tests passed!");
  }

  private static void testByteSizeParsing() {
    // Test bytes
    ByteSize bytes = new ByteSize("1024B");
    assertEqual(1024, bytes.getBytes(), "Bytes parsing failed");

    // Test kilobytes
    ByteSize kilobytes = new ByteSize("1KB");
    assertEqual(1024, kilobytes.getBytes(), "Kilobytes parsing failed");

    // Test megabytes
    ByteSize megabytes = new ByteSize("1.5MB");
    assertEqual(1572864, megabytes.getBytes(), "Megabytes parsing failed");

    // Test gigabytes
    ByteSize gigabytes = new ByteSize("1GB");
    assertEqual(1073741824, gigabytes.getBytes(), "Gigabytes parsing failed");
  }

  private static void testByteSizeConversion() {
    ByteSize size = new ByteSize("1024KB");

    // Test conversion to different units
    assertEqual(1024 * 1024, size.getBytes(), "Bytes conversion failed");
    assertEqual(1024.0, size.toUnit("KB"), "Conversion to KB failed");
    assertEqual(1.0, size.toUnit("MB"), "Conversion to MB failed");
    assertEqual(0.001, size.toUnit("GB"), "Conversion to GB failed", 0.001);
  }

  // Integer equality
  private static void assertEqual(long expected, long actual, String message) {
    if (expected != actual) {
      throw new AssertionError(message + ": expected " + expected + ", got " + actual);
    }
  }

  // Double equality with default delta
  private static void assertEqual(double expected, double actual, String message) {
    assertEqual(expected, actual, message, 0.0001);
  }

  // Double equality with custom delta
  private static void assertEqual(double expected, double actual, String message, double delta) {
    if (Math.abs(expected - actual) > delta) {
      throw new AssertionError(message + ": expected " + expected + ", got " + actual);
    }
  }
}
