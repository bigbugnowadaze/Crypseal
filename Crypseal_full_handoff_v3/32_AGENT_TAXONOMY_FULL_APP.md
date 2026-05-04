# Full Agent Taxonomy — Not Just Coding Agents

Crypseal must be a full product team inside an Android app, not a single coder with shell access. The agent roster must cover engineering, product, design, writing, quality, safety, and release.

## Principle

Do not route every task to one general model persona. Use specialist subagents with separate context windows, scoped tools, and explicit deliverables. This matches public Claude Code subagent behavior, where subagents are specialists with descriptions, separate context, and optional tool scopes; it also follows the OpenHands/SWE-agent lesson that agent-computer interfaces and role-specific workflows matter as much as the model itself.

## Required subagent families

### 1. Direction and product
- ProductDirectorAgent
- RoadmapStrategistAgent
- PriorArtLibrarianAgent
- ResearchScoutAgent
- AcceptanceCriteriaAgent

### 2. Engineering
- ExploreAgent
- PlanAgent
- ImplementAgent
- TestAgent
- GitAgent
- BuildFixAgent
- DependencyAgent
- AndroidPlatformAgent
- TermuxRuntimeAgent
- LocalModelRuntimeAgent
- RepoMapAgent

### 3. Design and interaction
- UXArchitectAgent
- InteractionDesignerAgent
- VisualDesignAgent
- MobileErgonomicsAgent
- DesignCriticAgent
- AccessibilityAgent
- MotionAndStateAgent

### 4. Human language and copy
- HumanCopywriterAgent
- BrandVoiceAgent
- OnboardingCopyAgent
- ErrorUXCopyAgent
- ReleaseNotesAgent
- DocumentationAgent
- AppStoreCopyAgent

### 5. Safety and trust
- SecurityAgent
- PromptInjectionDefenseAgent
- PermissionPolicyAgent
- PrivacyTrustAgent
- ToolUseReviewerAgent
- ReasoningSupervisorAgent

### 6. Evaluation and release
- QAAgent
- EvalHarnessAgent
- PerformanceThermalAgent
- ReleaseEngineerAgent
- RegressionTriageAgent

## Routing rule

The main orchestrator chooses one primary agent and up to three reviewers. Example:

- Building a feature: ProductDirector -> PlanAgent -> ImplementAgent -> TestAgent -> DesignCritic -> HumanCopywriter.
- Redesigning onboarding: UXArchitect -> OnboardingCopyAgent -> AccessibilityAgent -> DesignCritic.
- Changing command execution: TermuxRuntimeAgent -> SecurityAgent -> PermissionPolicyAgent -> ToolUseReviewer.
- Improving local model behavior: LocalModelRuntimeAgent -> ReasoningSupervisorAgent -> EvalHarnessAgent.

## Non-negotiable

Every user-visible feature must pass at least one engineering review, one UX review, and one copy/trust review before it is considered shippable.
