package com.llamalad7.mixinextras.sugar;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * The Cancellable annotation can be applied on arguments of a {@link Redirect} handler
 * to allow the possibility to cancel large quantities of logic within the injected method.
 * More specifically, this annotation may only be applied on a {@link CallbackInfo}
 * or {@link CallbackInfoReturnable} argument, which makes that argument behave similar
 * to as if the {@link CallbackInfo} was used in a traditional setting with {@link Inject}
 * and {@link Inject#cancellable()} set to <code>true</code>. Callback instances
 * are reused where applicable and created lazily.
 *
 * <h3>Callback IDs</h3>
 *
 * While {@link Inject} allows to set custom {@link CallbackInfo#getId() identifiers} for
 * the callback objects in the spongeian implementation, micromixin-transformer does not
 * allow the definition of custom callback IDs.
 * However, both micromixin-transformer and MixinExtras forbid the explicit definition of
 * these IDs. In the case of MixinExtras the resulting Id of the callback is left undefined
 * by the MixinExtras documentation, where as micromixin-transformer will use the name of
 * the method which is being targeted by the handler. As micromixin-transformer does not
 * support the usage of refmaps, the name of the identifier will thus be dependent on the
 * environment it is running under. This behaviour may cause differences between production
 * and development environments. As a rule of thumb, the identifier of the callback should
 * not be relied on for any logic except for logging and debugging.
 *
 * <h3>Permissible uses</h3>
 *
 * MixinExtras does not strongly define where the usage of this annotation is permissible
 * or not, however micromixin-transformer explicitly applies restrictions on where this
 * annotation can be used and where it can't. These restrictions are follows:
 *
 * <ul>
 *   <li>This annotation can only be applied on arguments of either type {@link CallbackInfo}
 *   or {@link CallbackInfoReturnable}.</li>
 *   <li>This annotation can only be applied where argument capture would otherwise be legal.
 *   In case of doubt, the annotated {@link CallbackInfo} should be appended to the end of the
 *   argument list of the handler method.</li>
 *   <li>This annotation may not be applied in conjunction with {@link Inject}.</li>
 *   <li>This annotation cannot be applied on non-injecting handlers - this means that it is
 *   illegal to use this annotation on {@link Unique}, {@link Overwrite}, or {@link Shadow}.</li>
 *   <li>As of time of writing, micromixin-transformer only permits use in conjunction with
 *   {@link Redirect}, but this restriction is likely to be lifted in future revisions of
 *   micromixin-transformer.</li>
 * </ul>
 *
 * <h3>Example usage</h3>
 *
 * Assume following target:
 * 
 * <blockquote><pre>
 *   private static int redirectStaticReturnable() {
 *       CancellableTest.counter1 = 1;
 *       CancellableTest.counter1 = new MutableInt(2).intValue();
 *       CancellableTest.counter1 = 3;
 *       return 0;
 *   }
 * </pre></blockquote>
 *
 * Execution of this method can be aborted after the redirected invocation of <code>intValue</code>
 * using following mixin handler:
 * <blockquote><pre>
 *  &#64;Redirect(at = &#64;At(value = "INVOKE", target = "intValue"), method = "redirectStaticReturnable")
 *  private static int handleRedirectStaticReturnable(&#64;NotNull MutableInt caller, &#64;Cancellable CallbackInfoReturnable&lt;Integer&gt; ci) {
 *      ci.setReturnValue(1);
 *      return 4;
 *  }
 * </pre></blockquote>
 *
 * This results in following code:
 *
 * <blockquote><pre>
 *  &#64;CommonCIInstance(index=1)
 *  private static void redirectStatic() {
 *      CallbackInfo callbackInfo = null;
 *      counter0 = 1;
 *      MutableInt mutableInt = new MutableInt(2);
 *      if (callbackInfo == null) {
 *          callbackInfo = new CallbackInfo("redirectStatic", true);
 *      }
 *      int i = CancellableTest.$handler$0$redirect$handleRedirectStatic(mutableInt, callbackInfo);
 *      if (callbackInfo.isCancelled()) {
 *          return;
 *      }
 *      counter0 = i;
 *      counter0 = 3;
 *  }
 * </pre></blockquote>
 *
 * Note that in this case the assignment to <code>counter0</code> will be cancelled, too.
 *
 * @since 0.6.3
 */
@Documented
@Retention(CLASS)
@Target(PARAMETER)
public @interface Cancellable { }
