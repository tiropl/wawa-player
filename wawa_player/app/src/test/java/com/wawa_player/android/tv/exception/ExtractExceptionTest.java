package com.wawa_player.android.tv.exception;

import com.google.common.truth.Truth;

import org.junit.Test;

public class ExtractExceptionTest {

    @Test
    public void with_message() {
        ExtractException e = new ExtractException("fail");
        Truth.assertThat(e.getMessage()).isEqualTo("fail");
        Truth.assertThat(e).isInstanceOf(java.util.concurrent.ExecutionException.class);
    }

    @Test
    public void with_null_message() {
        ExtractException e = new ExtractException(null);
        Truth.assertThat(e.getMessage()).isNull();
    }
}
