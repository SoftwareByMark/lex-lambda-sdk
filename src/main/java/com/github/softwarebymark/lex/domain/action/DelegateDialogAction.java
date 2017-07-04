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

import com.github.softwarebymark.lex.domain.LexRequest;

import java.util.Collections;
import java.util.Map;

/**
 * The Delegate Dialog Action
 *
 * @author Mark Borner
 */
public class DelegateDialogAction extends DialogAction {

    private final Map<String,String> slots;

    public DelegateDialogAction(LexRequest lexRequest) {
        super("Delegate");
        if (lexRequest == null) {
            throw new IllegalArgumentException("LexRequest should not be null");
        }
        this.slots = lexRequest.getIntent().getSlots();
    }

    public Map<String, String> getSlots() {
        return Collections.unmodifiableMap(slots);
    }
}
