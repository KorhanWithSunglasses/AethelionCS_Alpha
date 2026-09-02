# 70-CHANGE-CONTROL: CHANGE SCOPE & APPROVAL GATES

1. SCOPE BOUNDARIES
   Do not exceed the boundaries of the user request. A task specified as "research only" or "agent bootstrap only" must NOT modify application logic or bump versions.

2. ROLLBACK PLAN
   Every non-trivial architectural modification must have a documented rollback strategy.

3. MASS REWRITE PROHIBITION
   Refactoring an entire subsystem when a one-line bugfix suffices is prohibited unless explicitly requested by the user.
