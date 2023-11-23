package com.imotorini.sbobinator9000;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.imotorini.sbobinator9000.services.DiscoveryService;

public class DiscoveryServiceTest {

    @Mock
    private WifiManager mockWifiManager;
    @Mock
    private Context mockContext;
    @Mock
    private WifiInfo mockWifiInfo;

    private DiscoveryService discoveryService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        discoveryService = new DiscoveryService() {
            @Override
            public Object getSystemService(String name) {
                if (Context.WIFI_SERVICE.equals(name)) {
                    return mockWifiManager;
                }
                return super.getSystemService(name);
            }
        };
        when(mockWifiManager.getConnectionInfo()).thenReturn(mockWifiInfo);
    }

    @Test
    public void testOnBind() {
        assertNull(discoveryService.onBind(null));
    }

    @Test
    public void testOnStartCommand() {
        int startId = 1;
        int flags = 0;
        Intent intent = mock(Intent.class);

        assertEquals(Service.START_NOT_STICKY, discoveryService.onStartCommand(intent, flags, startId));
    }

    @Test
    public void getCurrentSubnetBase_Success() {
        when(mockWifiInfo.getIpAddress()).thenReturn(67305985);
        assertEquals("1.2.3", discoveryService.getCurrentSubnetBase());
    }
}
