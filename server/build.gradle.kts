plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.google.devtools.ksp")
    id("org.graalvm.buildtools.native")
}

group = "com.johnturkson.sync.server"
version = "0.0.1"

dependencies {
    implementation(project(":common"))
    implementation("com.johnturkson.cdk:cdk-generator:0.0.3")
    ksp("com.johnturkson.cdk:cdk-generator:0.0.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
    implementation("com.johnturkson.security:security-tools:0.0.7")
    implementation("com.johnturkson.text:text-tools:0.0.4")
    implementation("org.slf4j:slf4j-simple:2.0.3")
    implementation("org.springframework.security:spring-security-crypto:5.7.3")
    implementation("software.amazon.awscdk:apigatewayv2-alpha:2.46.0-alpha.0")
    implementation("software.amazon.awscdk:apigatewayv2-integrations-alpha:2.46.0-alpha.0")
    implementation(platform("software.amazon.awssdk:bom:2.16.104"))
    implementation("software.amazon.awssdk:netty-nio-client")
    implementation("software.amazon.awssdk:dynamodb-enhanced") {
        exclude("software.amazon.awssdk", "netty-nio-client")
        exclude("software.amazon.awssdk", "apache-client")
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
    arg("hostedZone", "johnturkson.com")
    arg("routeSelectionExpression", "\$request.body.type")
    arg("HANDLER_LOCATION", "../server/build/lambda/image/${project.name}.zip")
}
