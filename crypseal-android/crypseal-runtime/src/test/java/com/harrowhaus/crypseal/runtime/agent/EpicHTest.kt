package com.harrowhaus.crypseal.runtime.agent

import com.harrowhaus.crypseal.runtime.context.SkillsLoader
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.nio.file.Files

class EpicHTest {

    @Test
    fun testSkillsLoaderOverride() {
        val tempDir = Files.createTempDirectory("crypseal_test").toFile()
        val bundledDir = File(tempDir, "bundled")
        bundledDir.mkdirs()
        File(bundledDir, "base_skill.md").writeText("bundled_content")
        
        val projectRoot = File(tempDir, "project")
        projectRoot.mkdirs()
        val projectSkillsDir = File(projectRoot, ".crypseal/skills")
        projectSkillsDir.mkdirs()
        
        // Override the bundled skill
        File(projectSkillsDir, "base_skill.md").writeText("project_override_content")
        // Add a new project skill
        File(projectSkillsDir, "custom_skill.md").writeText("custom_content")
        
        val loader = SkillsLoader(bundledDir)
        val loaded = loader.loadSkills(projectRoot)
        
        assertEquals(2, loaded.size)
        val baseSkill = loaded.find { it.name == "base_skill" }
        assertEquals("project_override_content", baseSkill?.content)
        
        tempDir.deleteRecursively()
    }

    @Test
    fun testHookEngineBlocking() {
        val engine = HookEngine()
        
        val strictHook = object : PreCommandHook {
            override val name = "NoRmHook"
            override fun evaluate(command: String, args: Map<String, Any>): HookResult {
                val target = args["cmd"] as? String ?: ""
                if (target.contains("rm ")) {
                    return HookResult(false, "Deletion commands are hooked and blocked.")
                }
                return HookResult(true)
            }
        }
        
        engine.registerHook(strictHook)
        
        val safeResult = engine.evaluatePreCommand("run_command", mapOf("cmd" to "ls -la"))
        assertTrue(safeResult.allow)
        
        val blockResult = engine.evaluatePreCommand("run_command", mapOf("cmd" to "rm -rf /"))
        assertFalse(blockResult.allow)
        assertEquals("Deletion commands are hooked and blocked.", blockResult.blockReason)
    }
}
