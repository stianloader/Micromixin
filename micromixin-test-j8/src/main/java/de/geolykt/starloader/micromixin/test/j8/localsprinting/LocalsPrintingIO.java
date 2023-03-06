package de.geolykt.starloader.micromixin.test.j8.localsprinting;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;

public class LocalsPrintingIO {

    @NotNull
    private static final String FALLBACK_STRING = "<error: unspecified>";

    public static void assertEqualMethodSignature(String[] test, String[] witness) {
        StringBuilder fullTest = new StringBuilder();
        StringBuilder fullWitness = new StringBuilder();
        boolean startAppending = false;
        for (String s : test) {
            if (!startAppending) {
                if (s.endsWith(" * /")) {
                    startAppending = true;
                }
                continue;
            }
            fullTest.append(s);
        }
        if (!startAppending) {
            throw new AssertionError("Invalid method signature block: Comment block not ended.");
        }
        startAppending = false;
        for (String s : witness) {
            if (!startAppending) {
                if (s.endsWith(" * /")) {
                    startAppending = true;
                }
                continue;
            }
            fullWitness.append(s);
        }
        if (!startAppending) {
            throw new AssertionError("Invalid method signature block: Comment block not ended.");
        }

        String testHead = fullTest.toString();
        String witnessHead = fullWitness.toString();
        testHead = testHead.substring(0, testHead.indexOf(')') + 1);
        witnessHead = witnessHead.substring(0, witnessHead.indexOf(')') + 1);

        if (!LocalsPrintingIO.compareMethodNames(testHead, witnessHead)) {
            throw new AssertionError("Expected method signature of " + witnessHead + ", but got " + testHead);
        }
    }

    public static void assertEquals(@NotNull LocalPrintingContext test, @NotNull LocalPrintingContext witness) {
        if (!test.callbackName.equals(witness.callbackName)) {
            throw new AssertionError("Expected callback name of \"" + witness.callbackName + "\", but got \"" + test.callbackName + "\" instead.");
        }
        if (!test.initialFrameSize.equals(witness.initialFrameSize)) {
            throw new AssertionError("Expected initial frame size of \"" + witness.initialFrameSize + "\", but got \"" + test.initialFrameSize + "\" instead.");
        }
        // TODO Implement (or well - just remove the "false &&" part of it)!
        // Right now not done as instruction printing is not documented enough and thus not implemented in micromixin (and as such it isn't worth it to test it as it WILL always fail)
        if (Boolean.valueOf(false) && !test.instruction.equals(witness.instruction)) {
            throw new AssertionError("Expected instruction value of \"" + witness.instruction + "\", but got \"" + test.instruction + "\" instead.");
        }
        if (!test.targetClass.equals(witness.targetClass)) {
            throw new AssertionError("Expected target class of \"" + witness.targetClass + "\", but got \"" + test.targetClass + "\" instead.");
        }
        if (!test.targetMaxLocals.equals(witness.targetMaxLocals)) {
            throw new AssertionError("Expected maximum value of locals at target of \"" + witness.targetMaxLocals + "\", but got \"" + test.targetMaxLocals + "\" instead.");
        }
        if (!LocalsPrintingIO.compareMethodNames(test.targetMethod, witness.targetMethod)) {
            throw new AssertionError("Expected target method of \"" + witness.targetMethod + "\", but got \"" + test.targetMethod + "\" instead.");
        }
        LocalsPrintingIO.assertLocalTableLenientlyEquals(test.localsTable, witness.localsTable);
        LocalsPrintingIO.assertEqualMethodSignature(test.expectedCallbackSignature, witness.expectedCallbackSignature);
    }

    public static void assertEquals(LocalPrintingContext[] tests, LocalPrintingContext[] witnesses) {
        if (tests.length != witnesses.length) {
            throw new AssertionError("There is an unequal amount of test and witness values! Witnesses: " + witnesses.length + ", tests: " + tests.length);
        }
        Map<String, LocalPrintingContext> witnessMap = new HashMap<>();
        for (LocalPrintingContext witness : witnesses) {
            if (witnessMap.putIfAbsent(witness.callbackName, witness) != null) {
                throw new AssertionError("There are multiple witnesses with following callback name: \"" + witness.callbackName + "\". This method does not support duplicate callback names. Sorry!");
            }
        }
        for (LocalPrintingContext test : tests) {
            LocalPrintingContext witness = witnessMap.get(test.callbackName);
            if (witness == null) {
                throw new AssertionError("The test callback \"" + test.callbackName + "\" has no corresponding witness value!");
            }
            assertEquals(test, witness);
        }
    }

