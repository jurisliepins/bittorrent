package com.github.jurisliepins.value;

import com.github.jurisliepins.BException;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

public sealed interface BValue extends Comparable<BValue> permits BInteger, BByteString, BList, BDictionary {

    default Byte toByte() {
        return toShort().byteValue();
    }

    default Short toShort() {
        return toInteger().shortValue();
    }

    default Integer toInteger() {
        return toLong().intValue();
    }

    default Long toLong() {
        return toBInteger().value();
    }

    default BInteger toBInteger() {
        return switch (this) {
            case BInteger val -> val;
            default -> throw BValueExceptions.unexpectedBIntegerException();
        };
    }

    default String toString(Charset encoding) {
        return new String(toBytes(), encoding);
    }

    default byte[] toBytes() {
        return toBByteString().value();
    }

    default BByteString toBByteString() {
        return switch (this) {
            case BByteString val -> val;
            default -> throw BValueExceptions.unexpectedBByteStringException();
        };
    }

    default List<BValue> toList() {
        return toBList().value();
    }

    default BList toBList() {
        return switch (this) {
            case BList val -> val;
            default -> throw BValueExceptions.unexpectedBListException();
        };
    }

    default Map<BValue, BValue> toMap() {
        return toBDictionary().value();
    }

    default BDictionary toBDictionary() {
        return switch (this) {
            case BDictionary val -> val;
            default -> throw BValueExceptions.unexpectedBDictionaryException();
        };
    }

    class BValueExceptions {
        public static BException unexpectedBIntegerException() {
            return unexpectedBValueException(BValueType.BIntegerType);
        }

        public static BException unexpectedBByteStringException() {
            return unexpectedBValueException(BValueType.BByteStringType);
        }

        public static BException unexpectedBListException() {
            return unexpectedBValueException(BValueType.BListType);
        }

        public static BException unexpectedBDictionaryException() {
            return unexpectedBValueException(BValueType.BDictionaryType);
        }

        private static BException unexpectedBValueException(BValueType type) {
            return new BException("Unexpected BValue type - %s expected".formatted(type));
        }
    }
}