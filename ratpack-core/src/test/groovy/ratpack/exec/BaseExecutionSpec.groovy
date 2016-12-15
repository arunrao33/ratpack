/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ratpack.exec

import ratpack.func.Action
import ratpack.test.exec.ExecHarness
import spock.lang.AutoCleanup
import spock.lang.Specification

import java.util.concurrent.CountDownLatch

class BaseExecutionSpec extends Specification {

  @AutoCleanup
  ExecHarness execHarness = ExecHarness.harness()

  List<Object> events = []
  def latch = new CountDownLatch(1)

  def execAction(Action<? super Execution> action, Action<? super Throwable> onError = Action.noop()) {
    exec { it.onError(onError).start(action) }
  }

  def exec(Action<? super ExecStarter> exec) {
    def spec = execHarness.controller.fork()
      .onError { events << it }
      .onComplete { events << "complete"; latch.countDown() }

    exec.execute(spec)

    latch.await()
  }

}