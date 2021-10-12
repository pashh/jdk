/*
 * Copyright (c) 2021, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/**
 * @test
 * @bug 8193682
 * @summary Test DeflatorOutputStream for infinite loop while writing on closed stream
 * @run testng GZipLoopTest
 */
import java.io.*;
import java.util.Random;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipOutputStream;

import org.testng.annotations.Test;
import static org.testng.Assert.fail;


public class GZipLoopTest {
    private static final int FINISH_NUM = 512;
    private static OutputStream outStream = new OutputStream() {
        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            //throw exception during write
            throw new IOException();
        }
        @Override
        public void write(byte b[]) throws IOException {}
        @Override
        public void write(int b) throws IOException {}
    };
    private static byte[] b = new byte[FINISH_NUM];
    private static Random rand = new Random();

    @Test
    public void testGZipClose() throws IOException {
        rand.nextBytes(b);
        GZIPOutputStream zip = new GZIPOutputStream(outStream);
        try {
            zip.write(b, 0, FINISH_NUM);
            //close zip
            zip.close();
        } catch (IOException e) {
            //expected
        }
        for (int i = 0; i < 3; i++) {
            try {
                zip.write(b, 0, FINISH_NUM);
                fail("Deflater closed exception not thrown");
            } catch (NullPointerException | IOException e) {
                //for the first write operation IOException will be thrown
                //second write operation should throw NPE with deflator closed error instead of infinite loop
            }
        }
    }

    @Test
    public void testGZipFinish() throws IOException {
        rand.nextBytes(b);
        GZIPOutputStream zip = new GZIPOutputStream(outStream);
        try {
            zip.write(b, 0, FINISH_NUM);
            //close zip using finish()
            zip.finish();
        } catch (IOException e) {
            //expected
        }
        for (int i = 0; i < 3; i++) {
            try {
                zip.write(b, 0, FINISH_NUM);
                fail("Deflater closed exception not thrown");
            } catch (NullPointerException | IOException e) {
                //for the first write operation IOException will be thrown
                //second write operation should throw NPE with deflator closed error instead of infinite loop
            }
        }
    }

    @Test
    public void testZipClose() throws IOException {
        rand.nextBytes(b);
        ZipOutputStream zip = new ZipOutputStream(outStream);
        try {
            zip.write(b, 1, FINISH_NUM-1);
            //close zip
            zip.close();
        } catch (IOException e) {
            //expected
        }
        for (int i = 0; i < 3; i++) {
            try {
                zip.write(b, 1, FINISH_NUM-1);
                fail("Deflater closed exception not thrown");
            } catch (NullPointerException | IOException e) {
                //for the first write operation IOException will be thrown
                //second write operation should throw NPE with deflator closed error instead of infinite loop
            }
        }
    }

    @Test
    public void testZipFinish() throws IOException {
        rand.nextBytes(b);
        ZipOutputStream zip = new ZipOutputStream(outStream);
        try {
            zip.write(b, 0, FINISH_NUM);
            //close zip using finish()
            zip.finish();
        } catch (IOException e) {
            //expected
        }
        for (int i = 0; i < 3; i++) {
            try {
                zip.write(b, 1, FINISH_NUM-1);
                fail("Deflater closed exception not thrown");
            } catch (NullPointerException | IOException e) {
                //for the first write operation IOException will be thrown
                //second write operation should throw NPE with deflator closed error instead of infinite loop
            }
        }
    }


}
