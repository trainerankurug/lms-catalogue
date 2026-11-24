---
applyTo: "**/*"
---

For code-review tasks in this workspace, independently check that the
`lms-code-review` skill is available and that its reuse-review guidance is
reflected in the review. Before finalizing, verify that the reviewer examined
existing implementations and callers before recommending new logic, and that
any reuse finding cites concrete duplicated behavior or a bypassed project
boundary.

Include a concise **Reuse review** note in the review result: summarize what
existing implementation was considered and whether reuse concerns were found.
If the skill could not be loaded or its use cannot be verified, state that
clearly; do not claim compliance based only on this instruction. Do not create
style or comment-quality findings.
