This is a list of issues that are considered as bugs in the spongeian mixin
implementation. As of now they are forcefully reproduced in the micromixin
implementation, but in the future that should change.

henceforth, this list exists to facilitate migration to more acceptable standard
behaviour.

WARNING: Micromixin has a LOT more bugs than sponge. It needs a lot more
testing and maturity in order to come anywhere close. Remember that the
Spongeian implementation has a head start of over 8 years (Dec 14
2014 vs Feb 09 2023) - that is massive in software space

## PrintUtils#getExpectedCallbackSignature 

Opcodes.ACC_STATIC is ignored when creating the "expected" callback signature
for local capture printing

## PrintUtils#fastPrettyMethodName

Same as the above, albeit for different purposes

## CodeCopyUtil#copyHandler

It should be obvious to all that intentionally NOT remapping INVOKESTATIC
calls is not the proper call.
