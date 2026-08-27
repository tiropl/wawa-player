package com.wawa_player.android.tv.setting;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PasswordLockTest {
    @Test public void validatesLengthBoundaries() {
        assertFalse(PasswordLock.isValid(null));
        assertFalse(PasswordLock.isValid("12"));
        assertTrue(PasswordLock.isValid("123"));
        assertTrue(PasswordLock.isValid("12345678"));
        assertFalse(PasswordLock.isValid("123456789"));
    }
}
