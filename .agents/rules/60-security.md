# 60-SECURITY: SECURITY & CREDENTIAL DISCIPLINE

1. ZERO SECRETS IN REPOSITORY
   Never commit, print in plain text, or include in audit archives:
   - API tokens, passwords, or personal access tokens
   - Private keys, keystores, or certificates
   - User credentials or internal network configurations

2. AUDIT ARCHIVE CLEANLINESS
   When generating ZIP archives or diagnostic dumps, explicitly filter out `.env`, `local.properties`, `.idea/`, `.gradle/`, and IDE caches.
