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
package com.jogamp.common;

import java.io.PrintStream;

/**
 * @since 2.3.0
 */
public class ExceptionUtils {
    public static void dumpStack(final PrintStream out) {
        dumpStack(out, 1, -1);
    }
    public static void dumpStack(final PrintStream out, final int skip, final int depth) {
        dumpStack(out, new Exception(""), skip+1, depth);
    }
    public static void dumpStack(final PrintStream out, final Throwable t, final int skip, final int depth) {
        dumpStack(out, t.getStackTrace(), skip, depth);
    }
    public static void dumpStack(final PrintStream out, final StackTraceElement[] stack, final int skip, final int depth) {
        if( null == stack ) {
            return;
        }
        final int maxDepth;
        if( 0 > depth ) {
            maxDepth = stack.length;
        } else {
            maxDepth = Math.min(depth+skip, stack.length);
        }
        for(int i=skip; i<maxDepth; i++) {
            out.println("    ["+i+"]: "+stack[i]);
        }
    }

    /**
     * Dumps a {@link Throwable} in a decorating message including the current thread name,
     * and its {@link #dumpStack(PrintStream, StackTraceElement[], int, int) stack trace}.
     * <p>
     * Implementation will iterate through all {@link Throwable#getCause() causes}.
     * </p>
     */
    public static void dumpThrowable(final String additionalDescr, final Throwable t) {
        System.err.println("Caught "+additionalDescr+" "+t.getClass().getSimpleName()+": "+t.getMessage()+" on thread "+Thread.currentThread().getName());
        dumpStack(System.err, t.getStackTrace(), 0, -1);
        int causeDepth = 1;
        for( Throwable cause = t.getCause(); null != cause; cause = cause.getCause() ) {
            System.err.println("Caused["+causeDepth+"] by "+cause.getClass().getSimpleName()+": "+cause.getMessage()+" on thread "+Thread.currentThread().getName());
            dumpStack(System.err, cause.getStackTrace(), 0, -1);
            causeDepth++;
        }
    }

}
