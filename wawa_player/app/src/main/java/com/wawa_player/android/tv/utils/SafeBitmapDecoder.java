package com.wawa_player.android.tv.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPoolAdapter;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import com.bumptech.glide.util.ByteBufferUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Catches IOExceptions thrown by device-specific DRM image decoders (e.g. MediaTek
 * DcfDecoder) that fail when resetting a ByteBuffer-backed stream. On failure,
 * retries with a byte array that bypasses the stream reset requirement.
 */
public class SafeBitmapDecoder implements ResourceDecoder<ByteBuffer, Bitmap> {

    @Override
    public boolean handles(@NonNull ByteBuffer source, @NonNull Options options) {
        return true;
    }

    @Nullable
    @Override
    public Resource<Bitmap> decode(@NonNull ByteBuffer source, int width, int height, @NonNull Options options) throws IOException {
        // Materialize the full byte array up front so the fallback path always
        // has complete data even if the stream-based decode advances the
        // ByteBuffer's position before failing (e.g. DRM decoder reset errors).
        byte[] bytes = ByteBufferUtil.toBytes(source);
        source.rewind();
        try {
            InputStream stream = ByteBufferUtil.toStream(source);
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            if (bitmap == null) throw new IOException("BitmapFactory.decodeStream returned null");
            return BitmapResource.obtain(bitmap, new BitmapPoolAdapter());
        } catch (IOException e) {
            // DRM decoder failed (e.g. "Cannot reset to unset mark position").
            // Retry with byte array to bypass the stream-based DRM check.
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            if (bitmap == null) throw e;
            return BitmapResource.obtain(bitmap, new BitmapPoolAdapter());
        }
    }
}
