import com.android.build.api.dsl.ApplicationExtension
import com.urdzik.convention.CoreVersion
import com.urdzik.convention.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

class AndroidApplicationComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.application")
            apply(plugin = "org.jetbrains.kotlin.plugin.compose")

            extensions.configure<ApplicationExtension> {
                defaultConfig.targetSdk = CoreVersion.targetSdk
                defaultConfig.versionName = CoreVersion.versionName
                configureAndroidCompose(this)
            }

        }
    }

}