package org.stianloader.micromixin.testneo;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.ModuleLayer.Controller;
import java.lang.invoke.MethodHandles;
import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.stianloader.picoresolve.DependencyLayer;
import org.stianloader.picoresolve.DependencyLayer.DependencyEdge;
import org.stianloader.picoresolve.DependencyLayer.DependencyLayerElement;
import org.stianloader.picoresolve.GAV;
import org.stianloader.picoresolve.MavenResolver;
import org.stianloader.picoresolve.Scope;
import org.stianloader.picoresolve.exclusion.Exclusion;
import org.stianloader.picoresolve.exclusion.ExclusionContainer;
import org.stianloader.picoresolve.exclusion.ExclusionContainer.ExclusionMode;
import org.stianloader.picoresolve.repo.RepositoryAttachedValue;
import org.stianloader.picoresolve.repo.URIMavenRepository;
import org.stianloader.picoresolve.version.MavenVersion;
import org.stianloader.picoresolve.version.VersionRange;

public class MicromixinTestingSuiteNeo {
    @NotNull
    private static final GAV VIRTUAL_ROOT_GAV = new GAV("", "", MavenVersion.parse(""));

    @NotNull
    private static List<@NotNull URL> getEnvironmentURLs() {
        return Arrays.asList(Objects.requireNonNull(MicromixinTestingSuiteNeo.class.getProtectionDomain().getCodeSource().getLocation()));
    }

