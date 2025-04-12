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

 package io.cdap.wrangler.steps;

 import io.cdap.wrangler.api.Row;
 import io.cdap.wrangler.TestingRig;
 import org.junit.Assert;
 import org.junit.Test;
 
 import java.util.ArrayList;
 import java.util.List;
 
 public class AggregateStatsTest {
   @Test
   public void testAggregateStats() throws Exception {
     // Create sample input data
     List<Row> rows = new ArrayList<>();
 
     Row row1 = new Row();
     row1.add("data_transfer_size", "100KB");
     row1.add("response_time", "200ms");
     rows.add(row1);
 
     Row row2 = new Row();
     row2.add("data_transfer_size", "200KB");
     row2.add("response_time", "300ms");
     rows.add(row2);
 
     Row row3 = new Row();
     row3.add("data_transfer_size", "300KB");
     row3.add("response_time", "500ms");
     rows.add(row3);
 
     // Define the recipe
     String[] recipe = new String[] {
       "aggregate-stats :data_transfer_size :response_time total_size_mb total_time_sec"
     };
 
     // Execute the recipe
     List<Row> results = TestingRig.execute(recipe, rows);
 
     // Verify results
     Assert.assertEquals(1, results.size());
 
     // Expected results (600KB = ~0.586MB, 1000ms = 1s)
     double expectedTotalSizeInMB = 600 * 1024 / (1024.0 * 1024.0);
     double expectedTotalTimeInSec = 1000 / 1000.0;
 
     double actualSize = ((Number) results.get(0).getValue("total_size_mb")).doubleValue();
     double actualTime = ((Number) results.get(0).getValue("total_time_sec")).doubleValue();
 
     Assert.assertEquals(expectedTotalSizeInMB, actualSize, 0.001);
     Assert.assertEquals(expectedTotalTimeInSec, actualTime, 0.001);
   }
 }
 
