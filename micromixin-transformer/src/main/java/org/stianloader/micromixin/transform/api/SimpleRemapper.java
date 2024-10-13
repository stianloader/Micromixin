package org.stianloader.micromixin.transform.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.stianloader.micromixin.transform.internal.util.MemberRenameMap;
import org.stianloader.micromixin.transform.internal.util.Objects;

public final class SimpleRemapper {
    private final MemberRenameMap fieldRenames = new MemberRenameMap();
    private final MemberRenameMap methodRenames = new MemberRenameMap();
    private final Map<String, String> oldToNewClassName = new HashMap<String, String>();

    /**
     * Remaps a class name.
     *
     * @param name The old (unmapped) class name
     * @return The new (remapped) class name
     */
    @NotNull
    public String getRemappedClassName(@NotNull String name) {
        String s = this.oldToNewClassName.get(name);
        if (s == null) {
            return name;
        }
        return s;
    }

    @Nullable
    public String getRemappedClassNameFast(@NotNull String name) {
        return this.oldToNewClassName.get(name);
    }

    /**
     * Remaps a field descriptor.
     *
     * @param fieldDesc The old (unmapped) field descriptor
     * @param sharedBuilder A shared cached string builder. The contents of the string builder are wiped and after the invocation the contents are undefined
     * @return The new (remapped) field descriptor. It <b>can</b> be identity identical to the "fieldDesc" if it didn't need to be altered
     */
    @SuppressWarnings("null")
    @NotNull
    public String getRemappedFieldDescriptor(@NotNull String fieldDesc, @NotNull StringBuilder sharedBuilder) {
        sharedBuilder.setLength(0);
        return this.remapSingleDesc(fieldDesc, sharedBuilder);
    }

    @NotNull
    public String getRemappedFieldName(@NotNull String ownerName, @NotNull String fieldName, @NotNull String fieldDesc) {
        return this.fieldRenames.optGet(ownerName, fieldDesc, fieldName);
    }

    /**
     * Remaps a method descriptor.
     *
     * @param methodDesc The old (unmapped) method descriptor
     * @param sharedBuilder A shared cached string builder. The contents of the string builder are wiped and after the invocation the contents are undefined
     * @return The new (remapped) method descriptor. It <b>can</b> be identity identical to the "methodDesc" if it didn't need to be altered
     */
    @NotNull
    public String getRemappedMethodDescriptor(@NotNull String methodDesc, @NotNull StringBuilder sharedBuilder) {
        sharedBuilder.setLength(0);
        if (!this.remapSignature(methodDesc, sharedBuilder)) {
            return methodDesc;
        }
        return sharedBuilder.toString();
    }

    @NotNull
    public String getRemappedMethodName(@NotNull String ownerName, @NotNull String methodName, @NotNull String methodDesc) {
        return this.methodRenames.optGet(ownerName, methodDesc, methodName);
    }

    private void remapAnnotation(AnnotationNode annotation, StringBuilder sharedStringBuilder) {
        String internalName = annotation.desc.substring(1, annotation.desc.length() - 1);
        String newInternalName = oldToNewClassName.get(internalName);
        if (newInternalName != null) {
            annotation.desc = 'L' + newInternalName + ';';
        }
        if (annotation.values != null) {
            int size = annotation.values.size();
            for (int i = 0; i < size; i++) {
                @SuppressWarnings("unused") // We are using the cast as a kind of built-in automatic unit test
                String bitvoid = (String) annotation.values.get(i++);
                this.remapAnnotationValue(annotation.values.get(i), i, annotation.values, sharedStringBuilder);
            }
        }
    }

