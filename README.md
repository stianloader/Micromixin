# Micromixin

Micromixin is a lightweight reimplementation of Spongepowered's Mixin framework.

If you wish to talk about Micromixin or request that a feature gets implemented with
increased priority, you can join our discord: https://discord.gg/CjnPMxsAX6

## Building

When providing source code freely on the internet I usually want to provide it in a
way that it can be compiled far into the future (well in more interconnected projects
it gets a bit more complicated but still not too much of an issue), however in order to
support Java 6 (this functionality was requested by someone interested in this project),
<b>one needs to compile with Java 11 at most. Building with newer JDKs won't work.</b>

Otherwise, this project can be built like any other project via `./gradlew build`.

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
   purposes. However thanks to multi-release jars API consumers for Java 9+ will see
   little difference)

## Modules

The Micromixin framework comes in five modules.

 - "micromixin-annotations" includes
all the traditional mixin annotations that are implemented by Micromixin - nothing more.
 - "micromixin-transformer" includes the transformer - i.e. it is the heart of the project.
 - "micromixin-runtime" includes everything needed to run transformed classes (such as the
CallbackInfo classes).
 - "micromixin-test-j8" includes tests for Micromxin and is the least interesting part of
the project.

<b>Warning: In 90% of cases you'll want to bundle micromixin-transformer alongside
micromixin-runtime. Due to there being no strict dependencies between the classes,
the dependency is not resolved by default.</b>
micromixin-annotations should strictly be used for compilation only,
micromixin-transformer should strictly be used for runtime transformation only,
micromixin-runtime should most likely always be used.

## Maven

This project is available at https://stianloader.org/maven/ with following coordinates:
 - groupid: org.stianloader
 - artifactid:
  - micromixin-transformer
  - or micromixin-runtime
  - or micromixin-annotations

 Available versions are listed under
 https://stianloader.org/maven/org/stianloader/micromixin-runtime/

In the future, "stable" releases may be offered under OSSRH (maven central).

## Supported features

 - `@Inject` (Sponge)
 - `@Shadow` (Sponge)
 - `@Overwrite` (Sponge)
 - `@Redirect` (Sponge)
 - `@ModifyArg` (Sponge)
 - `@ModifyConstant` (Sponge)
 - `@ModifyReturnValue` (MixinExtras)

## Notable unsupported features

Note that we are steadily working towards adding new features and to establish throughout
feature compatibility with the spongeian mixin implementation.

 - Refmaps. This feature will not be implemented by myself in the forseeable future,
   if you absolutely need that feature: PR it. The underlying infrastructure probably
   already exists so it won't be a herculean task.
 - `@Coerce`
 - `@ModifyArgs`
 - `@ModifyVariable`
 - `@Inject` usage in constructors before `super()` call
 - Regex support in `Inject.method`, `Redirect.method`, `At.target` and similar

## Contributing

Even though it might sound surprising to most of you, writing the actual transformers is
often very trivial work. However, testing the compliance of implemented transformers is
a lot of work (although it often being rather dumb labour). As such writing test mixins
which allow to inspect the behaviour of sponge's mixin implementation is the best way of
guaranteeing that a feature gets implemented even if you can't wrap your head around
micromixin's codebase. These tests don't necessarily have to be PR'd to this repository
and can be written for a modloader other than SLL, but they should be mostly modloader
agnostic (that is: easily portable to SLL) as SLL is the only loader that I am aware of
which can make use of both sponge's mixin implementation and micromixin.

However, you can also indirectly contribute to micromixin by simply making use of it
and reporting any bugs you find along the way! (though please don't make use of micromixin
in gigantic projects and expect it to work fine)
