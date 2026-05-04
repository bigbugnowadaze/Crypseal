# Copy Style Evaluation Rubrics

## Score every user-facing string 1–5

### Clarity
Can a tired mobile user understand it quickly?

### Specificity
Does it name the actual command, file, permission, or risk?

### Humanity
Does it sound like a competent human product, not a generated placeholder?

### Brevity
Is the default visible text short enough for a phone?

### Expandability
Can the user open details if they need the raw data?

### Trust
Does it avoid hiding risk or overstating certainty?

## Required copy patterns

### Approval card
```text
[Action]?
Why: [plain reason]
Scope: [path/resource]
Risk: [low/medium/high + reason]
```

### Error summary
```text
What failed: [specific]
Likely cause: [if supported]
Next safest step: [action]
```

### Diff summary
```text
Changed [file] to [purpose].
This affects [surface/behavior].
Verification: [test/lint/run result].
```

### Empty state
```text
No projects yet.
Create one, clone a repo, or connect a Termux folder.
```

## Copy reviewers

- HumanCopywriterAgent owns tone.
- ErrorUXCopyAgent owns failure states.
- PrivacyTrustAgent owns permission/risk language.
- DocumentationAgent owns long-form docs.
