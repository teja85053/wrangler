package io.cdap.wrangler.api.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Represents a token for time duration values with units (e.g., "5ms", "2.1s").
 */
public class TimeDuration implements Token {
  private final String originalValue;
  private final double value;
  private final String unit;
  private final long nanoseconds;  // Using nanoseconds as the canonical unit

  public TimeDuration(String value) {
    this.originalValue = value;
    
    // Extract numeric value and unit from the token string
    String numericPart = value.replaceAll("[^0-9.]", "");
    String unitPart = value.replaceAll("[0-9.]", "");
    
    this.value = Double.parseDouble(numericPart);
    this.unit = unitPart.toLowerCase();
    this.nanoseconds = convertToNanoseconds(this.value, this.unit);
  }

  /**
   * Converts the value with unit to nanoseconds.
   *
   * @param value Numeric value
   * @param unit Unit (ns, ms, s, min, h, d)
   * @return Value converted to nanoseconds
   */
  private long convertToNanoseconds(double value, String unit) {
    switch (unit) {
      case "ns":
        return (long) value;
      case "ms":
        return (long) (value * 1_000_000);
      case "s":
        return (long) (value * 1_000_000_000);
      case "m":
      case "min":
        return (long) (value * 60 * 1_000_000_000);
      case "h":
        return (long) (value * 60 * 60 * 1_000_000_000);
      case "d":
        return (long) (value * 24 * 60 * 60 * 1_000_000_000);
      default:
        // Default to milliseconds if unit is not recognized
        return (long) (value * 1_000_000);
    }
  }

  /**
   * Gets the value in nanoseconds.
   *
   * @return Value in nanoseconds
   */
  public long getNanoseconds() {
    return nanoseconds;
  }

  /**
   * Gets the value in milliseconds.
   *
   * @return Value in milliseconds
   */
  public long getMilliseconds() {
    return nanoseconds / 1_000_000;
  }

  /**
   * Gets the value in seconds.
   *
   * @return Value in seconds
   */
  public long getSeconds() {
    return nanoseconds / 1_000_000_000;
  }

  /**
   * Gets the original numeric value.
   *
   * @return Original numeric value
   */
  public double getValue() {
    return value;
  }

  /**
   * Gets the unit.
   *
   * @return Unit (ns, ms, s, min, h, d)
   */
  public String getUnit() {
    return unit;
  }

  /**
   * Convert the nanoseconds to a specific unit.
   *
   * @param targetUnit Target unit (ns, ms, s, min, h, d)
   * @return Value in the target unit
   */
  public double toUnit(String targetUnit) {
    long ns = getNanoseconds();
    targetUnit = targetUnit.toLowerCase();
    
    switch (targetUnit) {
      case "ns":
        return ns;
      case "ms":
        return ns / 1_000_000.0;
      case "s":
        return ns / 1_000_000_000.0;
      case "m":
      case "min":
        return ns / (60.0 * 1_000_000_000.0);
      case "h":
        return ns / (60.0 * 60.0 * 1_000_000_000.0);
      case "d":
        return ns / (24.0 * 60.0 * 60.0 * 1_000_000_000.0);
      default:
        // Default to milliseconds if unit is not recognized
        return ns / 1_000_000.0;
    }
  }

  @Override
  public Object value() {
    return nanoseconds;
  }

  @Override
  public TokenType type() {
    return TokenType.TIME_DURATION; // You'll need to add this to TokenType enum
  }

  @Override
  public JsonElement toJson() {
    JsonObject object = new JsonObject();
    object.addProperty("value", originalValue);
    object.addProperty("nanoseconds", nanoseconds);
    return object;
  }
}