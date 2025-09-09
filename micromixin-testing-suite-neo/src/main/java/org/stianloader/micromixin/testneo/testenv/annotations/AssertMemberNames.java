package org.stianloader.micromixin.testneo.testenv.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.BiPredicate;

import org.jetbrains.annotations.NotNull;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface AssertMemberNames {

    public enum AssertConstraint {
        CONTAINS(String::contains),
        IS(String::equals);

        public static boolean test(@NotNull String memberName, @NotNull AssertMemberName assertion) {
            return assertion.constraint().test(memberName, assertion.value(), assertion.negate());
        }

        @NotNull
        private BiPredicate<@NotNull String, @NotNull String> fn;

        private AssertConstraint(@NotNull BiPredicate<@NotNull String, @NotNull String> fn) {
            this.fn = fn;
        }

        public boolean test(@NotNull String memberName, @NotNull String witness, boolean negate) {
            return this.fn.test(memberName, witness) ^ negate;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.FIELD})
    @Repeatable(AssertMemberNames.class)
    public @interface AssertMemberName {
        @NotNull
        AssertConstraint constraint();
        boolean negate() default false;
        @NotNull
        String value();
    }

    @NotNull
    AssertMemberName @NotNull[] value();
}
