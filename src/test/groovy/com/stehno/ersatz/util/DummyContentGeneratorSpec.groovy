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
package com.stehno.ersatz.util

import spock.lang.Specification

import static com.stehno.ersatz.util.DummyContentGenerator.generate
import static com.stehno.ersatz.util.StorageUnit.*

class DummyContentGeneratorSpec extends Specification {

    def 'generate'() {
        expect:
        generate(10.5d, unit).length == result

        where:
        unit      || result
        BYTES     || 11
        KILOBYTES || 10752
        MEGABYTES || 11010048
    }
}
