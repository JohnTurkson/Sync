plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.google.devtools.ksp")
}

group = "com.johnturkson.sync.common"
version = "0.0.1"

dependencies {
    implementation("com.johnturkson.cdk:cdk-generator:0.0.3")
    ksp("com.johnturkson.cdk:cdk-generator:0.0.3")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.20")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
    implementation(platform("software.amazon.awssdk:bom:2.17.294"))
    implementation("software.amazon.awssdk:dynamodb-enhanced") {
        exclude("software.amazon.awssdk", "apache-client")
        exclude("software.amazon.awssdk", "netty-nio-client")
    }
    compileOnly("software.amazon.awscdk:aws-cdk-lib:2.46.0")
}

kotlin {
    sourceSets {
        main {
            kotlin.srcDir("build/generated/ksp/main/kotlin")
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
        vendor.set(JvmVendorSpec.GRAAL_VM)
    }
}

ksp {
    arg("location", "$group.generated")
}
