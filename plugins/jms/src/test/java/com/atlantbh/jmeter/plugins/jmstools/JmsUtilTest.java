/*
 * Copyright 2013 undera.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.atlantbh.jmeter.plugins.jmstools;

import com.atlantbh.jmeter.plugins.jmstools.JmsUtil;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class JmsUtilTest {

    public JmsUtilTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getDefaultParameters method, of class JmsUtil.
     */
    @Test
    public void testGetDefaultParameters() {
        System.out.println("getDefaultParameters");
        JmsUtil instance = new JmsUtil();
        Arguments expResult = null;
        Arguments result = instance.getDefaultParameters();
    }

    /**
     * Test of runTest method, of class JmsUtil.
     */
    @Test
    public void testRunTest() {
        System.out.println("runTest");
        JavaSamplerContext ctx = new JavaSamplerContext(new Arguments());
        JmsUtil instance = new JmsUtil();
        SampleResult expResult = null;
        SampleResult result = instance.runTest(ctx);
    }

    /**
     * Test of setupTest method, of class JmsUtil.
     */
    @Test
    public void testSetupTest() {
        System.out.println("setupTest");
        JavaSamplerContext arg0 = null;
        JmsUtil instance = new JmsUtil();
        instance.setupTest(arg0);
        // TODO review the generated test code and remove the default call to fail.

    }

    /**
     * Test of teardownTest method, of class JmsUtil.
     */
    @Test
    public void testTeardownTest() {
        System.out.println("teardownTest");
        JavaSamplerContext arg0 = null;
        JmsUtil instance = new JmsUtil();
        instance.teardownTest(arg0);
        // TODO review the generated test code and remove the default call to fail.

    }
}
