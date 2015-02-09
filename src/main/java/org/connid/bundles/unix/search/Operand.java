/**
 * Copyright (C) 2011 ConnId (connid-dev@googlegroups.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.connid.bundles.unix.search;

import org.identityconnectors.framework.common.objects.Uid;

public class Operand {

    private Operator operator = null;

    private String attributeName = "";

    private String attributeValue = "";

    private boolean not = false;

    private Operand firstOperand = null;

    private Operand secondOperand = null;

    public Operand(final Operator operator, final String name, final String value, final boolean not) {
        this.operator = operator;
        attributeName = name;
        attributeValue = value;
        this.not = not;
    }

    public Operand(final Operator operator, final Operand firstOperand, final Operand secondOperand) {
        this.operator = operator;
        this.firstOperand = firstOperand;
        this.secondOperand = secondOperand;
    }   

    public final Operand getFirstOperand() {
        return firstOperand;
    }

    public final Operand getSecondOperand() {
        return secondOperand;
    }

    public final String getAttributeName() {
        return attributeName;
    }

    public final boolean isUid() {
        return attributeName.equalsIgnoreCase(Uid.NAME);
    }

    public final String getAttributeValue() {
        return attributeValue;
    }

    public final boolean isNot() {
        return not;
    }

    public final Operator getOperator() {
        return operator;
    }
}
