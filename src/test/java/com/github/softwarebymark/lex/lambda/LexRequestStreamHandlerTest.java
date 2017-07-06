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

package com.github.softwarebymark.lex.lambda;

import com.github.softwarebymark.lex.AbstractLexRequestHandler;
import com.github.softwarebymark.lex.LexRequestHandler;
import com.github.softwarebymark.lex.domain.*;
import com.github.softwarebymark.lex.domain.action.*;
import com.github.softwarebymark.lex.verifier.ButtonVerifier;
import com.github.softwarebymark.lex.verifier.GenericAttachmentVerifier;
import com.github.softwarebymark.lex.verifier.MessageVerifier;
import com.github.softwarebymark.lex.verifier.ResponseCardVerifier;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author Mark Borner
 */
public class LexRequestStreamHandlerTest {

    @Test (expected = RuntimeException.class)
    public void badBotName() throws Exception {
        LexRequestStreamHandler lexRequestStreamHandler = new LexRequestStreamHandler("bad-robot", new LexRequestHandler() {
            @Override
            public LexResponse handleRequest(LexRequest lexRequest, Map<String,String> sessionAttributes) {
                return null;
            }
        }) {};
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("request.json");
        lexRequestStreamHandler.handleRequest(inputStream, new ByteArrayOutputStream(), null);
    }

    @Test
    public void realJson() throws Exception {
        LexRequestStreamHandler lexRequestStreamHandler = new LexRequestStreamHandler("OrderBot", new LexRequestHandler() {
            @Override
            public LexResponse handleRequest(LexRequest lexRequest, Map<String,String> sessionAttributes) {
                assertNotNull(lexRequest);
                assertNotNull(lexRequest.getMessageVersion());
                assertNotNull(lexRequest.getBot());
                assertNotNull(lexRequest.getBot().getName());
                assertNotNull(lexRequest.getBot().getAlias());
                assertNotNull(lexRequest.getBot().getVersion());
                assertNotNull(lexRequest.getInputTranscript());
                assertNotNull(lexRequest.getIntent());
                assertNotNull(lexRequest.getIntent().getName());
                assertNotNull(lexRequest.getIntent().getSlots());
                assertEquals(3, lexRequest.getIntent().getSlots().size());
                assertNotNull(lexRequest.getInvocationSource());
                assertNotNull(lexRequest.getOutputDialogMode());
                assertNotNull(lexRequest.getUserId());
                assertNotNull(lexRequest.getSessionAttributes());
                assertEquals(2, lexRequest.getSessionAttributes().size());
                assertNotNull(sessionAttributes);
                assertEquals(2, sessionAttributes.size());
                return createCloseDialogActionResponse();
            }
        }) {};
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("request.json");
        lexRequestStreamHandler.handleRequest(inputStream, new ByteArrayOutputStream(), null);
    }

