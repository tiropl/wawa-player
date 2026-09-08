package com.wawa_player.android.tv.setting;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = com.wawa_player.android.tv.App.class, sdk = 36)
public class PasswordLockTest {
    @Test public void validatesLengthBoundaries() {
        assertFalse(PasswordLock.isValid(null));
        assertFalse(PasswordLock.isValid(""));
        assertFalse(PasswordLock.isValid("12"));
        assertTrue(PasswordLock.isValid("123"));
        assertTrue(PasswordLock.isValid("12345678"));
        assertFalse(PasswordLock.isValid("123456789"));
    }

    @Test public void setVerifyAndClearPasswordState() {
        PasswordLock.clear();
        assertFalse(PasswordLock.isSet());
        assertFalse(PasswordLock.verify(null));
        assertFalse(PasswordLock.verify(""));
        PasswordLock.setPassword("secret");
        assertTrue(PasswordLock.isSet());
        assertTrue(PasswordLock.verify("secret"));
        assertFalse(PasswordLock.verify("wrong"));
        PasswordLock.setPassword("changed");
        assertTrue(PasswordLock.verify("changed"));
        assertFalse(PasswordLock.verify("secret"));
        PasswordLock.clear();
        assertFalse(PasswordLock.isSet());
        assertFalse(PasswordLock.verify("changed"));
    }
}
