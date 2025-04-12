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

public class TimeDurationTest {

  public static void main(String[] args) {
    testTimeDurationParsing();
    testTimeDurationConversion();
    System.out.println("All tests passed!");
  }

  private static void testTimeDurationParsing() {
    // Test milliseconds
    TimeDuration ms = new TimeDuration("500ms");
    assertEqual(500 * 1000000L, ms.getNanoseconds(), "Milliseconds to nanoseconds failed");
    assertEqual(500L, ms.getMilliseconds(), "Milliseconds parsing failed");

    // Test seconds
    TimeDuration s = new TimeDuration("1.5s");
    assertEqual(1500L, s.getMilliseconds(), "Seconds to milliseconds failed");
    assertEqual(1500000000L, s.getNanoseconds(), "Seconds to nanoseconds failed");

    // Test minutes
    TimeDuration min = new TimeDuration("2min");
    assertEqual(120L, min.getSeconds(), "Minutes to seconds failed");
  }

  private static void testTimeDurationConversion() {
    TimeDuration duration = new TimeDuration("60s");

    // Test conversion to different units
    assertEqual(60 * 1000000000L, duration.getNanoseconds(), "Seconds to nanoseconds conversion failed");
    assertEqual(60000L, duration.getMilliseconds(), "Seconds to milliseconds conversion failed");
    assertEqual(60L, duration.getSeconds(), "Seconds conversion failed");
    assertEqual(1.0, duration.toUnit("min"), "Seconds to minutes conversion failed", 0.001);
  }

  // Integer equality
  private static void assertEqual(long expected, long actual, String message) {
    if (expected != actual) {
      throw new AssertionError(message + ": expected " + expected + ", got " + actual);
    }
  }

  // Double equality with default delta
  @SuppressWarnings("unused")
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

