# Agent Routing Table

| User/request type | Primary agent | Reviewers | Tool scope |
|---|---|---|---|
| Build new Android screen | AndroidPlatformAgent | UXArchitect, HumanCopywriter, TestAgent | fs, build, git |
| Add command execution feature | TermuxRuntimeAgent | SecurityAgent, ToolUseReviewer, ErrorUXCopyAgent | termux, fs, policy |
| Improve approval card UI | UXArchitect | HumanCopywriter, SecurityAgent, AccessibilityAgent | design, copy, policy |
| Write onboarding | OnboardingCopyAgent | UXArchitect, PrivacyTrustAgent | copy, design |
| Audit prior art | PriorArtLibrarian | ProductDirector, RoadmapStrategist | web/repo/docs |
| Fix failing build | BuildFixAgent | TestAgent, GitAgent | termux, fs, git |
| Add local model runtime | LocalModelRuntimeAgent | PerformanceThermalAgent, EvalHarnessAgent | model, benchmark |
| Create App Store listing | AppStoreCopyAgent | BrandVoiceAgent, ProductDirector | copy |
| Change permissions | PermissionPolicyAgent | SecurityAgent, UXArchitect, HumanCopywriter | policy, copy |
| Refactor repo | PlanAgent | RepoMapAgent, ImplementAgent, TestAgent | fs, search, test |
| Release build | ReleaseEngineerAgent | QAAgent, SecurityAgent, AppStoreCopyAgent | build, signing, docs |
