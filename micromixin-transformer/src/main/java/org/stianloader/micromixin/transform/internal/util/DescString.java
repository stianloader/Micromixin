package org.stianloader.micromixin.transform.internal.util;

import org.jetbrains.annotations.NotNull;

/**
 * Utility for dissecting a method descriptor string.
 */
public class DescString {

    private char[] asArray;
    private final String desc;
    private int startIndex = 0;

    public DescString(String desc) {
        int begin = 1; // Always starts with a parenthesis
        int end = desc.lastIndexOf(')');
        this.desc = desc.substring(begin, end);
    }

    public boolean hasNext() {
        return this.desc.length() != this.startIndex;
    }

    public int nextReferenceType() {
        int type = this.desc.codePointAt(this.startIndex);
        if (type == 'L') {
            // Object-type type
            this.startIndex = this.desc.indexOf(';', this.startIndex) + 1;
        } else if (type == '[') {
            // array-type type - things will go spicy
            if (this.asArray == null) {
                this.asArray = this.desc.toCharArray();
            }
            int typePosition = -1;
            for (int i = this.startIndex + 1; i < this.asArray.length; i++) {
                if (this.asArray[i] != '[') {
                    typePosition = i;
                    break;
                }
            }
            if (asArray[typePosition] == 'L') {
                this.startIndex = this.desc.indexOf(';', this.startIndex) + 1;
            } else {
                this.startIndex = ++typePosition;
            }
        } else {
            // Primitive-type type
            this.startIndex++; // Increment index by one, since the size of the type is exactly one
        }
        return type;
    }

    @NotNull
    public String nextType() {
        char type = this.desc.charAt(this.startIndex);
        if (type == 'L') {
            // Object-type type
            // the description ends with a semicolon here, which has to be kept
            int endPos = this.desc.indexOf(';', this.startIndex) + 1;
            String ret = this.desc.substring(this.startIndex, endPos);
            this.startIndex = endPos;
            return ret;
        } else if (type == '[') {
            // array-type type - things will go spicy
            if (this.asArray == null) {
                this.asArray = this.desc.toCharArray();
            }
            int typePosition = -1;
            for (int i = this.startIndex + 1; i < this.asArray.length; i++) {
                if (this.asArray[i] != '[') {
                    typePosition = i;
                    break;
                }
            }
            if (asArray[typePosition] == 'L') {
                int endPos = this.desc.indexOf(';', this.startIndex) + 1;
                String ret = this.desc.substring(this.startIndex, endPos);
                this.startIndex = endPos;
                return ret;
            } else {
                typePosition++;
                String ret = this.desc.substring(this.startIndex, typePosition);
                this.startIndex = typePosition;
                return ret;
            }
        } else {
            // Primitive-type type
            this.startIndex++; // Increment index by one, since the size of the type is exactly one
            return Character.toString(type);
        }
    }

    public void reset() {
        this.startIndex = 0;
    }
}
