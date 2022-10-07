plugins {
    kotlin("jvm")
    application
}

group = "com.johnturkson.sync.infrastructure"
version = "0.0.1"

dependencies {
    implementation(project(":common"))
    implementation(project(":server"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.10")
    implementation("software.amazon.awscdk:aws-cdk-lib:2.20.0")
    implementation("software.amazon.awscdk:apigatewayv2-alpha:2.17.0-alpha.0")
    implementation("software.amazon.awscdk:apigatewayv2-integrations-alpha:2.17.0-alpha.0")
}

application {
    mainClass.set("com.johnturkson.sync.infrastructure.SyncAppKt")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
        vendor.set(JvmVendorSpec.GRAAL_VM)
    }
}
