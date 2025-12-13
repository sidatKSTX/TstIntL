package securityTests

import future.keywords.in
import future.keywords.if

# ============================================================
# Harness STO Governance Policy - Security Scan Enforcement
# ============================================================
# This policy enforces security scanning requirements:
# 1. Blocks pipeline on Critical vulnerabilities (must fix)
# 2. Blocks pipeline on High vulnerabilities (must fix)
# 3. Warns on Medium vulnerabilities (shows in report)
# 4. Allows Low/Info vulnerabilities (shows in report only)
# ============================================================

# Define severity levels that will BLOCK the pipeline
deny_list := fill_defaults([
  {"severity": {"value": "Critical", "operator": "=="}},
  {"severity": {"value": "High", "operator": "=="}}
])

# Helper function to fill default values
fill_defaults(list) = result {
  result := [item |
    item := list[_]
  ]
}

# Deny rule for Critical vulnerabilities
deny[msg] {
  issue := input.securityTests.issues[_]
  issue.severity == "Critical"
  msg := sprintf("BLOCKED: Critical vulnerability found - %s (CVE: %s). Use AIDA for remediation guidance.", [issue.title, issue.referenceId])
}

# Deny rule for High vulnerabilities
deny[msg] {
  issue := input.securityTests.issues[_]
  issue.severity == "High"
  msg := sprintf("BLOCKED: High severity vulnerability found - %s (CVE: %s). Use AIDA for remediation guidance.", [issue.title, issue.referenceId])
}

# Warning rule for Medium vulnerabilities (doesn't block, but shows warning)
warn[msg] {
  issue := input.securityTests.issues[_]
  issue.severity == "Medium"
  msg := sprintf("WARNING: Medium severity vulnerability - %s. Review recommended.", [issue.title])
}
