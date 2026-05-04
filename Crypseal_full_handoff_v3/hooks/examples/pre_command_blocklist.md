# Hook: PreCommand Blocklist

Event: BeforeCommand

Purpose: block dangerous shell patterns before approval or execution.

Patterns:
- rm -rf /
- rm -rf ~
- curl * | sh
- wget * | sh
- chmod 777 -R *
- cat ~/.ssh/*
- cat .env

Action:
- return DENY for blocked patterns
- return ASK for package/network/inline eval
- return ALLOW only for known safe patterns inside project root
