plugins {
    id("com.android.application")
    `maven-publish`
    kotlin("android")
}

androidAppModule("com.criteo.pubsdk_android")

android {
    flavorDimensions("mode")
    productFlavors {
        create("memoryLeaksHunt") {
            dimension = "mode"
            versionNameSuffix = "-memoryLeaksHunt"
        }
    }
}

// Export APK for all build types (release, staging, debug)
addPublication("Apk") {
    groupId = "com.criteo.pubsdk_android"
    artifactId = "publisher-app"
    pom.packaging = "apk"

    android.applicationVariants.all {
        outputs.all {
            artifact(outputFile) {
                classifier = buildType.name
                builtBy(assembleProvider)
            }
        }
    }
}

dependencies {
    implementation(project(":publisher-sdk"))

    implementation(Deps.Square.Picasso.Picasso) {
        exclude(group = Deps.Android.Support.group)
    }

    // FIXME EE-1097 Adapters need SDK vX, but test app needs adapters vX, so when bumping version,
    //  one dependency should be cut off.
    implementation(Deps.Criteo.Mediation.MoPub("${sdkVersion()}+")) {
        exclude(group = Deps.Criteo.PublisherSdk.group)
    }

    implementation(Deps.Criteo.Mediation.AdMob("${sdkVersion()}+")) {
        exclude(group = Deps.Criteo.PublisherSdk.group)
    }

    implementation(Deps.Kotlin.Stdlib)
    implementation(Deps.Android.Support.AppCompatV7)
    implementation(Deps.Android.Support.Constraint.ConstraintLayout)
    implementation(Deps.Android.Support.Design)

    implementation(Deps.Google.AdMob) {
        exclude(group = Deps.Android.Support.group)
    }

    implementation(Deps.MoPub.Banner) {
        isTransitive = true
        exclude(group = Deps.Android.Support.group)
    }

    implementation(Deps.MoPub.Interstitial) {
        isTransitive = true
        exclude(group = Deps.Android.Support.group)
    }

    "memoryLeaksHuntImplementation"(Deps.Square.LeakCanary.LeakCanary)
}