    private void remapAnnotationValue(Object value, int index, List<Object> values, StringBuilder sharedStringBuilder) {
        if (value instanceof Type) {
            String type = ((Type) value).getDescriptor();
            sharedStringBuilder.setLength(0);
            if (this.remapSignature(type, sharedStringBuilder)) {
                values.set(index, Type.getType(sharedStringBuilder.toString()));
            }
        } else if (value instanceof String[]) {
            String[] enumvals = (String[]) value;
            String descriptor = enumvals[0];
            String name = Objects.requireNonNull(enumvals[1]);
            String internalName = descriptor.substring(1, descriptor.length() - 1);
            enumvals[1] = this.fieldRenames.optGet(internalName, descriptor, name);
            String newInternalName = this.oldToNewClassName.get(internalName);
            if (newInternalName != null) {
                enumvals[0] = 'L' + newInternalName + ';';
            }
        } else if (value instanceof AnnotationNode) {
            this.remapAnnotation((AnnotationNode) value, sharedStringBuilder);
        } else if (value instanceof List) {
            @SuppressWarnings("unchecked")
            List<Object> valueList = (List<Object>) value;
            int size = valueList.size();
            for (int i = 0; i < size; i++) {
                this.remapAnnotationValue(valueList.get(i), i, valueList, sharedStringBuilder);
            }
        } else {
            // Irrelevant
        }
    }

    public void remapBSMArg(final Object[] bsmArgs, final int index, final StringBuilder sharedStringBuilder) {
        Object bsmArg = bsmArgs[index];
        if (bsmArg instanceof Type) {
            Type type = (Type) bsmArg;
            sharedStringBuilder.setLength(0);

            if (type.getSort() == Type.METHOD) {
                if (this.remapSignature(type.getDescriptor(), sharedStringBuilder)) {
                    bsmArgs[index] = Type.getMethodType(sharedStringBuilder.toString());
                }
            } else if (type.getSort() == Type.OBJECT) {
                String oldVal = type.getInternalName();
                String remappedVal = this.remapInternalName(oldVal, sharedStringBuilder);
                if (oldVal != remappedVal) { // Instance comparison intended
                    bsmArgs[index] = Type.getObjectType(remappedVal);
                }
            } else {
                throw new IllegalArgumentException("Unexpected bsm arg Type sort. Sort = " + type.getSort() + "; type = " + type);
            }
        } else if (bsmArg instanceof Handle) {
            Handle handle = (Handle) bsmArg;
            String oldName = handle.getName();
            String hOwner = handle.getOwner();
            String newName = this.methodRenames.optGet(hOwner, handle.getDesc(), oldName);
            String newOwner = this.oldToNewClassName.get(hOwner);
            boolean modified = oldName != newName;
            if (newOwner != null) {
                hOwner = newOwner;
                modified = true;
            }
            String desc = handle.getDesc();
            sharedStringBuilder.setLength(0);
            if (remapSignature(desc, sharedStringBuilder)) {
                desc = sharedStringBuilder.toString();
                modified = true;
            }
            if (modified) {
                bsmArgs[index] = new Handle(handle.getTag(), hOwner, newName, desc, handle.isInterface());
            }
        } else if (bsmArg instanceof String) {
            // Do nothing. I'm kind of surprised that I built this method modular enough that this was a straightforward fix
        } else {
            throw new IllegalArgumentException("Unexpected bsm arg class at index " + index + " for " + Arrays.toString(bsmArgs) + ". Class is " + bsmArg.getClass().getName());
        }
    }

    public void remapClassName(@NotNull String oldName, @NotNull String newName) {
        this.oldToNewClassName.put(oldName, newName);
    }

    public void remapClassNames(Map<String, String> mappings) {
        this.oldToNewClassName.putAll(mappings);
    }

    public void remapField(@NotNull String owner, @NotNull String desc, @NotNull String oldName, @NotNull String newName) {
        this.fieldRenames.put(owner, desc, oldName, newName);
    }

    @NotNull
    public String remapInternalName(@NotNull String internalName, @NotNull StringBuilder sharedStringBuilder) {
        if (internalName.codePointAt(0) == '[') {
            return this.remapSingleDesc(internalName, sharedStringBuilder);
        } else {
            String remapped = this.oldToNewClassName.get(internalName);
            if (remapped != null) {
                return remapped;
            }
            return internalName;
        }
    }

