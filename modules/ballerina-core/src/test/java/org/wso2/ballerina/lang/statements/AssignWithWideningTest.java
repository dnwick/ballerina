/*
 * Copyright (c) 2016, WSO2 Inc. (http://wso2.com) All Rights Reserved.
 * <p>
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.ballerina.lang.statements;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.ballerina.core.model.BallerinaFile;
import org.wso2.ballerina.core.model.values.BDouble;
import org.wso2.ballerina.core.model.values.BFloat;
import org.wso2.ballerina.core.model.values.BInteger;
import org.wso2.ballerina.core.model.values.BLong;
import org.wso2.ballerina.core.model.values.BValue;
import org.wso2.ballerina.core.utils.ParserUtils;
import org.wso2.ballerina.lang.util.Functions;

/**
 * Test assignment statement with implicit casting (widening)
 */
public class AssignWithWideningTest {
    private BallerinaFile bFile;

    @BeforeClass
    public void setup() {
        bFile = ParserUtils.parseBalFile("lang/statements/assignment/assign-with-implicit-cast.bal");
    }

    @Test(description = "Test assignment of int to long")
    public void testAssignmentStatementIntToLong() {
        BValue[] args = {new BInteger(100)};
        BValue[] returns = Functions.invoke(bFile, "testIntCastLongStmt", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BLong.class);

        long actual = ((BLong) returns[0]).longValue();
        long expected = 100;
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test assignment of int to float")
    public void testAssignmentStatementIntToFloat() {
        BValue[] args = {new BInteger(100)};
        BValue[] returns = Functions.invoke(bFile, "testIntCastFloatStmt", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BFloat.class);

        float actual = ((BFloat) returns[0]).floatValue();
        float expected = 100f;
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test assignment of int to double")
    public void testAssignmentStatementIntToDouble() {
        BValue[] args = {new BInteger(100)};
        BValue[] returns = Functions.invoke(bFile, "testIntCastDoubleStmt", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BDouble.class);

        double actual = ((BDouble) returns[0]).doubleValue();
        double expected = 100d;
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test assignment of long to float")
    public void testAssignmentStatementLongToFloat() {
        BValue[] args = {new BLong(100L)};
        BValue[] returns = Functions.invoke(bFile, "testLongCastFloatStmt", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BFloat.class);

        float actual = ((BFloat) returns[0]).floatValue();
        float expected = 100f;
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test assignment of long to double")
    public void testAssignmentStatementLongToDouble() {
        BValue[] args = {new BLong(100L)};
        BValue[] returns = Functions.invoke(bFile, "testLongCastDoubleStmt", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BDouble.class);

        double actual = ((BDouble) returns[0]).doubleValue();
        double expected = 100d;
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test assignment of float to double")
    public void testAssignmentStatementFloatToDouble() {
        BValue[] args = {new BFloat(100f)};
        BValue[] returns = Functions.invoke(bFile, "testFloatCastDoubleStmt", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BDouble.class);

        double actual = ((BDouble) returns[0]).doubleValue();
        double expected = 100d;
        Assert.assertEquals(actual, expected);
    }


    @Test(description = "Test binary expression with int and long")
    public void testBinaryExpressionIntToLong() {
        BValue[] args = {new BInteger(100)};
        BValue[] returns = Functions.invoke(bFile, "testBinaryExpressionIntAndLongStmt", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BLong.class);

        long actual = ((BLong) returns[0]).longValue();
        long expected = 200;
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test binary expression with int and float")
    public void testBinaryExpressionIntToFloat() {
        BValue[] args = {new BInteger(100)};
        BValue[] returns = Functions.invoke(bFile, "testBinaryExpressionIntAndFloatStmt", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BFloat.class);

        float actual = ((BFloat) returns[0]).floatValue();
        float expected = 200f;
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test binary expression with int and double")
    public void testBinaryExpressionIntToDouble() {
        BValue[] args = {new BInteger(100)};
        BValue[] returns = Functions.invoke(bFile, "testBinaryExpressionIntAndDoubleStmt", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BDouble.class);

        double actual = ((BDouble) returns[0]).doubleValue();
        double expected = 200d;
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test binary expression with long and float")
    public void testBinaryExpressionLongToFloat() {
        BValue[] args = {new BLong(100L)};
        BValue[] returns = Functions.invoke(bFile, "testBinaryExpressionLongAndFloatStmt", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BFloat.class);

        float actual = ((BFloat) returns[0]).floatValue();
        float expected = 200f;
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test binary expression with long and double")
    public void testBinaryExpressionLongToDouble() {
        BValue[] args = {new BLong(100L)};
        BValue[] returns = Functions.invoke(bFile, "testBinaryExpressionLongAndDoubleStmt", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BDouble.class);

        double actual = ((BDouble) returns[0]).doubleValue();
        double expected = 200d;
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test binary expression with float and double")
    public void testBinaryExpressionFloatToDouble() {
        BValue[] args = {new BFloat(100f)};
        BValue[] returns = Functions.invoke(bFile, "testBinaryExpressionFloatAndDoubleStmt", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BDouble.class);

        double actual = ((BDouble) returns[0]).doubleValue();
        double expected = 200d;
        Assert.assertEquals(actual, expected);
    }
}
