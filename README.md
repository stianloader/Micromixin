# Micromixin

Micromixin is a leightweight reimplementation of Spongepowered's Mixin framework.

## Why reimplement?

The astute among you may be painfully aware that reimplementing any large framework is
a very time-consuming process. However, with Mixin I believe that the pain of staying
with the official implementation would be stronger than attempting to reimplement it
in a decent fashion. <b>By no means is it a full reimplementation</b>, but we try to
keep compatibility whereever possible.

Painpoints of the official Mixin implementation that this implementation seeks to avoid:
 - JPMS being an absolute hellscape
 - Mixin being very hard to use outside it's intended habitat (keyword: Modlauncher)
 - No reliance on outdated libraries
 - Service hell
 - Not being present on OSSRH. (As of now this is an afterthought, not actually done)
 - May break with so-called "ASM crashers"
 - Dependency hell (Only org.json:json and cafed00d is needed)
 - Integration with newer Java versions being confusing at best

Pain that would be induced by using this implementation instead of the official one:
 - No integration with OW2-ASM ClassNodes
 - Depends on Cafed00d, which is only present on jitpack. Also pretty unknown library
