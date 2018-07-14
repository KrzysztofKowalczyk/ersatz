/*
 * Copyright (C) 2018 Christopher J. Stehno
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
package com.stehno.ersatz.impl

import com.stehno.ersatz.ReceivedMessage
import com.stehno.ersatz.SentMessage
import com.stehno.ersatz.WebSocketExpectations
import com.stehno.ersatz.WsMessageType
import io.undertow.websockets.core.BufferedBinaryMessage
import io.undertow.websockets.core.BufferedTextMessage

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

import static com.stehno.ersatz.WsMessageType.*
import static java.util.concurrent.TimeUnit.SECONDS

class WebSocketExpectationsImpl implements WebSocketExpectations {

    private final CountDownLatch connectionLatch = new CountDownLatch(1)
    private final List<ReceivedMessageImpl> receivedMessages = []
    private final List<SentMessageImpl> sentMessages = []
    final String path

    WebSocketExpectationsImpl(final String path) {
        this.path = path
    }

    void connect() {
        connectionLatch.countDown()
    }

    boolean isConnected() {
        connectionLatch.count == 0
    }

    @Override
    ReceivedMessage receive(Object payload) {
        switch (resolve(payload)) {
            case BINARY:
                return receive(payload, BINARY)
            default:
                return receive(payload.toString(), TEXT)
        }
    }

    @Override
    ReceivedMessage receive(Object payload, WsMessageType messageType) {
        ReceivedMessageImpl message = new ReceivedMessageImpl(payload: payload, messageType: messageType)
        receivedMessages << message

        message
    }

    @Override
    ReceivedMessage receive(@DelegatesTo(ReceivedMessage) Closure closure) {
        ReceivedMessageImpl message = new ReceivedMessageImpl()
        closure.delegate = message
        closure.call()
        receivedMessages << message

        message
    }

    @Override
    SentMessage send(Object payload) {
        switch (resolve(payload)) {
            case BINARY:
                return send(payload, BINARY)
            default:
                return send(payload.toString(), TEXT)
        }
    }

    @Override
    SentMessage send(Object payload, WsMessageType messageType) {
        SentMessageImpl message = new SentMessageImpl(payload: payload, messageType: messageType)
        sentMessages << message
        message
    }

    @Override
    SentMessage send(@DelegatesTo(SentMessage) Closure closure) {
        SentMessageImpl message = new SentMessageImpl()
        closure.delegate = message
        closure.call()
        sentMessages << message
        message
    }

    @Override
    ReceivedMessage receive(Consumer<ReceivedMessage> config) {
        ReceivedMessageImpl message = new ReceivedMessageImpl()
        config.accept(message)
        receivedMessages << message
        message
    }

    @Override
    SentMessage send(Consumer<SentMessage> config) {
        SentMessageImpl message = new SentMessageImpl()
        config.accept(message)
        sentMessages << message
        message
    }

    int getExpectedMessageCount() {
        receivedMessages.size()
    }

    void eachSender(Closure closure) {
        sentMessages.each closure
    }

    void eachMessage(Closure closure) {
        receivedMessages.each closure
    }

    ReceivedMessageImpl findMatch(final BufferedTextMessage message) {
        receivedMessages.find { m -> m.matches(message) }
    }

    ReceivedMessageImpl findMatch(final BufferedBinaryMessage message) {
        receivedMessages.find { m -> m.matches(message) }
    }

    boolean verify(final long timeout = 1, final TimeUnit unit = SECONDS) {
        connectionLatch.await(timeout, unit) && receivedMessages.every { m -> m.marked(timeout, unit) }
    }
}