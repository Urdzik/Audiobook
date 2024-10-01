import com.android.build.api.dsl.ApplicationExtension
import com.urdzik.convention.CoreVersion
import com.urdzik.convention.configureFlavors
import com.urdzik.convention.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")

            }

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = CoreVersion.targetSdk
                defaultConfig.versionName = CoreVersion.versionName
                configureFlavors(this)
                buildTypes {
                }
            }
        }
    }

}