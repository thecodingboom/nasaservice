package api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NASAApiClientTest {

    @Mock
    private NASAApiClient client;

    @Test
    public void dateFormatTest(){
        when(client.determineDateFormat(anyString())).thenCallRealMethod();
            client.determineDateFormat("June 2, 2018");
    }
}
