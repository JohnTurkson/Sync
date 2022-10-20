plugins {
    kotlin("jvm")
    application
}

group = "com.johnturkson.sync.infrastructure"
version = "0.0.1"

dependencies {
    implementation(project(":common"))
    implementation(project(":server"))
    implementation("software.amazon.awscdk:aws-cdk-lib:2.46.0")
    implementation("software.amazon.awscdk:apigatewayv2-alpha:2.46.0-alpha.0")
    implementation("software.amazon.awscdk:apigatewayv2-integrations-alpha:2.46.0-alpha.0")
}

application {
    mainClass.set("com.johnturkson.sync.infrastructure.SyncAppKt")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
        vendor.set(JvmVendorSpec.GRAAL_VM)
    }
}
