package org.disco.easyb

import org.disco.easyb.core.exception.VerificationException

it "should fail if an exception is not thrown", {
    failed = false
    try {
        ensureThrows(RuntimeException.class) {
            // If we don't throw an exception the spec should fail
        }
        failed = true
    } catch (VerificationException expected) {
        // Success
    }
    if (failed) {
        fail("The absence of an exception didn't fail the ensureThrows check")
    }
}