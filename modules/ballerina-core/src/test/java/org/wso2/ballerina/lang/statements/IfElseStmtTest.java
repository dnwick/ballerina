/*
*  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/
package org.wso2.ballerina.lang.statements;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.ballerina.core.exception.SemanticException;
import org.wso2.ballerina.core.model.BallerinaFile;
import org.wso2.ballerina.core.model.values.BInteger;
import org.wso2.ballerina.core.model.values.BString;
import org.wso2.ballerina.core.model.values.BValue;
import org.wso2.ballerina.core.utils.ParserUtils;
import org.wso2.ballerina.lang.util.Functions;

/**
 * This contains methods to test different behaviours of the if-else statement.
 *
 * @since 0.8.0
 */
public class IfElseStmtTest {

    private BallerinaFile bFile;
    private final String funcName = "testIfStmt";

    @BeforeClass
    public void setup() {
        bFile = ParserUtils.parseBalFile("lang/statements/if-stmt.bal");
    }

    @Test(description = "Check a == b")
    public void testIfBlock() {
        BValue[] args = {new BInteger(10), new BInteger(10), new BInteger(20)};
        BValue[] returns = Functions.invoke(bFile, funcName, args);

        Assert.assertEquals(returns.length, 2);
        Assert.assertSame(returns[0].getClass(), BInteger.class);
        // TODO Uncomment following line once the multiple return feature is implemented
//        Assert.assertSame(returns[1].getClass(), BInteger.class);

        int actual = ((BInteger) returns[0]).intValue();
        int expected = 110;
        Assert.assertEquals(actual, expected);

//        actual = ((BInteger) returns[1]).intValue();
//        expected = 21;
//        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Check a == b + 1")
    public void testElseIfFirstBlock() {
        BValue[] args = {new BInteger(11), new BInteger(10), new BInteger(20)};
        BValue[] returns = Functions.invoke(bFile, funcName, args);

        Assert.assertEquals(returns.length, 2);
        Assert.assertSame(returns[0].getClass(), BInteger.class);
        // TODO Uncomment following line once the multiple return feature is implemented
//        Assert.assertSame(returns[1].getClass(), BInteger.class);

        int actual = ((BInteger) returns[0]).intValue();
        int expected = 210;
        Assert.assertEquals(actual, expected);

//        actual = ((BInteger) returns[1]).intValue();
//        expected = 21;
//        Assert.assertEquals(actual, expected);

    }

    @Test(description = "Check a == b + 2")
    public void testElseIfSecondBlock() {
        BValue[] args = {new BInteger(12), new BInteger(10), new BInteger(20)};
        BValue[] returns = Functions.invoke(bFile, funcName, args);

        Assert.assertEquals(returns.length, 2);
        Assert.assertSame(returns[0].getClass(), BInteger.class);
        // TODO Uncomment following line once the multiple return feature is implemented
//        Assert.assertSame(returns[1].getClass(), BInteger.class);

        int actual = ((BInteger) returns[0]).intValue();
        int expected = 310;
        Assert.assertEquals(actual, expected);

//        actual = ((BInteger) returns[1]).intValue();
//        expected = 21;
//        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Check else")
    public void testElseBlock() {
        BValue[] args = {new BInteger(10), new BInteger(100), new BInteger(20)};
        BValue[] returns = Functions.invoke(bFile, funcName, args);

        Assert.assertEquals(returns.length, 2);
        Assert.assertSame(returns[0].getClass(), BInteger.class);
        // TODO Uncomment following line once the multiple return feature is implemented
//        Assert.assertSame(returns[1].getClass(), BInteger.class);

        int actual = ((BInteger) returns[0]).intValue();
        int expected = 410;
        Assert.assertEquals(actual, expected);

//        actual = ((BInteger) returns[1]).intValue();
//        expected = 21;
//        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Check simple ifElse")
    public void testAge() {
        BValue[] args = {new BInteger(21)};
        BValue[] returns = Functions.invoke(bFile, "testAgeGroup", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BString.class);
        String actual = returns[0].stringValue();
        String expected = "elder";
        Assert.assertEquals(actual, expected);

        args = new BValue[]{new BInteger(16)};
        returns = Functions.invoke(bFile, "testAgeGroup", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BString.class);

        actual = returns[0].stringValue();
        expected = "minor";
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Check the scope managing in ifelse block")
    public void testIfElseBlockScopes() {
        BValue[] args = { new BInteger(1) };
        BValue[] returns = Functions.invoke(bFile, "ifElseScope", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class, "Class type mismatched");
        BInteger actual = (BInteger) returns[0];
        Assert.assertEquals(actual.intValue(), 200, "mismatched output value");

        args = new BValue[] { new BInteger(2) };
        returns = Functions.invoke(bFile, "ifElseScope", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class, "Class type mismatched");
        actual = (BInteger) returns[0];
        Assert.assertEquals(actual.intValue(), 400, "mismatched output value");

        args = new BValue[] { new BInteger(16) };
        returns = Functions.invoke(bFile, "ifElseScope", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class, "Class type mismatched");
        actual = (BInteger) returns[0];
        Assert.assertEquals(actual.intValue(), 500, "mismatched output value");
    }

    @Test(description = "Check the scope managing in nested ifelse block")
    public void testNestedIfElseBlockScopes() {
        BValue[] args = { new BInteger(1), new BInteger(1) };
        BValue[] returns = Functions.invoke(bFile, "nestedIfElseScope", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class, "Class type mismatched");
        BInteger actual = (BInteger) returns[0];
        Assert.assertEquals(actual.intValue(), 100, "mismatched output value");

        args = new BValue[] { new BInteger(1), new BInteger(2) };
        returns = Functions.invoke(bFile, "nestedIfElseScope", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class, "Class type mismatched");
        actual = (BInteger) returns[0];
        Assert.assertEquals(actual.intValue(), 200, "mismatched output value");

        args = new BValue[] { new BInteger(2), new BInteger(2) };
        returns = Functions.invoke(bFile, "nestedIfElseScope", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class, "Class type mismatched");
        actual = (BInteger) returns[0];
        Assert.assertEquals(actual.intValue(), 300, "mismatched output value");

        args = new BValue[] { new BInteger(2), new BInteger(3) };
        returns = Functions.invoke(bFile, "nestedIfElseScope", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class, "Class type mismatched");
        actual = (BInteger) returns[0];
        Assert.assertEquals(actual.intValue(), 400, "mismatched output value");

        args = new BValue[] { new BInteger(3), new BInteger(3) };
        returns = Functions.invoke(bFile, "nestedIfElseScope", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class, "Class type mismatched");
        actual = (BInteger) returns[0];
        Assert.assertEquals(actual.intValue(), 500, "mismatched output value");

        args = new BValue[] { new BInteger(3), new BInteger(4) };
        returns = Functions.invoke(bFile, "nestedIfElseScope", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class, "Class type mismatched");
        actual = (BInteger) returns[0];
        Assert.assertEquals(actual.intValue(), 600, "mismatched output value");
    }

    @Test(description = "Test if condition parameter resolver scope")
    public void testIfConditionScope() {
        BallerinaFile balFile = ParserUtils.parseBalFile("lang/statements/if-condition-scope.bal");

        BValue[] args1 = { new BInteger(3)};
        BValue[] returns = Functions.invoke(balFile, "testConditionScope", args1);
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class, "Class type mismatched");
        BInteger actual = (BInteger) returns[0];
        Assert.assertEquals(actual.intValue(), 10, "if condition scope not set properly");

        BValue[] args2 = new BValue[] { new BInteger(6) };
        returns = Functions.invoke(balFile, "testConditionScope", args2);
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class, "Class type mismatched");
        actual = (BInteger) returns[0];
        Assert.assertEquals(actual.intValue(), 20, "elseif condition scope not set properly");
    }

    @Test(description = "Test if statement with incompatible types",
            expectedExceptions = {SemanticException.class},
            expectedExceptionsMessageRegExp = "if-stmnt-with-incompatible-types.bal:2: incompatible type: " +
                    "'boolean' expected, found 'string'")
    public void testIfStmtWithIncompatibleTypes() {
        ParserUtils.parseBalFile("lang/statements/if-stmnt-with-incompatible-types.bal");
    }

    @Test(description = "Test else-if statement with incompatible types",
            expectedExceptions = {SemanticException.class},
            expectedExceptionsMessageRegExp = "elseif-stmnt-with-incompatible-types.bal:2: incompatible type: " +
                    "'boolean' expected, found 'string'")
    public void testElseIfStmtWithIncompatibleTypes() {
        ParserUtils.parseBalFile("lang/statements/elseif-stmnt-with-incompatible-types.bal");
    }
}
