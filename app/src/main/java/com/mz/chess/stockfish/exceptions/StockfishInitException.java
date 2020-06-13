/* Copyright 2018 David Cai Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mz.chess.stockfish.exceptions;

public class StockfishInitException extends Exception {
    public StockfishInitException() {
        super();
    }

    public StockfishInitException(String message) {
        super(message);
    }

    public StockfishInitException(String message, Throwable cause) {
        super(message, cause);
    }

    public StockfishInitException(Throwable cause) {
        super(cause);
    }
}
