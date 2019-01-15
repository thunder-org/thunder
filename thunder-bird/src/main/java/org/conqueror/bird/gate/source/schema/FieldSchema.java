package org.conqueror.bird.gate.source.schema;

import org.conqueror.bird.data.BirdData;
import org.conqueror.bird.exceptions.schema.SchemaException;
import org.joda.time.DateTime;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public abstract class FieldSchema implements BirdData {

    protected static final Object FILTERED_OUT_VALUE = new Object();

    private JSONObject values;

    private final String srcFieldName;
    private final String indexFieldName;
    private final int fieldNumber;

    public FieldSchema(String srcFieldName, String indexFieldName, int fieldNumber) {
        this.srcFieldName = srcFieldName;
        this.indexFieldName = indexFieldName;
        this.fieldNumber = fieldNumber;
    }

    public String getSrcFieldName() {
        return srcFieldName;
    }

    public String getIndexFieldName() {
        return indexFieldName;
    }

    public int getFieldNumber() {
        return fieldNumber;
    }

    public boolean isIndexingField() {
        return indexFieldName != null;
    }

    public static boolean isFilteredOutValue(Object value) {
        return (value != null) && value.equals(FieldSchema.FILTERED_OUT_VALUE);
    }

    public abstract Object processedValue(Map<String, Object> source) throws SchemaException;

    public JSONObject getValues() {
        return values;
    }

    public FieldSchema setValues(JSONObject values) {
        this.values = values;
        return this;
    }

    public static String toStringValue(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            return (String) value;
        }

        return value.toString();
    }

    public static String[] toStringArrayValue(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            return new String[]{(String) value};
        } else if (value instanceof JSONArray) {
            String[] values = new String[((JSONArray) value).size()];
            int num = 0;
            for (Object elmt : ((JSONArray) value)) {
                values[num++] = toStringValue(elmt);
            }
            return values;
        }

        return null;
    }

    public static Integer toIntegerValue(Object value) {
        try {
            if (value == null) {
                return null;
            } else if (value instanceof String) {
                return Integer.parseInt(((String) value).replaceAll(",", ""));
            } else if (value instanceof Long) {
                return ((Long) value).intValue();
            } else if (value instanceof Integer) {
                return (Integer) value;
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    public static Integer[] toIntegerArrayValue(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            return new Integer[]{Integer.parseInt(((String) value).replaceAll(",", ""))};
        } else if (value instanceof JSONArray) {
            Integer[] values = new Integer[((JSONArray) value).size()];
            int num = 0;
            for (Object elmt : ((JSONArray) value)) {
                values[num++] = toIntegerValue(elmt);
            }
            return values;
        }

        return null;
    }

    public static Long toLongValue(Object value) {
        try {
            if (value == null) {
                return null;
            } else if (value instanceof String) {
                return Long.parseLong(((String) value).replaceAll(",", ""));
            } else if (value instanceof Integer) {
                return (long) value;
            } else if (value instanceof Long) {
                return (Long) value;
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    public static Float toFloatValue(Object value) {
        try {
            if (value == null) {
                return null;
            } else if (value instanceof String) {
                return Float.parseFloat(((String) value).replaceAll(",", ""));
            } else if (value instanceof Integer) {
                return ((Integer) value).floatValue();
            } else if (value instanceof Long) {
                return ((Long) value).floatValue();
            } else if (value instanceof Float) {
                return (Float) value;
            } else if (value instanceof Double) {
                return ((Double) value).floatValue();
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    public static Double toDoubleValue(Object value) {
        try {
            if (value == null) {
                return null;
            } else if (value instanceof String) {
                return Double.parseDouble(((String) value).replaceAll(",", ""));
            } else if (value instanceof Integer) {
                return ((Integer) value).doubleValue();
            } else if (value instanceof Long) {
                return ((Long) value).doubleValue();
            } else if (value instanceof Float) {
                return ((Float) value).doubleValue();
            } else if (value instanceof Double) {
                return (Double) value;
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    public static Long[] toLongArrayValue(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            return new Long[]{Long.parseLong(((String) value).replaceAll(",", ""))};
        } else if (value instanceof JSONArray) {
            Long[] values = new Long[((JSONArray) value).size()];
            int num = 0;
            for (Object elmt : ((JSONArray) value)) {
                values[num++] = toLongValue(elmt);
            }
            return values;
        }

        return null;
    }

    public static Float[] toFloatArrayValue(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            return new Float[]{Float.parseFloat(((String) value).replaceAll(",", ""))};
        } else if (value instanceof JSONArray) {
            Float[] values = new Float[((JSONArray) value).size()];
            int num = 0;
            for (Object elmt : ((JSONArray) value)) {
                values[num++] = toFloatValue(elmt);
            }
            return values;
        }

        return null;
    }

    public static Double[] toDoubleArrayValue(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            return new Double[]{Double.parseDouble(((String) value).replaceAll(",", ""))};
        } else if (value instanceof JSONArray) {
            Double[] values = new Double[((JSONArray) value).size()];
            int num = 0;
            for (Object elmt : ((JSONArray) value)) {
                values[num++] = toDoubleValue(elmt);
            }
            return values;
        }

        return null;
    }

    public static DateTime toDateTimeValue(Object value, String format) throws SchemaException {
        return toDateTimeValueWithFormatter(value, DateTimeFormat.forPattern(format).withLocale(Locale.ENGLISH));
    }

    public static DateTime toDateTimeValueWithFormatter(Object value, DateTimeFormatter formatter) throws SchemaException {
        try {
            if (value == null) {
                return null;
            } else if (value instanceof DateTime) {
                return (DateTime) value;
            } else if (value instanceof Date) {
                return new DateTime(((Date) value).getTime());
            } else if (value instanceof String) {
                return formatter.parseDateTime((String) value);
            }
        } catch (Exception e) {
            throw new SchemaException("failed to parse date : " + e.getMessage(), e.getCause());
        }

        return null;
    }

    public static DateTime toDateTimeValue(Object value, String[] formats) throws SchemaException {
        DateTimeFormatter[] formatters = new DateTimeFormatter[formats.length];
        int num = 0;
        for (String format : formats) {
            formatters[num++] = DateTimeFormat.forPattern(format).withLocale(Locale.ENGLISH);
        }
        return toDateTimeValueWithFormatter(value, formatters);
    }

    public static DateTime toDateTimeValueWithFormatter(Object value, DateTimeFormatter[] formatters) throws SchemaException {
        try {
            if (value == null) {
                return null;
            } else if (value instanceof DateTime) {
                return (DateTime) value;
            } else if (value instanceof Date) {
                return new DateTime(((Date) value).getTime());
            } else if (value instanceof String) {
                for (DateTimeFormatter formatter : formatters) {
                    try {
                        return formatter.parseDateTime((String) value);
                    } catch (IllegalFieldValueException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            throw new SchemaException("failed to parse date : " + e.getMessage(), e.getCause());
        }

        return null;
    }

    public static Boolean toBooleanValue(Object value) {
        try {
            if (value == null) {
                return null;
            } else if (value instanceof Boolean) {
                return (Boolean) value;
            } else if (value instanceof String) {
                return ((String) value).equalsIgnoreCase("true") ? true
                    : ((String) value).equalsIgnoreCase("false") ? false : null;
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    public static Boolean startsWith(Object value, String startValue) {
        return (value != null) && toStringValue(value).startsWith(startValue);
    }

    private static final String IPV4_REGEX = "^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$";
    private static final Pattern IPV4_REGEX_PATTERN = Pattern.compile(IPV4_REGEX);

    public static String toIPStringValue(Object value) {
        if (value instanceof String) {
            Matcher matcher = IPV4_REGEX_PATTERN.matcher((String) value);
            if (matcher.matches()) {
                int count = matcher.groupCount();
                String[] groups = new String[count];
                for (int i = 0; i < count; i++) {
                    groups[i] = matcher.group(i + 1);
                }

                for (int i = 0; i <= 3; i++) {
                    String ipSegment = groups[i];
                    if (ipSegment == null || ipSegment.length() <= 0) return null;
                    try {
                        int number = Integer.parseInt(ipSegment);
                        if (number > 255) return null;
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }

                return (String) value;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return "FieldSchema{" +
            "srcFieldName='" + srcFieldName + '\'' +
            ", indexFieldName='" + indexFieldName + '\'' +
            ", fieldNumber=" + fieldNumber +
            '}';
    }

}
