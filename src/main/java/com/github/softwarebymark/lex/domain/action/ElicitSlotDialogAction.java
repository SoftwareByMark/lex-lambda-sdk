/*
 *    Copyright 2017 Mark Borner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.github.softwarebymark.lex.domain.action;

import com.github.softwarebymark.lex.domain.ResponseCard;
import com.github.softwarebymark.lex.domain.LexRequest;
import com.github.softwarebymark.lex.domain.Message;

import java.util.Map;

/**
 * The Elicit Slot Dialog Action
 *
 * @author Mark Borner
 */
public class ElicitSlotDialogAction extends DialogActionWithDetails {

    private final String intentName;
    private final Map<String,String> slots;
    private final String slotToElicit;

    public ElicitSlotDialogAction(String slotToElicit, LexRequest lexRequest) {
        super("ElicitSlot");
        if (slotToElicit == null) {
            throw new IllegalArgumentException("Slot to Elicit should not be null");
        }
        if (lexRequest == null) {
            throw new IllegalArgumentException("LexRequest should not be null");
        }
        this.intentName = lexRequest.getIntent().getName();
        this.slotToElicit = slotToElicit;
        this.slots = lexRequest.getIntent().getSlots();
    }

    public ElicitSlotDialogAction(String slotToElicit, LexRequest lexRequest, Message message) {
        this(slotToElicit, lexRequest);
        setMessage(message);
    }

    public ElicitSlotDialogAction(String slotToElicit, LexRequest lexRequest, Message message, ResponseCard responseCard) {
        this(slotToElicit, lexRequest, message);
        setResponseCard(responseCard);
    }

    public ElicitSlotDialogAction(String slotToElicit, LexRequest lexRequest, ResponseCard responseCard) {
        this(slotToElicit, lexRequest);
        setResponseCard(responseCard);
    }

    public String getIntentName() {
        return intentName;
    }

    public Map<String, String> getSlots() {
        return slots;
    }

    public String getSlotToElicit() {
        return slotToElicit;
    }

}
