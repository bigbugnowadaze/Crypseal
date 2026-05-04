package com.harrowhaus.crypseal.guard

class CommandClassifier {

    private val denyPatterns = listOf(
        Regex("rm\\s+-rf\\s+/$"),
        Regex("rm\\s+-rf\\s+~$"),
        Regex("rm\\s+-rf\\s+\\*$"),
        Regex("curl\\s+.*\\|\\s*sh"),
        Regex("wget\\s+.*\\|\\s*sh"),
        Regex("chmod\\s+777\\s+-R"),
        Regex("chown\\s+-R"),
        Regex("cat\\s+.*\\.ssh/.*")
    )

    private val inlineEvalPatterns = listOf(
        Regex("^python\\s+-c"),
        Regex("^node\\s+-e"),
        Regex("^ruby\\s+-e"),
        Regex("^perl\\s+-e"),
        Regex("^php\\s+-r"),
        Regex("^lua\\s+-e"),
        Regex("^sh\\s+-c"),
        Regex("^bash\\s+-c")
    )

    private val allowAutoPatterns = listOf(
        Regex("^pwd$"),
        Regex("^ls.*"),
        Regex("^find\\s+\\..*"),
        Regex("^rg\\s+.*"),
        Regex("^git\\s+status$"),
        Regex("^git\\s+diff\\s+--stat$")
    )

    fun classifyCommand(command: String): Pair<PolicyAction, RiskLevel> {
        val trimmed = command.trim()

        // 1. Check deny lists
        for (pattern in denyPatterns) {
            if (pattern.containsMatchIn(trimmed)) {
                return Pair(PolicyAction.DENY, RiskLevel.BLOCKED_DESTRUCTIVE)
            }
        }

        // 2. Check inline evals
        for (pattern in inlineEvalPatterns) {
            if (pattern.containsMatchIn(trimmed)) {
                return Pair(PolicyAction.ASK, RiskLevel.HIGH_INLINE_EVAL)
            }
        }

        // 3. Check auto allows (Low Risk)
        for (pattern in allowAutoPatterns) {
            if (pattern.matches(trimmed)) {
                return Pair(PolicyAction.ALLOW, RiskLevel.LOW_READ)
            }
        }

        // Default to ASK for everything else
        return Pair(PolicyAction.ASK, RiskLevel.MEDIUM_EDIT)
    }
}
