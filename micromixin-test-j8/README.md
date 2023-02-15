## Micromixin-Test-J8

Any good library needs good tests.
However traditional test harnesses are a bit unsuited for bytecode modification
frameworks. For this reason the Micromixin framework is rolling it's own harness.

Furthermore, in order to be able to replicate a production environment and to
easily test the actual production environment, the test harness is built as a starloader
mod. The mod itself is however not dependent on a game.

As an added bonus the offical mixin implementation can also be tested for inconsistencies or
unexpected behaviour, without requiring any changes to the tests themselves. To test
with the official (i.e. Sponge's) Mixin implementation, older Starloader-Launcher releases should
be used. Most (if not even all) versions released before february 2023 should work for that
purpose. However, if you wish to test with the version you currently have on your remote
a more complicated setup is needed (i.e. you'd need to compile the launcher yourself with
your own Micromixin version).

That may appear complicated to you, and in that case I can only say: Don't worry - I'd be
surprised if you understood the purpose of all this!
In any case I'll be reviewing your changes before they get pulled and in most cases
that suffices to avoid pulling unexpected (or invalid) behaviour. For larger additions
I'll write my own tests and review them carefully. It's not something you have to necessarily
bother with yourself.

If pushing untested stuff is a bit too uneasy to you then feel free to make your own
tests beforehand. Since you appear to be interested in contributing to this cozy library,
it shouldn't be all that difficult to set up a minimalistic environment that is suited to test
your changes. I'd be happy if you were to share somehow it in that case as it would
drastically speed up testing time. That would also mean that contributions could get
accepted more quickly.
