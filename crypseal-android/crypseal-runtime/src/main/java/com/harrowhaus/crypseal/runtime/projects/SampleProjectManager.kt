package com.harrowhaus.crypseal.runtime.projects

import java.io.File

object SampleProjectManager {
    
    fun setupSampleProject(baseDir: File): File {
        val projectDir = File(baseDir, "M7Sample")
        if (!projectDir.exists()) {
            projectDir.mkdirs()
        }
        
        val mainPy = File(projectDir, "main.py")
        if (!mainPy.exists()) {
            mainPy.writeText("""
def greet(name):
    return f"Hello, {name}"

if __name__ == "__main__":
    print(greet("Crypseal"))
            """.trimIndent())
        }
        
        val dotCrypseal = File(projectDir, ".crypseal")
        if (!dotCrypseal.exists()) {
            dotCrypseal.mkdirs()
        }
        
        val agentMd = File(dotCrypseal, "AGENT.md")
        if (!agentMd.exists()) {
            agentMd.writeText("""
# Project: M7 Sample
This is a test project for verifying the M7 milestone.
The project contains a simple Python script 'main.py' that greets Crypseal.
            """.trimIndent())
        }
        
        return projectDir
    }
}