    public static void main(String[] args) {
        Path configPath = Paths.get(args.length == 0 ? "micromixin-test-config-eclipse.json" : args[0]);
        if (Files.notExists(configPath)) {
            throw new IllegalStateException("Cannot find test suite configuration file: '" + configPath + "'");
        }

        JSONObject jsonRoot;
        try {
            jsonRoot = new JSONObject(Files.readString(configPath, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to read json cinfoguration file", e);
        }

        for (Object var10001 : jsonRoot.getJSONArray("runConfigurations")) {
            if (!(var10001 instanceof JSONObject)) {
                throw new IllegalStateException("Invalid element in 'runConfigurations'");
            }

            JSONObject runConfiguration = (JSONObject) var10001;

            MavenResolver resolver = new MavenResolver(Path.of(".maven"));

            if (runConfiguration.getBoolean("alsoFetchLocally")) {
                URI mavenLocal = Path.of(System.getProperty("user.home"), ".m2", "repository").toUri();
                resolver.addRepository(new URIMavenRepository("mavenLocal", mavenLocal));
            }

            for (Object var10002 : runConfiguration.getJSONArray("repositories")) {
                if (!(var10002 instanceof JSONObject)) {
                    throw new IllegalStateException("Invalid element in 'repositories'");
                }

                JSONObject repository = (JSONObject) var10002;

                String id = repository.getString("id");
                String uri = repository.getString("uri");

                if (id == null) {
                    throw new IllegalStateException("Repository without id");
                }
                if (uri == null) {
                    throw new IllegalStateException("Repository without uri");
                }

                resolver.addRepository(new URIMavenRepository(id, URI.create(uri)));
            }

            List<DependencyEdge> edges = new ArrayList<>();

            for (Object var10002 : runConfiguration.getJSONArray("artifacts")) {
                if (!(var10002 instanceof JSONObject)) {
                    throw new IllegalStateException("Invalid element in 'artifacts'");
                }

                JSONObject artifacts = (JSONObject) var10002;

                String groupId = artifacts.getString("groupId");
                String artifactId = artifacts.getString("artifactId");
                String version = artifacts.getString("version");
                String type = artifacts.optString("type", null);
                JSONArray excludes = artifacts.optJSONArray("excludes");

                if (groupId == null) {
                    throw new IllegalStateException("Artifact without groupId");
                }
                if (artifactId == null) {
                    throw new IllegalStateException("Artifact without artifactId");
                }
                if (version == null) {
                    throw new IllegalStateException("Artifact without version");
                }
                if (type == null) {
                    type = "jar";
                }

                ExclusionContainer<Exclusion> exclusions;
                if (excludes == null) {
                    exclusions = ExclusionContainer.empty();
                } else {
                    exclusions = new ExclusionContainer<>(ExclusionMode.ANY);
                    for (Object var10003 : excludes) {
                        if (!(var10003 instanceof String)) {
                            throw new IllegalStateException("'excludes' contains element not instanceof String.");
                        }
                        String exclusion = (String) var10003;
                        int splitIndex = exclusion.indexOf(':');
                        if (splitIndex < 0) {
                            throw new IllegalStateException("Exclusions must have the format 'groupId:artifactId'");
                        }
                        exclusions.addChild(new Exclusion(exclusion.substring(0, splitIndex), exclusion.substring(splitIndex + 1)));
                    }
                }

                edges.add(new DependencyEdge(groupId, artifactId, null, type, VersionRange.parse(version), Scope.RUNTIME, exclusions));
            }

            List<CompletableFuture<RepositoryAttachedValue<Path>>> tasks = new ArrayList<>();
            DependencyLayer layer;
            for (layer = MicromixinTestingSuiteNeo.resolveAllChildren(resolver, edges); layer != null; layer = layer.getChild()) {
                for (DependencyLayerElement element : layer.elements) {
                    tasks.add(resolver.download(element.gav, element.classifier, element.type, ForkJoinPool.commonPool()));
                }
            }

            /*tasks.forEach(cf -> {
                cf.thenAccept(v -> {
                    MavenRepository repo = v.getRepository();
                    if (repo == null) {
                        System.out.println("Fetched " + v.getValue() + " from <unknown>");
                    } else {
                        System.out.println("Fetched " + v.getValue() + " from " + repo.getRepositoryId());
                    }
                });
            });*/

            List<URI> artifactURIs = new ArrayList<>(); // Converted to URLs for the classloader
            List<Path> artifactPaths = new ArrayList<>(); // For the module finder

            if (runConfiguration.has("alsoInclude")) {
                for (Object var10002 : runConfiguration.getJSONArray("alsoInclude")) {
                    if (!(var10002 instanceof String)) {
                        throw new IllegalStateException("'alsoInclude' contains element not instanceof String.");
                    }
                    Path alsoIncludePath = Paths.get((String) var10002);
                    if (Files.notExists(alsoIncludePath)) {
                        throw new IllegalStateException("'alsoInclude' references element that does not exist (" + alsoIncludePath + ").");
                    }
                    artifactURIs.add(alsoIncludePath.toUri());
                    artifactPaths.add(alsoIncludePath);
                }
            }

            CompletableFuture.allOf(tasks.toArray(new CompletableFuture<?>[0])).join();

            for (CompletableFuture<RepositoryAttachedValue<Path>> cf : tasks) {
                Path p = cf.join().getValue();
                artifactPaths.add(p);
                artifactURIs.add(p.toUri());
            }

            URL[] artifactURLs = new URL[artifactURIs.size()];

            for (int i = artifactURLs.length; i-- != 0;) {
                try {
                    artifactURLs[i] = artifactURIs.get(i).toURL();
                } catch (MalformedURLException e) {
                    throw new IllegalStateException("Cannot convert URI to URL for classloading process: " + artifactURIs.get(i).toASCIIString(), e);
                }
            }

            System.out.println("=".repeat(80));

            List<@NotNull String> rootModules = new ArrayList<>();
            for (Object var10002 : runConfiguration.getJSONArray("rootModules")) {
                if (!(var10002 instanceof String)) {
                    throw new IllegalStateException("'rootModules' contains element not instanceof String.");
                }
                rootModules.add((String) var10002);
            }

            if (runConfiguration.has("setProperties")) {
                JSONObject properties = runConfiguration.getJSONObject("setProperties");
                properties.toMap().forEach((k, v) -> {
                    System.setProperty(k, v.toString());
                });
            }

            try {
                ModuleFinder earlyFinder = ModuleFinder.of();
                ModuleFinder lateFinder = ModuleFinder.of(artifactPaths.toArray(new Path[artifactPaths.size()]));
                List<String> modules = new ArrayList<>();
                lateFinder.findAll().forEach(ref -> {
                    modules.add(ref.descriptor().name());
                });
                System.out.println("Following modules present: " + new TreeSet<>(modules));

                Configuration moduleConfig = ModuleLayer.boot().configuration().resolve(earlyFinder, lateFinder, rootModules);

                Controller controller = ModuleLayer.defineModulesWithOneLoader(moduleConfig, Arrays.asList(ModuleLayer.boot()), ClassLoader.getPlatformClassLoader());
                ModuleLayer moduleLayer = controller.layer();

                moduleLayer.modules().forEach(module -> {
                    modules.remove(module.getName());
                });

                if (!modules.isEmpty()) {
                    System.err.println("Following modules not initialized (you might want to define them as root modules): " + modules);
                }

                JSONArray testEnvironmentURLArray = new JSONArray(MicromixinTestingSuiteNeo.getEnvironmentURLs());
                String testEnvironmentURLs = testEnvironmentURLArray.toString(0);
                String testModEnvironmentURLs = new JSONArray(Collections.singleton(testEnvironmentURLArray)).toString(0);
                System.setProperty("de.geolykt.starloader.launcher.CLILauncher.mainClass", "org.stianloader.micromixin.testneo.testenv.MicromixinTestNeo");
                System.setProperty("de.geolykt.starloader.launcher.IDELauncher.bootURLs", testEnvironmentURLs);
                System.setProperty("de.geolykt.starloader.launcher.IDELauncher.modURLs", testModEnvironmentURLs);
                Class<?> clazz = moduleLayer.findLoader("de.geolykt.starloader.launcher").loadClass("de.geolykt.starloader.launcher.IDELauncher");

                System.out.println("'" + clazz.getName() + "' is loaded by '" + clazz.getClassLoader().getName() + "' and is of module '" + clazz.getModule().getName() + "'");
                System.out.println("Class URL (as per protection domain): " + clazz.getProtectionDomain().getCodeSource().getLocation());

                Method m = clazz.getMethod("main", String[].class);
                m.setAccessible(true);
                try {
                    MethodHandles.lookup().unreflect(m).invokeExact(args);
                } catch (Throwable e) {
                    throw new IllegalStateException("An unknown error occured while running the SLL testing environment", e);
                }
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Reflection error whilst starting SLL testing environment.", e);
            }
        }
    }

    @NotNull
    private static final DependencyLayer resolveAllChildren(@NotNull MavenResolver resolver, @NotNull List<@NotNull DependencyEdge> edges) {
        List<DependencyLayerElement> elements = new ArrayList<>();
        elements.add(new DependencyLayerElement(MicromixinTestingSuiteNeo.VIRTUAL_ROOT_GAV, null, null, ExclusionContainer.empty(), edges));
        DependencyLayer layer = new DependencyLayer(null, elements);
        resolver.resolveAllChildren(layer, ForkJoinPool.commonPool()).join();
        return Objects.requireNonNull(layer.getChild());
    }
}
