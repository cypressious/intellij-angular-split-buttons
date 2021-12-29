plugins {
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.intellij") version "1.2.0"
}

group = "org.example"
version = "0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    type.set("IU")
    plugins.set(listOf("JavaScriptLanguage", "sass"))
    version.set("2021.2.3")
}
tasks {
    patchPluginXml {
        changeNotes.set("""Initial Release""".trimIndent())
        version.set(project.version.toString())
        sinceBuild.set("212.0")
        untilBuild.set("999.*")
    }
}