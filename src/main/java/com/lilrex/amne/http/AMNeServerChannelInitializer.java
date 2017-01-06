package com.lilrex.amne.http;

import com.lilrex.amne.worker.IMsgSender;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class AMNeServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    private IMsgSender msgSender;

    public AMNeServerChannelInitializer(IMsgSender msgSender) {
        this.msgSender = msgSender;
    }

    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline p = socketChannel.pipeline();
        p.addLast(new HttpRequestDecoder());
        p.addLast(new HttpResponseEncoder());
        p.addLast(new EchoServerHandler(msgSender));
    }
}
