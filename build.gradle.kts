import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML

fun properties(key: String) = project.findProperty(key).toString()

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.0"
    id("org.jetbrains.intellij") version "1.15.0"
    id("org.jetbrains.changelog") version "2.1.2"
}

group = properties("pluginGroup")
version = properties("pluginVersion")

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.28")
    annotationProcessor("org.projectlombok:lombok:1.18.28")
    implementation("com.google.code.gson:gson:2.10.1")
}

intellij {
    version.set(properties("platformVersion"))
    type.set(properties("platformType"))
    downloadSources.set(properties("platformDownloadSources").toBoolean())
    updateSinceUntilBuild.set(true)
    val platformPlugins = properties("platformPlugins").split(',').map(String::trim).filter(String::isNotEmpty)
    plugins.set(
            platformPlugins + listOf("com.intellij.java")
    )
}

changelog {
    version.set(properties("pluginVersion"))
    groups.set(emptyList())
}

tasks {

    buildSearchableOptions {
        enabled = false
    }

    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    withType<org.jetbrains.intellij.tasks.RunIdeTask> {
        this.maxHeapSize = "4g"
    }

    patchPluginXml {
        version.set(properties("pluginVersion"))
        sinceBuild.set(properties("pluginSinceBuild"))
        untilBuild.set(properties("pluginUntilBuild"))
        pluginDescription.set(
                projectDir.resolve("README.md").readText().lines().run {
                    val start = "<!-- DESCRIPTION HEADER BEGIN -->"
                    val end = "<!-- DESCRIPTION HEADER END -->"
                    if (!containsAll(listOf(start, end))) {
                        throw GradleException("DESCRIPTION HEADER section not found in README.md:\n$start ... $end")
                    }
                    subList(indexOf(start) + 1, indexOf(end))
                }.joinToString("\n").run {
                    val header = markdownToHTML(this)
                    projectDir.resolve("docs/FOOTER.md").readText().lines().run {
                        val start = "<!-- DESCRIPTION FOOTER BEGIN -->"
                        val end = "<!-- DESCRIPTION FOOTER END -->"
                        if (!containsAll(listOf(start, end))) {
                            throw GradleException("DESCRIPTION FOOTER section not found in FOOTER.md:\n$start ... $end")
                        }
                        subList(indexOf(start) + 1, indexOf(end))
                    }.joinToString("\n").run {
                        header + markdownToHTML(this)

                    }
                }
        )
        changeNotes.set(changelog.renderItem(changelog.getLatest(), Changelog.OutputType.HTML))
    }

    runPluginVerifier {
        ideVersions.set(properties("pluginVerifierIdeVersions").split(",").toList())
    }

    test {
        useJUnitPlatform()
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