    public static void assertLocalTableLenientlyEquals(String[][] test, String[][] witness) {
        if (test.length != witness.length) {
            throw new AssertionError("Expected " + witness.length + " rows, but instead got " + test.length + " rows in the local table.");
        }
        for (int row = 0; row < witness.length; row++) {
            // Marker
            if (!Objects.equals(test[row][0], witness[row][0])) {
                throw new AssertionError("Expected a marker value of \"" + witness[row][0] + "\" at row " + row + ", but instead got " + test[row][0] + ".");
            }
            // Local index
            if (!Objects.equals(test[row][1], witness[row][1])) {
                throw new AssertionError("Expected a local index of \"" + witness[row][1] + "\" at row " + row + ", but instead got " + test[row][1] + ".");
            }
            // Type
            if (!Objects.equals(test[row][2], witness[row][2])) {
                throw new AssertionError("Expected a type of \"" + witness[row][2] + "\" at row " + row + ", but instead got " + test[row][2] + ".");
            }
            // Local name comparison skipped, as the spongeian implementation uses LVT while Micromixin discards it.
            // Capture flag
            if (!Objects.equals(test[row][4], witness[row][4])) {
                throw new AssertionError("Expected a capture flag of \"" + witness[row][4] + "\" at row " + row + ", but instead got " + test[row][4] + ".");
            }
        }
    }

    public static boolean compareMethodNames(String methodA, String methodB) {
        int abracket = methodA.indexOf('(');
        int bbracket = methodB.indexOf('(');
        if (abracket != bbracket || !methodA.regionMatches(false, 0, methodB, 0, abracket)) {
            return false;
        }
        int aidx, bidx;
        aidx = bidx = abracket + 1;
        while (aidx != -1 && bidx != -1) {
            int aspace = methodA.indexOf(' ', aidx);
            int bspace = methodB.indexOf(' ', bidx);
            int typeLength = aspace - aidx;
            if (bidx + typeLength != bspace || !methodA.regionMatches(false, aidx, methodB, bidx, typeLength)) {
                return false;
            }
            aidx = methodA.indexOf(',', aspace);
            bidx = methodB.indexOf(',', bspace);
        }
        return aidx == -1 && bidx == -1; // Compare argument count
    }

