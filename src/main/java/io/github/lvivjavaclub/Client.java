/*
 * Copyright (c) 2008-2021, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.lvivjavaclub;

import java.util.Random;

public class Client {

    public static final Random RANDOM = new Random();

    public String fetchData() {
        if (trueForPercentage(50)) {
            throw new IllegalAccessError("Something went wrong. Try later!");
        }
        return "my-data";
    }

    public String tryNotToFail() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
        if (trueForPercentage(25)) {
            throw new IllegalAccessError("Something went wrong. Try later!");
        }
        return "I'm alive!";
    }

    public String callApi() {
        return "I've called the API!";
    }

    private boolean trueForPercentage(int percentage) {
        return RANDOM.nextInt(100) < percentage;
    }
}
