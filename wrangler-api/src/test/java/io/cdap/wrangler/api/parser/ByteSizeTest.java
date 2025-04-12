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

import org.junit.Assert;
import org.junit.Test;

public class ByteSizeTest {

  @Test
  public void testByteSizeParsing() {
    // Test bytes
    ByteSize bytes = new ByteSize("1024B");
    Assert.assertEquals(1024, bytes.getBytes());
    
    // Test kilobytes
    ByteSize kilobytes = new ByteSize("1KB");
    Assert.assertEquals(1024, kilobytes.getBytes());
    
    // Test megabytes
    ByteSize megabytes = new ByteSize("1.5MB");
    Assert.assertEquals(1572864, megabytes.getBytes());
    
    // Test gigabytes
    ByteSize gigabytes = new ByteSize("1GB");
    Assert.assertEquals(1073741824, gigabytes.getBytes());
  }

  @Test
  public void testByteSizeConversion() {
    ByteSize size = new ByteSize("1024KB");
    
    // Test conversion to different units
    Assert.assertEquals(1024 * 1024, size.getBytes());
    Assert.assertEquals(1024, size.toUnit("KB"), 0.001);
    Assert.assertEquals(1, size.toUnit("MB"), 0.001);
    Assert.assertEquals(0.001, size.toUnit("GB"), 0.001);
  }
}