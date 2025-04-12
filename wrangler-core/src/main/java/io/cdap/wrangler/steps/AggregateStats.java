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

import io.cdap.wrangler.api.Arguments;
import io.cdap.wrangler.api.Directive;
import io.cdap.wrangler.api.DirectiveExecutionException;
import io.cdap.wrangler.api.DirectiveParseException;
import io.cdap.wrangler.api.ExecutorContext;
import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.api.annotations.Categories;
import io.cdap.wrangler.api.parser.ByteSize;
import io.cdap.wrangler.api.parser.ColumnName;
import io.cdap.wrangler.api.parser.Text;
import io.cdap.wrangler.api.parser.TimeDuration;
import io.cdap.wrangler.api.parser.TokenType;
import io.cdap.wrangler.api.parser.UsageDefinition;

import java.util.Collections;
import java.util.List;

/**
 * A directive for aggregating byte size and time duration values.
 */
@Categories(categories = { "aggregator", "statistics" })
public class AggregateStats implements Directive {
  public static final String NAME = "aggregate-stats";
  private String byteSizeColumn;
  private String timeDurationColumn;
  private String totalSizeColumn;
  private String totalTimeColumn;
  private String sizeUnit = "MB"; // Default output unit for size
  private String timeUnit = "s";  // Default output unit for time

  @Override
  public UsageDefinition define() {
    UsageDefinition.Builder builder = UsageDefinition.builder(NAME);
    builder.define("byteSizeColumn", TokenType.COLUMN_NAME);
    builder.define("timeDurationColumn", TokenType.COLUMN_NAME);
    builder.define("totalSizeColumn", TokenType.COLUMN_NAME);
    builder.define("totalTimeColumn", TokenType.COLUMN_NAME);
    builder.define("sizeUnit", TokenType.TEXT, true);
    builder.define("timeUnit", TokenType.TEXT, true);
    return builder.build();
  }

  @Override
  public void initialize(Arguments args) throws DirectiveParseException {
    this.byteSizeColumn = ((ColumnName) args.value("byteSizeColumn")).value();
    this.timeDurationColumn = ((ColumnName) args.value("timeDurationColumn")).value();
    this.totalSizeColumn = ((ColumnName) args.value("totalSizeColumn")).value();
    this.totalTimeColumn = ((ColumnName) args.value("totalTimeColumn")).value();

    if (args.contains("sizeUnit")) {
      this.sizeUnit = ((Text) args.value("sizeUnit")).value();
    }

    if (args.contains("timeUnit")) {
      this.timeUnit = ((Text) args.value("timeUnit")).value();
    }
  }

  @Override
  public List<Row> execute(List<Row> rows, ExecutorContext context) throws DirectiveExecutionException {
    long totalBytes = 0;
    long totalNanoseconds = 0;

    for (Row row : rows) {
      if (row.find(byteSizeColumn) != -1) {
        Object byteSizeValue = row.getValue(byteSizeColumn);
        if (byteSizeValue != null) {
          try {
            ByteSize byteSize = (byteSizeValue instanceof ByteSize)
                ? (ByteSize) byteSizeValue
                : new ByteSize(byteSizeValue.toString());
            totalBytes += byteSize.getBytes();
          } catch (Exception e) {
            // Skip unparseable value
          }
        }
      }

      if (row.find(timeDurationColumn) != -1) {
        Object timeDurationValue = row.getValue(timeDurationColumn);
        if (timeDurationValue != null) {
          try {
            TimeDuration timeDuration = (timeDurationValue instanceof TimeDuration)
                ? (TimeDuration) timeDurationValue
                : new TimeDuration(timeDurationValue.toString());
            totalNanoseconds += timeDuration.getNanoseconds();
          } catch (Exception e) {
            // Skip unparseable value
          }
        }
      }
    }

    double totalSizeInUnit = convertBytes(totalBytes, sizeUnit);
    double totalTimeInUnit = convertNanoseconds(totalNanoseconds, timeUnit);

    Row resultRow = new Row();
    resultRow.add(totalSizeColumn, totalSizeInUnit);
    resultRow.add(totalTimeColumn, totalTimeInUnit);

    return Collections.singletonList(resultRow);
  }

  private double convertBytes(long bytes, String unit) {
    unit = unit.toUpperCase();
    switch (unit) {
      case "B":
        return bytes;
      case "K":
      case "KB":
        return bytes / 1024.0;
      case "M":
      case "MB":
        return bytes / (1024.0 * 1024.0);
      case "G":
      case "GB":
        return bytes / (1024.0 * 1024.0 * 1024.0);
      case "T":
      case "TB":
        return bytes / (1024.0 * 1024.0 * 1024.0 * 1024.0);
      case "P":
      case "PB":
        return bytes / (1024.0 * 1024.0 * 1024.0 * 1024.0 * 1024.0);
      default:
        return bytes;
    }
  }

  private double convertNanoseconds(long nanoseconds, String unit) {
    unit = unit.toLowerCase();
    switch (unit) {
      case "ns":
        return nanoseconds;
      case "ms":
        return nanoseconds / 1_000_000.0;
      case "s":
        return nanoseconds / 1_000_000_000.0;
      case "m":
      case "min":
        return nanoseconds / (60.0 * 1_000_000_000.0);
      case "h":
        return nanoseconds / (60.0 * 60.0 * 1_000_000_000.0);
      case "d":
        return nanoseconds / (24.0 * 60.0 * 60.0 * 1_000_000_000.0);
      default:
        return nanoseconds / 1_000_000.0;
    }
  }

  @Override
  public void destroy() {
    // No-op
  }
}
