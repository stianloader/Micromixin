# Micromixin

Micromixin is a lightweight reimplementation of Spongepowered's Mixin framework.

If you wish to talk about Micromixin, this project has a discord: https://discord.gg/CjnPMxsAX6

## Building

When providing source code freely on the internet I usually want to provide it in a
way that it can be compiled far into the future (well in more interconnected projects
it gets a bit more complicated but still not too much of an issue), however in order to
support Java 6 (this functionality was requested by someone interested in this project),
<b>one needs to compile with Java 11 at most. Building with newer JDKs won't work.</b>

## Why reimplement?

The astute among you may be painfully aware that reimplementing any large framework is
a very time-consuming process. However, with Mixin I believe that the pain of staying
with the official implementation would be stronger than attempting to reimplement it
in a decent fashion. <b>By no means is it a full reimplementation</b>, but we try to
keep compatibility whereever possible.

Painpoints of the official Mixin implementation that this implementation seeks to avoid:
 - Mixin's JPMS being an absolute hellscape
 - Mixin being very hard to use outside it's intended habitat (keyword: Modlauncher)
 - Mixin being reliant on outdated libraries
 - Service hell
 - Not being present on OSSRH. (As of now this is an afterthought, not actually done)
 - Dependency hell (Only org.json:json and objectweb's asm is needed under micromixin)
 - Integration with newer Java versions being confusing at best
 - Sponge's Mixins supports operand stack manipulation by declaring @Inject handlers
   as something other than void (see
   <https://discord.com/channels/142425412096491520/626802111455297538/1075864621589733387>
   ). Micromixin rejects that practice and will POP/POP2 such operands as that leads
   to unstable behaviour.
 - Mixin using Java 8 (Micromixin uses Java 6, making it usable for some more niche
   purposes)

## Modules

The Micromixin framework comes in three modules. "micromixin-annotations" includes
all the traditional mixin annotations that are implemented by Micromixin - nothing more.
"micromixin-transformer" includes the transformer - i.e. it is the heart of the project.
"micromixin-runtime" includes everything needed to run transformed classes (such as the
CallbackInfo classes).
"micromixin-test-j8" includes tests for Micromxin and is the least interesting part of
the project.

<b>Warning: In 90% of cases you'll want to bundle micromixin-transformer alongside
micromixin-runtime. Due to there being no strict dependencies between the classes,
the dependency is not resolved by default.</b>
micromixin-annotations should strictly be used for compilation only,
micromixin-transformer should strictly be used for runtime transformation only,
micromixin-runtime should most likely always be used.

## Supported features

 - `@Inject`
 - `@Shadow`
 - `@Overwrite`
 - `@Redirect`
 - `@ModifyReturnValue` (MixinExtras)

## Notable unsupported features

Note that we are steadily working towards adding new features and to stablish throughout
feature compatibility with the spongeian mixin implementation.

 - `@Coerce`
 - `@ModifyArgs`
 - `@ModifyVariable`
 - `@ModifyConstant`
 - `@Inject` usage in constructors before `super()` call
 - Regex support in `Inject.target`, `Redirect.target` and `At.target`
