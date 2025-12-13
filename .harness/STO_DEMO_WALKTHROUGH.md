# Harness STO Demo Walkthrough

## Demo Overview
This demo showcases Harness Security Testing Orchestration (STO) capabilities including:
- **OWASP Dependency Check** - Scans dependencies for known CVEs
- **SCA Vulnerability Scan** - Software Composition Analysis
- **OPA Governance Policies** - Automated security enforcement
- **AIDA Integration** - AI-powered remediation guidance

---

## Demo Scenario

### Part 1: Clean Main Branch (Baseline)
1. Run pipeline on `main` branch
2. Show clean security scan results
3. Build completes successfully
4. Demonstrate governance policies allowing clean code through

### Part 2: Vulnerable tsto Branch (Security Failure)
1. Switch to `tsto` branch containing vulnerable code
2. Run pipeline - security scans detect vulnerabilities
3. **Pipeline FAILS** due to governance policy enforcement

#### Vulnerabilities Introduced:

**CRITICAL (Will Block Pipeline):**
- **CVE-2021-44228 (Log4Shell)** - Log4j 2.14.1 dependency
- **CWE-89 (SQL Injection)** - PaymentService.getPaymentDetailsByQuery()
- **CWE-78 (Command Injection)** - PaymentService.generatePaymentReport()

**HIGH (Will Block Pipeline):**
- **CVE-2019-14540** - Jackson Databind 2.9.8 deserialization
- **CWE-502 (Insecure Deserialization)** - PaymentService.loadPaymentData()

**MEDIUM (Warning Only):**
- **CWE-327 (Weak Cryptography)** - MD5 and DES usage
- **CWE-798 (Hardcoded Credentials)** - DB_PASSWORD, API_KEY
- **CVE-2015-7501** - Commons Collections 3.2.1

### Part 3: AIDA Remediation Demo
1. Click on a CRITICAL vulnerability in STO dashboard
2. Open AIDA assistant
3. AIDA provides:
   - Root cause explanation
   - Step-by-step fix instructions
   - Code examples for remediation
   - Best practices guidance

### Part 4: Fix Critical Issues
1. Apply AIDA-suggested fixes for CRITICAL vulnerabilities
2. Update Log4j to 2.21.0+ (patched version)
3. Use PreparedStatement for SQL queries
4. Sanitize input for command execution

### Part 5: Skip Low Priority & Proceed
1. Show how to configure fail_on_severity
2. Change from `critical` to `high` or `medium`
3. Pipeline proceeds with warnings for lower severity issues
4. Demonstrate security report showing all findings

---

## Pipeline Configuration

### Security Stage (Runs First)
```yaml
- stage:
    name: Security Scan
    type: SecurityTests
    spec:
      execution:
        steps:
          - parallel:
              - step:
                  type: Owasp
                  name: OWASP Dependency Check
                  spec:
                    mode: orchestration
                    fail_on_severity: critical
              - step:
                  type: OsvScanner
                  name: SCA Vulnerability Scan
                  spec:
                    mode: orchestration
                    fail_on_severity: critical
```

### Build Stage (Runs After Security)
- Only executes if security scans pass
- Includes Test Intelligence and Cache Intelligence
- Parallel test execution with 3 workers

---

## OPA Governance Policy

Located at: `.harness/policies/security_scan_policy.rego`

**Enforcement Rules:**
- CRITICAL severity → **Pipeline BLOCKED**
- HIGH severity → **Pipeline BLOCKED**
- MEDIUM severity → **Warning shown** (continues)
- LOW/INFO severity → **Report only** (continues)

---

## Key Demo Talking Points

### 1. Shift-Left Security
> "Security scans run BEFORE build, catching vulnerabilities early in the pipeline"

### 2. Parallel Execution
> "OWASP and SCA scans run in parallel, maximizing efficiency without compromising security coverage"

### 3. Governance Enforcement
> "OPA policies automatically enforce security standards - no manual review needed for policy compliance"

### 4. AIDA Intelligence
> "AIDA doesn't just find problems - it provides actionable remediation guidance with code examples"

### 5. Flexible Severity Control
> "Configure which severities block vs. warn based on your organization's risk tolerance"

### 6. Full Visibility
> "All findings - blocked or allowed - appear in the security report for complete audit trail"

---

## AIDA Demo Script

When showing AIDA for a vulnerability:

1. **Click vulnerability** → "Let's see what AIDA can tell us about this Log4Shell vulnerability"

2. **Open AIDA panel** → "AIDA is Harness's AI assistant that understands security context"

3. **Show explanation** → "It explains that Log4j 2.14.1 allows remote code execution through JNDI lookup"

4. **Show fix** → "AIDA recommends upgrading to Log4j 2.21.0 or later, and shows the exact pom.xml change needed"

5. **Additional context** → "It also suggests adding JNDI lookup restrictions as defense in depth"

---

## Files Modified for Demo

| File | Changes | Severity |
|------|---------|----------|
| `pom.xml` | Added Log4j 2.14.1, Jackson 2.9.8, Commons Collections 3.2.1 | CRITICAL, HIGH, MEDIUM |
| `PaymentService.java` | Added SQL injection, Command injection, Insecure deserialization, Weak crypto, Hardcoded creds | CRITICAL, HIGH, MEDIUM |
| `.harness/tsto.yaml` | STO pipeline with parallel security scans | N/A |
| `.harness/policies/security_scan_policy.rego` | OPA governance policy | N/A |

---

## Quick Reset for Re-Demo

To reset and run demo again:
```bash
# Reset to main (clean)
git checkout main

# Or reset vulnerable branch
git checkout tsto
```
