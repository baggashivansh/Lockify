# Phase 3-7 Deep Dive Summary

## Phase 3 — Session (`phase3.session`)
**What:** Track devices, logout everywhere
**Scenario:** User sees 3 active sessions, revokes unknown device

## Phase 4 — Enterprise (`phase4.enterprise`)
**What:** Audit logs, rate limiting, MFA
**Scenario:** 11th login attempt in 1 min → 429 Too Many Requests

## Phase 5 — OAuth (`phase5.oauth`)
**What:** Google/GitHub login + account linking
**Scenario:** User logs in with Google → oauth_accounts links to existing email

## Phase 6 — ABAC (`phase6.authorization`)
**What:** Resource ownership + department rules
**Scenario:** User can only edit own document in same department

## Phase 7 — Hardening (`phase7.hardening`)
**What:** Token rotation, blacklist, fingerprint, adaptive MFA
**Scenario:** Stolen refresh token reused → entire token family revoked

See `docs/DEEP-DIVE/ATTACK-SCENARIOS.md` for full attack/defense matrix.
