package com.harrowhaus.crypseal.runtime.context

import java.io.File

data class Skill(
    val name: String,
    val description: String,
    val content: String
)

class SkillsLoader(private val bundledSkillsPath: File) {

    fun loadSkills(projectRoot: File): List<Skill> {
        val loaded = mutableMapOf<String, Skill>()
        
        // 1. Load bundled/default skills
        if (bundledSkillsPath.exists() && bundledSkillsPath.isDirectory) {
            bundledSkillsPath.listFiles()?.forEach { file ->
                if (file.isFile && file.extension == "md") {
                    loaded[file.nameWithoutExtension] = Skill(
                        name = file.nameWithoutExtension,
                        description = "Bundled skill",
                        content = file.readText()
                    )
                }
            }
        }

        // 2. Override with project-specific skills
        val projectSkillsDir = File(projectRoot, ".crypseal/skills")
        if (projectSkillsDir.exists() && projectSkillsDir.isDirectory) {
            projectSkillsDir.listFiles()?.forEach { file ->
                if (file.isFile && file.extension == "md") {
                    loaded[file.nameWithoutExtension] = Skill(
                        name = file.nameWithoutExtension,
                        description = "Project override skill",
                        content = file.readText()
                    )
                }
            }
        }

        return loaded.values.toList()
    }
}
