/*
 * Copyright 2019 Google LLC
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

import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.Gson;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.*;
import java.util.Optional;
import java.util.logging.Logger;

import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Logger.class, HelloBackgroundSample.class})
public class HelloBackgroundSampleTest {
  @Mock private static Logger loggerInstance;

  private HttpRequest request;
  private HttpResponse response;

  private ByteArrayOutputStream stdOut;
  private StringWriter responseOut;

  // Use GSON (https://github.com/google/gson) to parse JSON content.
  private Gson gson = new Gson();

  private EnvironmentVariables environmentVariables;

  @Before
  public void beforeTest() throws Exception {
    environmentVariables = new EnvironmentVariables();

    // Use a new mock for each test
    request = mock(HttpRequest.class);
    response = mock(HttpResponse.class);

    BufferedReader reader = new BufferedReader(new StringReader("{}"));
    when(request.getReader()).thenReturn(reader);

    responseOut = new StringWriter();
    BufferedWriter writer = new BufferedWriter(responseOut);
    when(response.getWriter()).thenReturn(writer);

    // Capture std out
    stdOut = new ByteArrayOutputStream();
    System.setOut(new PrintStream(stdOut));

    // Capture logs
    PowerMockito.mockStatic(Logger.class);
    if (loggerInstance == null) {
      loggerInstance = mock(Logger.class);
    }

    when(Logger.getLogger(anyString())).thenReturn(loggerInstance);
  }

  @After
  public void afterTest() {
    request = null;
    response = null;
    responseOut = null;
    stdOut = null;
    System.setOut(null);
    Mockito.reset();
  }

  @Test
  public void helloBackground_printsName() throws Exception {

    when(request.getFirstQueryParameter("name")).thenReturn(Optional.of("John"));

    new HelloBackgroundSample().accept(request, null);
    verify(loggerInstance, times(1)).info("Hello John!");
  }

  @Test
  public void helloBackground_printsHelloWorld() throws Exception {
    new HelloBackgroundSample().accept(request, null);

    verify(loggerInstance, times(1)).info("Hello world!");
  }
}
