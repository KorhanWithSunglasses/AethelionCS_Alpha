# 40-DEBUGGING: FORENSIC DEBUGGING PROTOCOL

1. REPRODUCE BEFORE FIXING
   Never attempt a code change without first reproducing the failure in a controlled, observable manner.

2. ISOLATE THE EXACT FAILING LAYER
   Determine whether the issue resides in:
   - Network / HTTP Transport
   - HTML / DOM Parsing
   - Model / State Mapping
   - Resolver / Extractor Logic
   - Packaging / Classloading (DEX/APK)
   - Target Application Player (ExoPlayer/Media3)

3. CAPTURE COMPLETE FAILURE TRACES
   Always log:
   - Full exception class name
   - Exception message
   - Invoking parameters
   - Stack trace
   Never swallow exceptions with empty catch blocks.

4. MINIMAL SURGICAL FIX
   Fix only the root cause. Avoid incidental refactoring or stylistic edits in the same changeset.
