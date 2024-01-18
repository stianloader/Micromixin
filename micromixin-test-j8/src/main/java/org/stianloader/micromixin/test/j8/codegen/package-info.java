/**
 * As it turns out writing tests by hand isn't fun. One way around this issue
 * is to only write the truly necessary tests - this is the approach a lot of
 * projects go with (if they even have tests). However it has the drawback
 * that it might not catch that single edge-case. So code generation it is
 * to generate a large amount of tests that might be rather similar to each
 * other but actually all slightly different.
 *
 * <p>These tests are engineered as a blanket solution and do not aim to reach a
 * specific degree of coverage.
 *
 * <p>They are only really here for the sake of being reusable later on.
 */
package org.stianloader.micromixin.test.j8.codegen;
