/**
 * Copyright 2014 JogAmp Community. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY JogAmp Community ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JogAmp Community OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of JogAmp Community.
 */
package com.jogamp.common.nio;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

import org.junit.Assert;
import org.junit.Test;

import com.jogamp.junit.util.JunitTracer;

import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

/**
 * Testing {@link MappedByteBufferInputStream} and {@link MappedByteBufferOutputStream}
 * direct stream to stream copy via mapped buffers.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestByteBufferCopyStream extends JunitTracer {

    static void testImpl(final String srcFileName, final long size,
                         final MappedByteBufferInputStream.CacheMode srcCacheMode, final int srcSliceShift,
                         final String dstFileName,
                         final MappedByteBufferInputStream.CacheMode dstCacheMode, final int dstSliceShift ) throws IOException {
        final Runtime runtime = Runtime.getRuntime();
        final long[] usedMem0 = { 0 };
        final long[] freeMem0 = { 0 };
        final long[] usedMem1 = { 0 };
        final long[] freeMem1 = { 0 };
        final String prefix = "test "+String.format(TestByteBufferInputStream.PrintPrecision+" MiB", size/TestByteBufferInputStream.MIB);
        TestByteBufferInputStream.dumpMem(prefix+" before", runtime, -1, -1, usedMem0, freeMem0 );

        final File srcFile = new File(srcFileName);
        srcFile.delete();
        srcFile.createNewFile();
        srcFile.deleteOnExit();

        final RandomAccessFile input;
        {
            final RandomAccessFile _input = new RandomAccessFile(srcFile, "rw");
            _input.setLength(size);
            _input.close();
            input = new RandomAccessFile(srcFile, "r");
        }
        final MappedByteBufferInputStream mis = new MappedByteBufferInputStream(input.getChannel(),
                                                                                FileChannel.MapMode.READ_ONLY,
                                                                                srcCacheMode,
                                                                                srcSliceShift);
        Assert.assertEquals(size, input.length());
        Assert.assertEquals(size, mis.length());
        Assert.assertEquals(0, mis.position());
        Assert.assertEquals(size, mis.remaining());

        final File dstFile = new File(dstFileName);
        dstFile.delete();
        dstFile.createNewFile();
        dstFile.deleteOnExit();
        final RandomAccessFile output = new RandomAccessFile(dstFile, "rw");
        final MappedByteBufferInputStream.FileResizeOp szOp = new MappedByteBufferInputStream.FileResizeOp() {
            @Override
            public void setLength(final long newSize) throws IOException {
                output.setLength(newSize);
            }
        };
        final MappedByteBufferOutputStream mos = new MappedByteBufferOutputStream(output.getChannel(),
                                                                                  FileChannel.MapMode.READ_WRITE,
                                                                                  dstCacheMode,
                                                                                  srcSliceShift, szOp);
        Assert.assertEquals(0, output.length());
        Assert.assertEquals(0, mos.length());
        Assert.assertEquals(0, mos.position());
        Assert.assertEquals(0, mos.remaining());

        mos.write(mis, mis.remaining());

        Assert.assertEquals(size, input.length());
        Assert.assertEquals(size, output.length());
        Assert.assertEquals(size, mis.length());
        Assert.assertEquals(size, mos.length());
        Assert.assertEquals(size, mis.position());
        Assert.assertEquals(size, mos.position());
        Assert.assertEquals(0, mis.remaining());
        Assert.assertEquals(0, mos.remaining());

        mos.close();
        mis.close();
        input.close();
        output.close();
        srcFile.delete();
        dstFile.delete();
        TestByteBufferInputStream.dumpMem(prefix+" after ", runtime, usedMem0[0], freeMem0[0], usedMem1, freeMem1 );
        System.gc();
        try {
            Thread.sleep(500);
        } catch (final InterruptedException e) { }
        TestByteBufferInputStream.dumpMem(prefix+" gc'ed ", runtime, usedMem0[0], freeMem0[0], usedMem1, freeMem1 );
    }

    @Test
    public void test00() throws IOException {
        final int srcSliceShift = MappedByteBufferInputStream.DEFAULT_SLICE_SHIFT;
        final int dstSliceShift = MappedByteBufferInputStream.DEFAULT_SLICE_SHIFT;
        final long size = 3L * ( 1L << 30 ); // 3 GiB
        testImpl("./testIn.bin", size, MappedByteBufferInputStream.CacheMode.FLUSH_PRE_HARD, srcSliceShift,
                 "./testOut.bin", MappedByteBufferInputStream.CacheMode.FLUSH_PRE_HARD, dstSliceShift );
    }

    @Test
    public void test01() throws IOException {
        final int srcSliceShift = MappedByteBufferInputStream.DEFAULT_SLICE_SHIFT;
        final int dstSliceShift = MappedByteBufferInputStream.DEFAULT_SLICE_SHIFT;
        final long size = 3L * ( 1L << 30 ); // 3 GiB
        testImpl("./testIn.bin", size, MappedByteBufferInputStream.CacheMode.FLUSH_PRE_SOFT, srcSliceShift,
                 "./testOut.bin", MappedByteBufferInputStream.CacheMode.FLUSH_PRE_SOFT, dstSliceShift );
    }

    @Test
    public void test02() throws IOException {
        final int srcSliceShift = 28; // 256M bytes per slice
        final int dstSliceShift = 28; // 256M bytes per slice
        final long size = 3L * ( 1L << 30 ); // 3 GiB
        testImpl("./testIn.bin", size, MappedByteBufferInputStream.CacheMode.FLUSH_PRE_SOFT, srcSliceShift,
                 "./testOut.bin", MappedByteBufferInputStream.CacheMode.FLUSH_PRE_SOFT, dstSliceShift );
    }

    @Test
    public void test11() throws IOException {
        final int srcSliceShift = 28; // 256M bytes per slice
        final int dstSliceShift = 27; // 128M bytes per slice
        final long size = 3L * ( 1L << 30 ); // 3 GiB
        testImpl("./testIn.bin", size, MappedByteBufferInputStream.CacheMode.FLUSH_PRE_SOFT, srcSliceShift,
                 "./testOut.bin", MappedByteBufferInputStream.CacheMode.FLUSH_PRE_SOFT, dstSliceShift );
    }

    @Test
    public void test12() throws IOException {
        final int srcSliceShift = 27; // 128M bytes per slice
        final int dstSliceShift = 28; // 256M bytes per slice
        final long size = 3L * ( 1L << 30 ); // 3 GiB
        testImpl("./testIn.bin", size, MappedByteBufferInputStream.CacheMode.FLUSH_PRE_SOFT, srcSliceShift,
                 "./testOut.bin", MappedByteBufferInputStream.CacheMode.FLUSH_PRE_SOFT, dstSliceShift );
    }

    public static void main(final String args[]) throws IOException {
        final String tstname = TestByteBufferCopyStream.class.getName();
        org.junit.runner.JUnitCore.main(tstname);
    }
}
