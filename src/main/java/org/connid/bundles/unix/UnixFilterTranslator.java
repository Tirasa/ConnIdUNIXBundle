/**
 * Copyright (C) ${project.inceptionYear} ConnId (connid-dev@googlegroups.com)
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
package org.connid.bundles.unix;

import org.connid.bundles.unix.search.Operand;
import org.connid.bundles.unix.search.Operator;
import org.identityconnectors.common.StringUtil;
import org.identityconnectors.framework.common.objects.AttributeUtil;
import org.identityconnectors.framework.common.objects.filter.*;

public class UnixFilterTranslator extends AbstractFilterTranslator<Operand> {

    @Override
    protected Operand createEqualsExpression(final EqualsFilter filter,
            final boolean not) {
        if (filter == null) {
            return null;
        }
        String value = AttributeUtil.getAsStringValue(filter.getAttribute());
        if (StringUtil.isBlank(value)) {
            return null;
        }
        return new Operand(Operator.EQ,
                filter.getAttribute().getName(), value, not);
    }

    @Override
    protected Operand createStartsWithExpression(final StartsWithFilter filter,
            final boolean not) {
        if (filter == null) {
            return null;
        }
        return new Operand(Operator.SW,
                filter.getName(), filter.getValue(), not);
    }

    @Override
    protected Operand createEndsWithExpression(final EndsWithFilter filter,
            final boolean not) {
        if (filter == null) {
            return null;
        }

        return new Operand(Operator.EW,
                filter.getName(), filter.getValue(), not);
    }

    @Override
    protected Operand createContainsExpression(final ContainsFilter filter,
            final boolean not) {
        if (filter == null) {
            return null;
        }

        return new Operand(Operator.C,
                filter.getName(), filter.getValue(), not);
    }

    @Override
    protected Operand createOrExpression(
            final Operand leftExpression, final Operand rightExpression) {
        if (leftExpression == null || rightExpression == null) {
            return null;
        }

        return new Operand(Operator.OR, leftExpression, rightExpression);
    }

    @Override
    protected Operand createAndExpression(final Operand leftExpression,
            final Operand rightExpression) {
        if (leftExpression == null || rightExpression == null) {
            return null;
        }

        return new Operand(Operator.AND, leftExpression, rightExpression);
    }

    private String checkSearchValue(String value) {
        if (StringUtil.isEmpty(value)) {
            return null;
        }
        if (value.contains("*") || value.contains("&") || value.contains("|")) {
            throw new IllegalArgumentException(
                    "Value of search attribute contains illegal character(s).");
        }
        return value;
    }
}
