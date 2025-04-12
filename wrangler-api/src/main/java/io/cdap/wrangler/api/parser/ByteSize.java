package io.cdap.wrangler.api.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Represents a token for byte size values with units (e.g., "10KB", "1.5MB").
 */
public class ByteSize implements Token {
  private final String originalValue;
  private final double value;
  private final String unit;
  private final long bytes;

  public ByteSize(String value) {
    this.originalValue = value;
    
    // Extract numeric value and unit from the token string
    String numericPart = value.replaceAll("[^0-9.]", "");
    String unitPart = value.replaceAll("[0-9.]", "");
    
    this.value = Double.parseDouble(numericPart);
    this.unit = unitPart.toUpperCase();
    this.bytes = convertToBytes(this.value, this.unit);
  }

  /**
   * Converts the value with unit to bytes.
   *
   * @param value Numeric value
   * @param unit Unit (B, KB, MB, GB, etc.)
   * @return Value converted to bytes
   */
  private long convertToBytes(double value, String unit) {
    switch (unit) {
      case "B":
        return (long) value;
      case "K":
      case "KB":
        return (long) (value * 1024);
      case "M":
      case "MB":
        return (long) (value * 1024 * 1024);
      case "G":
      case "GB":
        return (long) (value * 1024 * 1024 * 1024);
      case "T":
      case "TB":
        return (long) (value * 1024 * 1024 * 1024 * 1024);
      case "P":
      case "PB":
        return (long) (value * 1024 * 1024 * 1024 * 1024 * 1024);
      default:
        // Default to bytes if unit is not recognized
        return (long) value;
    }
  }

  /**
   * Gets the value in bytes.
   *
   * @return Value in bytes
   */
  public long getBytes() {
    return bytes;
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
   * @return Unit (B, KB, MB, etc.)
   */
  public String getUnit() {
    return unit;
  }

  /**
   * Convert the bytes to a specific unit.
   *
   * @param targetUnit Target unit (B, KB, MB, etc.)
   * @return Value in the target unit
   */
  public double toUnit(String targetUnit) {
    long bytes = getBytes();
    targetUnit = targetUnit.toUpperCase();
    
    switch (targetUnit) {
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
        // Default to bytes if unit is not recognized
        return bytes;
    }
  }

  @Override
  public Object value() {
    return bytes;
  }

  @Override
  public TokenType type() {
    return TokenType.BYTE_SIZE; // You'll need to add this to TokenType enum
  }

  @Override
  public JsonElement toJson() {
    JsonObject object = new JsonObject();
    object.addProperty("value", originalValue);
    object.addProperty("bytes", bytes);
    return object;
  }
}