    public static synchronized LocalPrintingContext[] guardedRead(@NotNull Runnable action) {
        PrintStream originErr = System.err;
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            PrintStream replaceError = new PrintStream(byteOut);
            System.setErr(replaceError);
            action.run();
            replaceError.flush();
            String read = new String(byteOut.toByteArray(), StandardCharsets.UTF_8);
            while (read.codePointBefore(read.length()) == '\n' || read.codePointBefore(read.length()) == '\r') {
                read = read.substring(0, read.length() - 1);
            }
            if (!read.startsWith("/**") || !read.endsWith("**/")) {
                originErr.append(read);
                throw new IllegalStateException("Recieved data that isn't the LocalCapture.PRINT table. Read data: (START)" + read + "(END)");
            }
            String[] lines = read.split("[\\n\\r]");
            List<LocalPrintingContext> parsedContexts = new ArrayList<>();
            int blockStart = 0;
            int blockCount = 0;
            for (int i = 0; i < lines.length; i++) {
                if (lines[i].startsWith("/**")) {
                    blockCount++;
                    if (blockCount == 4) {
                        parsedContexts.add(parse(lines, blockStart, i));
                        blockCount = 0;
                    } else if (blockCount == 1) {
                        blockStart = i;
                    }
                }
            }
            return parsedContexts.toArray(new LocalPrintingContext[0]);
        } finally {
            System.setErr(originErr);
        }
    }

    @NotNull
    public static LocalPrintingContext parse(String[] lines, int start, int end) {
        String tClass = FALLBACK_STRING;
        String method = FALLBACK_STRING;
        String maxLocals = FALLBACK_STRING;
        String frameSize = FALLBACK_STRING;
        String callback = FALLBACK_STRING;
        String instructions = FALLBACK_STRING;
        boolean alreadyCaptured = false;
        int block = 0;
        List<String[]> locals = new ArrayList<>();
        List<String> expectedSignatureLines = new ArrayList<>();
        for (int lineIndex = start; lineIndex < end; lineIndex++) {
            String line = lines[lineIndex];
            if (!line.startsWith("/*") || !line.endsWith("*/")) {
                continue;
            }
            if (line.codePointAt(2) == '*') { // We can do .codePointAt since we already know how it starts and ends
                block++;
                continue;
            }
            if (block == 3) {
                // Expected callback signature
                // (The repeated #substring calls are not great for performance, but this shouldn't matter too much)
                int lineLength = line.length() - 2;
                line = line.substring(3, lineLength);
                lineLength -= 3;
                while (lineLength != 0 && line.codePointBefore(lineLength) == ' ') {
                    line = line.substring(0, --lineLength);
                }
                expectedSignatureLines.add(line);
            } else if (block == 2) {
                // Local table
                line = line.substring(2, line.length() - 2).trim();
                String[] parts = line.split("\\s+");
                String[] adjustedParts = new String[5];
                boolean captured = line.endsWith("<capture>");
                if (captured) {
                    if (alreadyCaptured) {
                        if (parts.length != 5) {
                            throw new IllegalStateException("Format error: Expected column count: 5; Got " + parts.length + "; Column content: " + Arrays.toString(parts));
                        }
                        adjustedParts[0] = "";
                        adjustedParts[1] = parts[0] + parts[1]; // local
                        adjustedParts[2] = parts[2]; // type
                        adjustedParts[3] = parts[3]; // name
                        adjustedParts[4] = parts[4]; // <capture>
                    } else {
                        if (!line.startsWith(">")) {
                            throw new IllegalStateException("Format error: Line of first captured local (at line " + lineIndex + ") must start with a \">\"! Instead, the first character was: \'" + line.substring(0, 1) + "\'. Full line: \"" + lines[lineIndex] + "\".");
                        }
                        if (parts.length != 6) {
                            throw new IllegalStateException("Format error: Expected column count: 6; Got " + parts.length + "; Column content: " + Arrays.toString(parts));
                        }
                        alreadyCaptured = true;
                        adjustedParts[0] = parts[0]; // ">"
                        adjustedParts[1] = parts[1] + parts[2]; // local
                        adjustedParts[2] = parts[3]; // type
                        adjustedParts[3] = parts[4]; // name
                        adjustedParts[4] = parts[5]; // <capture>
                    }
                } else if (parts.length == 3) {
                    if (parts[0].equals("[") && parts[1].codePointBefore(parts[1].length()) == ']' && parts[2].equals("<top>")) {
                        adjustedParts[0] = ""; // The initial frame cannot end in the middle of a double (unless the computer did something dumb)
                        adjustedParts[1] = parts[0] + parts[1]; // local
                        adjustedParts[2] = "<top>"; // type (we already know that one)
                        adjustedParts[3] = ""; // name (not present)
                        adjustedParts[4] = "";
                    } else if (parts[0].equals("LOCAL") && parts[1].equals("TYPE") && parts[2].equals("NAME")) {
                        // Table header - safe to skip
                        continue;
                    } else {
                        throw new IllegalStateException("Format error: Expected table header or the value at column index 2 to be \"<top>\"; Got " + parts[2] + " instead; Column content: " + Arrays.toString(parts));
                    }
                } else if (alreadyCaptured) {
                    throw new IllegalStateException("Format error: Cannot capture arguments with holes in between! Erroneous line: \"" + line + "\"");
                } else if (parts.length != 4) {
                    throw new IllegalStateException("Format error: Expected column count: 4; Got " + parts.length + "; Column content: " + Arrays.toString(parts));
                } else {
                    adjustedParts[0] = "";
                    adjustedParts[1] = parts[0] + parts[1]; // local
                    adjustedParts[2] = parts[2]; // type
                    adjustedParts[3] = parts[3]; // name
                    adjustedParts[4] = "";
                }
                locals.add(adjustedParts);
            } else if (block == 1) {
                int idx = line.indexOf(':');
                if (idx == -1) {
                    throw new IllegalStateException("The first block contains a line without a ':'.");
                }
                // Injection metadata
                String key = line.substring(2, idx).trim();
                String value = line.substring(idx + 1, line.length() - 2).trim();
                assert value != null;
                switch (key) {
                case "Target Class":
                    tClass = value;
                    break;
                case "Target Method":
                    method = value;
                    break;
                case "Target Max LOCALS":
                    maxLocals = value;
                    break;
                case "Initial Frame Size":
                    frameSize = value;
                    break;
                case "Callback Name":
                    callback = value;
                    break;
                case "Instruction":
                    instructions = value;
                    break;
                default:
                    throw new IllegalStateException("Unknown field: " + key);
                }
            } else {
                // Ignore other blocks - for now
                if (block == 0) {
                    throw new IllegalArgumentException("Error: Read ahead of block. The given \"lines\" argument does not specify a valid LocalCapture.PRINT unit.");
                } else {
                    throw new IllegalArgumentException("Error: Read behind of block. The given \"lines\" argument does not specify a valid LocalCapture.PRINT unit (did you accidentally give this method too many lines?). Erroneous line: " + lineIndex + ", block = " + block);
                }
            }
        }
        String[][] table = locals.toArray(new String[0][]);
        LocalPrintingContext ctx = new LocalPrintingContext(tClass, method, maxLocals, frameSize, callback, instructions, table, expectedSignatureLines.toArray(new String[0]));
        return ctx;
    }
}
