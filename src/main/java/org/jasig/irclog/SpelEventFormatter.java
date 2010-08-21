/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jasig.irclog;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.irclog.events.IrcEvent;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class SpelEventFormatter implements EventFormatter {
    private final Map<Class<IrcEvent>, Expression> formatExpressions = new LinkedHashMap<Class<IrcEvent>, Expression>();
    private final SpelExpressionParser expressionParser = new SpelExpressionParser();
    
    protected final Log logger = LogFactory.getLog(this.getClass());
    
    @SuppressWarnings("unchecked")
    public void setFormatExpressions(Properties expressions) {
        this.formatExpressions.clear();
        
        for (final Map.Entry<Object, Object> expressionItr : expressions.entrySet()) {
            final String key = (String)expressionItr.getKey();
            
            final Class<IrcEvent> eventClass;
            try {
                eventClass = (Class<IrcEvent>)Class.forName(key);
            }
            catch (ClassNotFoundException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            
            final String expressionText = (String)expressionItr.getValue();
            final Expression expression = this.expressionParser.parseExpression(expressionText);
            
            this.logger.debug("Compiled expression '" + expression.getExpressionString() + " for " + eventClass);
            
            this.formatExpressions.put(eventClass, expression);
        }
    }
    
    /* (non-Javadoc)
     * @see org.jasig.irclog.EventFormatter#formatEvent(org.jasig.irclog.events.IrcEvent)
     */
    public String formatEvent(IrcEvent event) {
        final Expression expression = this.formatExpressions.get(event.getClass());
        if (expression == null) {
            logger.warn("No Expression configured for event " + event + ". event.toString() will be returned");
            return event.toString();
        }
        
        final String message = expression.getValue(event, String.class);
        this.logger.trace("Formatted '" + expression.getExpressionString() + "' to '" + message);
        return message;
    }
}