    public void remapMethod(@NotNull String owner, @NotNull String desc, @NotNull String oldName, @NotNull String newName) {
        this.methodRenames.put(owner, desc, oldName, newName);
    }

    public boolean remapSignature(String signature, StringBuilder out) {
        return this.remapSignature(out, signature, 0, signature.length());
    }

    private boolean remapSignature(StringBuilder signatureOut, String signature, int start, int end) {
        if (start == end) {
            return false;
        }
        int type = signature.codePointAt(start++);
        switch (type) {
        case 'T':
            // generics type parameter
            // fall-through intended as they are similar enough in format compared to objects
        case 'L':
            // object
            // find the end of the internal name of the object
            int endObject = start;
            while(true) {
                // this will skip a character, but this is not interesting as class names have to be at least 1 character long
                int codepoint = signature.codePointAt(++endObject);
                if (codepoint == ';') {
                    String name = signature.substring(start, endObject);
                    String newName = this.oldToNewClassName.get(name);
                    boolean modified = false;
                    if (newName != null) {
                        name = newName;
                        modified = true;
                    }
                    signatureOut.appendCodePoint(type);
                    signatureOut.append(name);
                    signatureOut.append(';');
                    modified |= this.remapSignature(signatureOut, signature, ++endObject, end);
                    return modified;
                } else if (codepoint == '<') {
                    // generics - please no
                    // post scriptum: well, that was a bit easier than expected
                    int openingBrackets = 1;
                    int endGenerics = endObject;
                    while(true) {
                        codepoint = signature.codePointAt(++endGenerics);
                        if (codepoint == '>' ) {
                            if (--openingBrackets == 0) {
                                break;
                            }
                        } else if (codepoint == '<') {
                            openingBrackets++;
                        }
                    }
                    String name = signature.substring(start, endObject);
                    String newName = this.oldToNewClassName.get(name);
                    boolean modified = false;
                    if (newName != null) {
                        name = newName;
                        modified = true;
                    }
                    signatureOut.append('L');
                    signatureOut.append(name);
                    signatureOut.append('<');
                    modified |= this.remapSignature(signatureOut, signature, endObject + 1, endGenerics++);
                    signatureOut.append('>');
                    // apparently that can be rarely be a '.', don't ask when or why exactly this occours
                    signatureOut.appendCodePoint(signature.codePointAt(endGenerics));
                    modified |= this.remapSignature(signatureOut, signature, ++endGenerics, end);
                    return modified;
                }
            }
        case '+':
            // idk what this one does - but it appears that it works good just like it does right now
        case '*':
            // wildcard - this can also be read like a regular primitive
            // fall-through intended
        case '(':
        case ')':
            // apparently our method does not break even in these cases, so we will consider them raw primitives
        case '[':
            // array - fall through intended as in this case they behave the same
        default:
            // primitive
            signatureOut.appendCodePoint(type);
            return this.remapSignature(signatureOut, signature, start, end); // Did not modify the signature - but following operations could
        }
    }

    @NotNull
    public String remapSingleDesc(@NotNull String input, @NotNull StringBuilder sharedBuilder) {
        int indexofL = input.indexOf('L');
        if (indexofL == -1) {
            return input;
        }
        int length = input.length();
        String internalName = input.substring(indexofL + 1, length - 1);
        String newInternalName = this.oldToNewClassName.get(internalName);
        if (newInternalName == null) {
            return input;
        }
        sharedBuilder.setLength(indexofL + 1);
        sharedBuilder.setCharAt(indexofL, 'L');
        while(indexofL != 0) {
            sharedBuilder.setCharAt(--indexofL, '[');
        }
        sharedBuilder.append(newInternalName);
        sharedBuilder.append(';');
        return sharedBuilder.toString();
    }

    public void removeMethodRemap(@NotNull String owner, @NotNull String desc, @NotNull String name) {
        this.methodRenames.remove(owner, desc, name);
    }
}
