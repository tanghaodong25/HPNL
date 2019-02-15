package com.intel.hpnl.pingpong;

import java.util.ArrayList;
import java.util.List;
import java.nio.ByteBuffer;

import com.intel.hpnl.core.EqService;
import com.intel.hpnl.core.CqService;
import com.intel.hpnl.core.Connection;

public class Server {
  public static void main(String args[]) {
    final int BUFFER_SIZE = 65536;
    final int BUFFER_NUM = 32;

    EqService eqService = new EqService("172.168.2.106", "123456", 3, BUFFER_NUM, true);
    CqService cqService = new CqService(eqService, eqService.getNativeHandle());

    List<Connection> conList = new ArrayList<Connection>();

    ConnectedCallback connectedCallback = new ConnectedCallback(conList, true);
    ReadCallback readCallback = new ReadCallback(true, eqService);
    eqService.setConnectedCallback(connectedCallback);
    eqService.setRecvCallback(readCallback);

    for (int i = 0; i < BUFFER_NUM*10; i++) {
      ByteBuffer sendBuf = ByteBuffer.allocateDirect(BUFFER_SIZE);
      eqService.setSendBuffer(sendBuf, BUFFER_SIZE, i);
    }

    for (int i = 0; i < BUFFER_NUM*2*10; i++) {
      ByteBuffer recvBuf = ByteBuffer.allocateDirect(BUFFER_SIZE);
      eqService.setRecvBuffer(recvBuf, BUFFER_SIZE, i);
    }

    cqService.start();
    eqService.start();

    cqService.join();
    eqService.shutdown();
    eqService.join();
  }
}