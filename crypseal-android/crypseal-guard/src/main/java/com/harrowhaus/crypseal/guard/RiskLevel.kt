package com.harrowhaus.crypseal.guard

enum class RiskLevel {
    LOW_READ,
    LOW_STATUS,
    LOW_TEST,
    MEDIUM_EDIT,
    MEDIUM_PACKAGE_INSTALL,
    MEDIUM_NETWORK,
    HIGH_DELETE,
    HIGH_EXTERNAL_STORAGE,
    HIGH_SECRET_ACCESS,
    HIGH_INLINE_EVAL,
    BLOCKED_DESTRUCTIVE
}

enum class PolicyAction {
    ALLOW,
    ASK,
    DENY
}