    @Test
    public void close() throws Exception {
        LexRequestStreamHandler lexRequestStreamHandler = new LexRequestStreamHandler("OrderBot", new LexRequestHandler() {
            @Override
            public LexResponse handleRequest(LexRequest lexRequest, Map<String,String> sessionAttributes) {
                sessionAttributes.remove("key1");
                sessionAttributes.put("key3", "value3");
                return createCloseDialogActionResponse();
            }
        }) {};
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("request.json");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        lexRequestStreamHandler.handleRequest(inputStream, byteArrayOutputStream, null);
        byteArrayOutputStream.close();
        String responseJson = byteArrayOutputStream.toString();
        String expectedJson = "{\"dialogAction\":{\"type\":\"Close\",\"fulfillmentState\":\"Fulfilled\",\"message\":{\"contentType\":\"PlainText\",\"content\":\"This is your reply.\"},\"responseCard\":{\"version\":1,\"contentType\":\"application/vnd.amazonaws.card.generic\",\"genericAttachments\":[{\"title\":\"Title\",\"subTitle\":\"Subtitle\",\"imageUrl\":\"https://www.google.com/image.jpg\",\"attachmentLinkUrl\":\"http://www.google.com/\",\"buttons\":[{\"text\":\"Button\",\"value\":\"value\"}]}]}},\"sessionAttributes\":{\"key2\":\"value2\",\"key3\":\"value3\"}}";
        JSONAssert.assertEquals(expectedJson, responseJson, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void confirmIntent() throws Exception {
        LexRequestStreamHandler lexRequestStreamHandler = new LexRequestStreamHandler("OrderBot", new LexRequestHandler() {
            @Override
            public LexResponse handleRequest(LexRequest lexRequest, Map<String,String> sessionAttributes) {
                sessionAttributes.remove("key1");
                sessionAttributes.put("key3", "value3");
                return createConfirmIntentDialogActionResponse(lexRequest);
            }
        }) {};
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("request.json");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        lexRequestStreamHandler.handleRequest(inputStream, byteArrayOutputStream, null);
        byteArrayOutputStream.close();
        String responseJson = byteArrayOutputStream.toString();
        String expectedJson = "{\"dialogAction\":{\"type\":\"ConfirmIntent\",\"intentName\":\"OrderIntent\",\"slots\":{\"slot1\":\"value1\",\"slot2\":\"value2\",\"slot3\":\"value3\"},\"responseCard\":{\"version\":1,\"contentType\":\"application/vnd.amazonaws.card.generic\",\"genericAttachments\":[{\"title\":\"Title\",\"subTitle\":\"Subtitle\",\"imageUrl\":\"https://www.google.com/image.jpg\",\"attachmentLinkUrl\":\"http://www.google.com/\",\"buttons\":[{\"text\":\"Button\",\"value\":\"value\"}]}]},\"message\":{\"contentType\":\"SSML\",\"content\":\"<speak>Yo!</speak>\"}},\"sessionAttributes\":{\"key2\":\"value2\",\"key3\":\"value3\"}}";
        JSONAssert.assertEquals(expectedJson, responseJson, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void delegate() throws Exception {
        LexRequestStreamHandler lexRequestStreamHandler = new LexRequestStreamHandler("OrderBot", new LexRequestHandler() {
            @Override
            public LexResponse handleRequest(LexRequest lexRequest, Map<String,String> sessionAttributes) {
                sessionAttributes.remove("key1");
                sessionAttributes.put("key3", "value3");
                return createDelegateDialogActionResponse(lexRequest);
            }
        }) {};
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("request.json");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        lexRequestStreamHandler.handleRequest(inputStream, byteArrayOutputStream, null);
        byteArrayOutputStream.close();
        String responseJson = byteArrayOutputStream.toString();
        String expectedJson = "{\"dialogAction\":{\"type\":\"Delegate\",\"slots\":{\"slot1\":\"value1\",\"slot2\":\"value2\",\"slot3\":\"value3\"}},\"sessionAttributes\":{\"key2\":\"value2\",\"key3\":\"value3\"}}";
        JSONAssert.assertEquals(expectedJson, responseJson, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void elicitIntent() throws Exception {
        LexRequestStreamHandler lexRequestStreamHandler = new LexRequestStreamHandler("OrderBot", new LexRequestHandler() {
            @Override
            public LexResponse handleRequest(LexRequest lexRequest, Map<String,String> sessionAttributes) {
                sessionAttributes.remove("key1");
                sessionAttributes.put("key3", "value3");
                return createElicitIntentDialogActionResponse();
            }
        }) {};
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("request.json");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        lexRequestStreamHandler.handleRequest(inputStream, byteArrayOutputStream, null);
        byteArrayOutputStream.close();
        String responseJson = byteArrayOutputStream.toString();
        String expectedJson = "{\"dialogAction\":{\"type\":\"ElicitIntent\",\"message\":{\"contentType\":\"PlainText\",\"content\":\"What?\"},\"responseCard\":{\"version\":1,\"contentType\":\"application/vnd.amazonaws.card.generic\",\"genericAttachments\":[{\"title\":\"Title\",\"subTitle\":\"Subtitle\",\"imageUrl\":\"https://www.google.com/image.jpg\",\"attachmentLinkUrl\":\"http://www.google.com/\",\"buttons\":[{\"text\":\"Button\",\"value\":\"value\"}]}]}},\"sessionAttributes\":{\"key2\":\"value2\",\"key3\":\"value3\"}}";
        JSONAssert.assertEquals(expectedJson, responseJson, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void elicitSlot() throws Exception {
        LexRequestStreamHandler lexRequestStreamHandler = new LexRequestStreamHandler("OrderBot", new LexRequestHandler() {
            @Override
            public LexResponse handleRequest(LexRequest lexRequest, Map<String,String> sessionAttributes) {
                sessionAttributes.remove("key1");
                sessionAttributes.put("key3", "value3");
                return createElicitSlotDialogActionResponse(lexRequest);
            }
        }) {};
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("request.json");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        lexRequestStreamHandler.handleRequest(inputStream, byteArrayOutputStream, null);
        byteArrayOutputStream.close();
        String responseJson = byteArrayOutputStream.toString();
        String expectedJson = "{\"dialogAction\":{\"type\":\"ElicitSlot\",\"intentName\":\"OrderIntent\",\"slots\":{\"slot1\":\"value1\",\"slot2\":\"value2\",\"slot3\":\"value3\"},\"slotToElicit\":\"MySlot\",\"message\":{\"contentType\":\"PlainText\",\"content\":\"Value?\"},\"responseCard\":{\"version\":1,\"contentType\":\"application/vnd.amazonaws.card.generic\",\"genericAttachments\":[{\"title\":\"Title\",\"subTitle\":\"Subtitle\",\"imageUrl\":\"https://www.google.com/image.jpg\",\"attachmentLinkUrl\":\"http://www.google.com/\",\"buttons\":[{\"text\":\"Button\",\"value\":\"value\"}]}]}},\"sessionAttributes\":{\"key2\":\"value2\",\"key3\":\"value3\"}}";
        JSONAssert.assertEquals(expectedJson, responseJson, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void serialiseSessioinObjects() throws Exception {
        TypeReference<List<Car>> typeReference = new TypeReference<List<Car>>() {};
        LexRequestStreamHandler lexRequestStreamHandler = new LexRequestStreamHandler("OrderBot", new AbstractLexRequestHandler() {
            @Override
            public LexResponse handleRequest(LexRequest lexRequest, Map<String, String> sessionAttributes) {
                List<Car> cars = getObjectFromSession(sessionAttributes, "cars", typeReference);
                assertNotNull(cars);
                assertEquals(3, cars.size());
                for (Car car : cars) {
                    assertNotNull(car.getMake());
                    assertNotNull(car.getModel());
                }
                cars.remove(0);  // remove "ford"
                Car tesla = new Car();
                tesla.setMake("tesla");
                tesla.setModel("series8");
                cars.add(tesla);
                saveObjectIntoSession(sessionAttributes, "cars", cars, typeReference);
                return createCloseDialogActionResponse(FulfillmentState.Fulfilled);
            }
        }) {};
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("requestWithSessionObject.json");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        lexRequestStreamHandler.handleRequest(inputStream, byteArrayOutputStream, null);
        byteArrayOutputStream.close();
        String responseJson = byteArrayOutputStream.toString();
        String expectedJson = "{\"dialogAction\":{\"type\":\"Close\",\"fulfillmentState\":\"Fulfilled\"},\"sessionAttributes\":{\"cars\":\"[{\\\"make\\\":\\\"vw\\\",\\\"model\\\":\\\"golf\\\"},{\\\"make\\\":\\\"honda\\\",\\\"model\\\":\\\"civic\\\"},{\\\"make\\\":\\\"tesla\\\",\\\"model\\\":\\\"series8\\\"}]\",\"key2\":\"value2\",\"key3\":\"value3\"}}";
        JSONAssert.assertEquals(expectedJson, responseJson, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void verifier() throws Exception {
        LexRequestStreamHandler lexRequestStreamHandler = new LexRequestStreamHandler("OrderBot", new AbstractLexRequestHandler() {
            @Override
            public LexResponse handleRequest(LexRequest lexRequest, Map<String,String> sessionAttributes) {
                List<GenericAttachment> genericAttachments = new ArrayList<>();
                for (int i=0; i< ResponseCardVerifier.MAX_GENERIC_ATTACHMENTS; i++) {
                    genericAttachments.add(new GenericAttachment("Title"));
                }
                GenericAttachment genericAttachment = new GenericAttachment(RandomStringUtils.randomAlphabetic(GenericAttachmentVerifier.MAX_TITLE_LENGTH+1));
                genericAttachment.setSubTitle(RandomStringUtils.randomAlphabetic(GenericAttachmentVerifier.MAX_SUBTITLE_LENGTH+1));
                genericAttachment.setImageUrl(RandomStringUtils.randomAlphabetic(GenericAttachmentVerifier.MAX_URL_LENGTH+1));
                genericAttachment.setAttachmentLinkUrl(RandomStringUtils.randomAlphabetic(GenericAttachmentVerifier.MAX_URL_LENGTH+1));
                for (int i=0; i<GenericAttachmentVerifier.MAX_BUTTONS; i++) {
                    genericAttachment.addButton("foo", "bar");
                }
                genericAttachment.addButton(RandomStringUtils.randomAlphabetic(ButtonVerifier.MAX_TEXT_LENGTH+1), RandomStringUtils.randomAlphabetic(ButtonVerifier.MAX_VALUE_LENGTH+1));
                genericAttachments.add(genericAttachment);
                ResponseCard responseCard = new ResponseCard(genericAttachments);
                String message = RandomStringUtils.randomAlphabetic(MessageVerifier.MAX_CONTENT_LENGTH+1);
                return createCloseDialogActionResponse(FulfillmentState.Fulfilled, message, responseCard);
            }
        }) {};
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("request.json");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        lexRequestStreamHandler.handleRequest(inputStream, byteArrayOutputStream, null);
        byteArrayOutputStream.close();
    }

    private LexResponse createCloseDialogActionResponse() {
        CloseDialogAction closeDialogAction = new CloseDialogAction(FulfillmentState.Fulfilled);
        closeDialogAction.setMessage(new Message("This is your reply."));
        closeDialogAction.setResponseCard(createResponseCard());
        return new LexResponse(closeDialogAction);
    }

    private LexResponse createConfirmIntentDialogActionResponse(LexRequest lexRequest) {
        ConfirmIntentDialogAction confirmIntentDialogAction = new ConfirmIntentDialogAction(lexRequest.getIntent().getName(), lexRequest.getIntent().getSlots(), new Message("<speak>Yo!</speak>"), createResponseCard());
        return new LexResponse(confirmIntentDialogAction);
    }

    private LexResponse createDelegateDialogActionResponse(LexRequest lexRequest) {
        DelegateDialogAction delegateDialogAction = new DelegateDialogAction(lexRequest.getIntent().getSlots());
        return new LexResponse(delegateDialogAction);
    }

    private LexResponse createElicitIntentDialogActionResponse() {
        ElicitIntentDialogAction elicitIntentDialogAction = new ElicitIntentDialogAction(new Message("What?"), createResponseCard());
        return new LexResponse(elicitIntentDialogAction);
    }

    private LexResponse createElicitSlotDialogActionResponse(LexRequest lexRequest) {
        ElicitSlotDialogAction elicitSlotDialogAction = new ElicitSlotDialogAction("MySlot", lexRequest.getIntent().getName(), lexRequest.getIntent().getSlots(), new Message("Value?"), createResponseCard());
        return new LexResponse(elicitSlotDialogAction);
    }

    private ResponseCard createResponseCard() {
        GenericAttachment genericAttachment = new GenericAttachment("Title");
        genericAttachment.setSubTitle("Subtitle");
        genericAttachment.setAttachmentLinkUrl("http://www.google.com/");
        genericAttachment.setImageUrl("https://www.google.com/image.jpg");
        genericAttachment.addButton("Button", "value");
        return new ResponseCard(genericAttachment);
    }

}
